package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "topic_comments")
@Data
public class TopicComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private GeneratedTopic topic;

    @Column(name = "author_type", nullable = false)
    private String authorType; // "STUDENT" или "TEACHER"

    @Column(name = "author_guid", nullable = false)
    private UUID authorGuid;

    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private TopicComment parentComment; // Для вложенных ответов
}
