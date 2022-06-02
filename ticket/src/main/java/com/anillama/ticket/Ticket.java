package com.anillama.ticket;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ticket")
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
    private LocalDate createdDate;
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
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketComment> comments = new ArrayList<>();
    private String description;
    @ElementCollection
    private List<String> fileNames;
    @Column(nullable = false)
    private Long assigneeId;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private String projectName;

    public void addComment(TicketComment ticketComment){
        this.getComments().add(ticketComment);
    }
}

