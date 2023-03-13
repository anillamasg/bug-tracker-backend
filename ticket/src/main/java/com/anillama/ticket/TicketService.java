package com.anillama.ticket;

import com.anillama.amqp.RabbitMQMessageProducer;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionValidateService;
import com.anillama.clients.userproject.UserProjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.anillama.clients.application.ApplicationUtil.*;

@Slf4j
@Service
public class TicketService {
    private final String uploadDirectory = "attachments/";

    private final TicketRepository ticketRepository;
    private final ApplicationUserSessionValidateService sessionValidateService;
    private final RabbitMQMessageProducer messageProducer;

    public TicketService(TicketRepository ticketRepository, ApplicationUserSessionValidateService sessionValidateService, RabbitMQMessageProducer messageProducer) {
        this.ticketRepository = ticketRepository;
        this.sessionValidateService = sessionValidateService;
        this.messageProducer = messageProducer;
    }

    public ResponseEntity<Long> createTicket(String authorizationHeader, TicketRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), request.projectId()), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, CREATION), HttpStatus.UNAUTHORIZED);

            Ticket ticket = Ticket.builder()
                    .title(request.title())
                    .description(request.description())
                    .assigneeId(request.assigneeId())
                    .assignee(request.assignee())
                    .assignedBy(request.assignedBy())
                    .createdDate(LocalDate.now())
                    .createdBy(request.createdBy())
                    .dueDate(LocalDate.parse(request.dueDate()))
                    .priority(request.priority())
                    .status(request.status())
                    .type((request.type()))
                    .fileNames(request.attachedFiles())
                    .projectId(request.projectId())
                    .projectName(request.projectName())
                    .build();
            ticket = ticketRepository.save(ticket);

            Path sourceFilePath = Paths.get(uploadDirectory + "0/");
            Path targetFilePath = Paths.get(uploadDirectory + ticket.getId() + "/");

            try {
                Files.move(sourceFilePath, targetFilePath);
            } catch (FileAlreadyExistsException ex) {
                log.info("Target file already exists");
            } catch (IOException ex) {
                log.info("I/O error: {}", ex);
            }

            return ResponseEntity.ok(ticket.getId());
        }
        return new ResponseEntity(invalidUserFailed(TICKET, CREATION), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Ticket> getTicket(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Ticket ticket = ticketRepository.getTicketById(id);
            if (ticket == null)
                return new ResponseEntity(serviceDoesNotExist(TICKET, RETRIEVAL), HttpStatus.BAD_REQUEST);
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), ticket.getProjectId()), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, RETRIEVAL), HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(ticket);
        }
        return new ResponseEntity(invalidUserFailed(TICKET, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<Ticket>> findAllByProjectId(String authorizationHeader, Long projectId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), projectId), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, RETRIEVAL), HttpStatus.UNAUTHORIZED);
            List<Ticket> tickets = ticketRepository.findAllByProjectId(projectId);
            return ResponseEntity.ok(tickets);
        }
        return new ResponseEntity(invalidUserFailed(TICKET, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<Ticket>> findAllByAssigneeId(String authorizationHeader, Long assigneeId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            List<Ticket> tickets = ticketRepository.findAllByAssigneeId(assigneeId);
            List<Long> accessibleProjectIds = messageProducer.publishAndReceiveList(response.userId(), INTERNAL_EXCHANGE, INTERNAL_GET_ALL_PROJECTS_FOR_USER_ROUTING_KEY);
            tickets = tickets.stream().filter(ticket -> accessibleProjectIds.contains(ticket.getProjectId())).collect(Collectors.toList());
            return ResponseEntity.ok(tickets);
        }
        return new ResponseEntity(invalidUserFailed(TICKET, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<String> deleteTicketById(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);

            Ticket ticket = ticketRepository.getTicketById(id);
            if (ticket == null)
                return new ResponseEntity(serviceDoesNotExist(TICKET, DELETION), HttpStatus.BAD_REQUEST);
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), ticket.getProjectId()), INTERNAL_EXCHANGE, "internal.checkUserProject.routing-key");
            if (check.equals(INVALID)) {
                return new ResponseEntity(unauthorizedUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);
            }
            ticketRepository.deleteTicketById(id);
            return ResponseEntity.ok(serviceSuccessful(TICKET, DELETION));
        }
        return new ResponseEntity(invalidUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<String> deleteAllTicketsByProject(String authorizationHeader, Long projectId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);

            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), projectId), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(unauthorizedUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);

            Integer deleteStatus = removeAllTicketsByProject(projectId);
            if (deleteStatus.equals(0))
                return new ResponseEntity(serviceDoesNotExist(TICKET, DELETION), HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(serviceSuccessful(TICKET, DELETION));
        }
        return new ResponseEntity(invalidUserFailed(TICKET, DELETION), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public Integer removeAllTicketsByProject(Long projectId) {
        return ticketRepository.deleteAllByProjectId(projectId);
    }

    public ResponseEntity<String> updateTicket(String authorizationHeader, TicketRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), request.projectId()), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID)) {
                return new ResponseEntity(unauthorizedUserFailed(TICKET, UPDATE), HttpStatus.UNAUTHORIZED);
            }
            Ticket savedTicket = ticketRepository.getTicketById(request.id());
            if (savedTicket == null)
                return new ResponseEntity(serviceDoesNotExist(TICKET, UPDATE), HttpStatus.BAD_REQUEST);
            savedTicket.setTitle(request.title());
            savedTicket.setDescription(request.description());
            savedTicket.setAssigneeId(request.assigneeId());
            savedTicket.setAssignee(request.assignee());
            savedTicket.setAssignedBy(request.assignedBy());
            savedTicket.setDueDate(LocalDate.parse(request.dueDate()));
            savedTicket.setPriority(request.priority());
            savedTicket.setStatus(request.status());
            savedTicket.setType(request.type());

            TicketComment newComment = request.comment();
            TicketCommentAction action = newComment.getAction();
            action.setComment(newComment);
            newComment.setAction(action);
            newComment.setTicket(savedTicket);

            List<TicketComment> newComments = savedTicket.getComments();
            newComments.add(newComment);
            savedTicket.setComments(newComments);

            savedTicket.setProjectId(request.projectId());
            savedTicket.setProjectName(request.projectName());
            ticketRepository.save(savedTicket);
            return ResponseEntity.ok(serviceSuccessful(TICKET, UPDATE));
        }
        return new ResponseEntity(invalidUserFailed(TICKET, UPDATE), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<List<String>> uploadAttachments(String authorizationHeader, Long ticketId, MultipartFile[] attachments) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            List<String> fileNames = new ArrayList<>();
            String currentUploadDir = uploadDirectory + ticketId + "/";
            try {
                Files.createDirectories(Paths.get(currentUploadDir));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (MultipartFile attachment : attachments) {
                String fileName = attachment.getOriginalFilename().replaceAll("\\s+", "_");
                Path path = Paths.get(currentUploadDir, fileName);
                fileNames.add(fileName);
                try {
                    Files.write(path, attachment.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(fileNames);
        }
        return new ResponseEntity("Invalid user. Attachments upload failed.", HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Resource> downloadAttachments(String authorizationHeader, Long ticketId, String name, HttpServletRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            String dir = uploadDirectory + ticketId + "/" + name.replaceAll("\\s+", "_");
            Path path = Paths.get(dir);
            ByteArrayResource resource;
            try {
                resource = new ByteArrayResource(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity("Attachment does not exist. Attachments download failed.", HttpStatus.BAD_REQUEST);
            }

            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.info("Could not determine file type.");
            }

            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return new ResponseEntity("Invalid user. Attachments download failed.", HttpStatus.UNAUTHORIZED);
    }
}