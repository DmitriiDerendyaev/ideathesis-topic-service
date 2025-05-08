package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_competencies")
@Data
public class RequestCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_competency_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private UserRequest request;

    @ManyToOne
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @TableGenerator(name = "request_competencies_unique", table = "request_competencies",
            pkColumnName = "request_id", valueColumnName = "competency_id")
    @Column(name = "request_id", insertable = false, updatable = false)
    private Long requestId;

    @Column(name = "competency_id", insertable = false, updatable = false)
    private Long competencyId;
}