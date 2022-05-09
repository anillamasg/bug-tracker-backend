package com.anillama.ticket.rabbitmq;

import com.anillama.ticket.TicketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class TicketConsumer {
    private final TicketService ticketService;

    @RabbitListener(queues = "${rabbitmq.queues.removeAllTicketsByProject}")
    public void removeAllTicketsByProject(Long projectId) {
        log.info("Consumed at removeAllTicketsByProject {} from queue", projectId);
        ticketService.removeAllTicketsByProject(projectId);
    }
}
