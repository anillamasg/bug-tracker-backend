package com.anillama.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket getTicketById(Long id);

    List<Ticket> findAllByProjectId(Long projectId);

    List<Ticket> findAllByAssigneeId(Long assigneeId);

    Integer deleteTicketById(Long id);

    Integer deleteAllByProjectId(Long projectId);
}
