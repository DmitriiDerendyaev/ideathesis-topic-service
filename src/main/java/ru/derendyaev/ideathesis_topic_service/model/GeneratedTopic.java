package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "generated_topics")
@Data
public class GeneratedTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private UserRequest request;

    @Column(name = "title", nullable = false, length = 2048)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "actuality", nullable = false, columnDefinition = "TEXT")
    private String actuality;

    @Column(name = "problems", nullable = false, columnDefinition = "TEXT")
    private String problems;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TopicStatus status = TopicStatus.DRAFT; // По умолчанию DRAFT
}

