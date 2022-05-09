package com.anillama.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticket_id_sequence", sequenceName = "ticket_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_id_sequence")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String assignedBy;
    @Column(nullable = false)
    private String assignee;
    @Column(nullable = false)
    private LocalDate createDate;
    @Column(nullable = false)
    private String createdBy;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(nullable = false)
    private String priority;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String status;
    @ElementCollection
    private List<String> comments;
    private String description;
    @ElementCollection
    private List<String> fileNames;
    @Column(nullable = false)
    private Long assigneeId;
    @Column(nullable = false)
    private Long projectId;
}

