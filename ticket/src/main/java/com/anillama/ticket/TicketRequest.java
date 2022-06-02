package com.anillama.ticket;

import java.util.List;

public record TicketRequest(Long id, String title, Long assigneeId, String assignee, String assignedBy,
                            String createdBy, String dueDate,
                            String priority, String type, String status, TicketComment comment, String description,
                            Long projectId, String projectName, List<String> attachedFiles) {
}