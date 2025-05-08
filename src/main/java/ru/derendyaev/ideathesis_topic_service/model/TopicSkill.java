package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic_skills")
@Data
public class TopicSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_skill_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private GeneratedTopic topic;

    @ManyToOne
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @TableGenerator(name = "topic_skills_unique", table = "topic_skills",
            pkColumnName = "topic_id", valueColumnName = "competency_id")
    @Column(name = "topic_id", insertable = false, updatable = false)
    private Long topicId;

    @Column(name = "competency_id", insertable = false, updatable = false)
    private Long competencyId;
}
