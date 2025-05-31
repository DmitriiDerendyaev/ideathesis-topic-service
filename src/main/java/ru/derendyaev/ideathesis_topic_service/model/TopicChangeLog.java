package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "topic_change_log")
@Data
public class TopicChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "student_guid", nullable = false)
    private UUID studentGuid;

    @Column(name = "title", nullable = false, length = 2048)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "actuality", nullable = false, columnDefinition = "TEXT")
    private String actuality;

    @Column(name = "problems", nullable = false, columnDefinition = "TEXT")
    private String problems;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TopicStatus status;

    @Column(name = "change_time", nullable = false)
    private LocalDateTime changeTime;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;
}
