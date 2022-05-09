package com.anillama.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@RestController
@Slf4j
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {
        log.info("getTicket => {}", id);
        return ticketService.getTicket(authorizationHeader, id);
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<Ticket>> findAllByProjectId(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long projectId) {
        log.info("findAllByProjectId => {}", projectId);
        return ticketService.findAllByProjectId(authorizationHeader, projectId);
    }

    @GetMapping("/byAssignee/{assigneeId}")
    public ResponseEntity<List<Ticket>> findAllByAssigneeId(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long assigneeId) {
        return ticketService.findAllByAssigneeId(authorizationHeader, assigneeId);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTicket(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody TicketRequest request) {
        return ticketService.createTicket(authorizationHeader, request);
    }

    @PutMapping
    public ResponseEntity<String> updateTicket(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody TicketRequest request) {
        return ticketService.updateTicket(authorizationHeader, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {
        return ticketService.deleteTicketById(authorizationHeader, id);
    }

    @DeleteMapping("/byProject/{projectId}")
    public ResponseEntity<String> deleteAllTicketsByProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long projectId) {
        return ticketService.deleteAllTicketsByProject(authorizationHeader, projectId);
    }

    @PostMapping("/uploadAttachment/{id}")
    public ResponseEntity<List<String>> uploadAttachments(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        log.info("uploadAttachments => {} {}", id);
        return ticketService.uploadAttachments(authorizationHeader, id, files);
    }

    @GetMapping("/downloadAttachment/{id}&{name}")
    public ResponseEntity<Resource> downloadAttachments(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id, @PathVariable String name, HttpServletRequest request) {
        log.info("downloadAttachments => {} {}", id, name);
        return ticketService.downloadAttachments(authorizationHeader, id, name, request);
    }
}
