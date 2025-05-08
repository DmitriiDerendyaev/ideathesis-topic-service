package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "topic_selections")
@Data
public class TopicSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selection_id")
    private Long id;

    @Column(name = "student_guid", nullable = false)
    private UUID studentGuid;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private GeneratedTopic topic;

    @Column(name = "selected_at", nullable = false, updatable = false)
    private LocalDateTime selectedAt = LocalDateTime.now();

    @Column(name = "topic_id", insertable = false, updatable = false)
    private Long topicId;
}
