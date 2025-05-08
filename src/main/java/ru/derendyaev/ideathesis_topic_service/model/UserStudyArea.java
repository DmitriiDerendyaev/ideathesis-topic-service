package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_study_areas")
@Data
public class UserStudyArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_study_area_id")
    private Long id;

    @Column(name = "student_guid", nullable = false)
    private UUID studentGuid;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private StudyArea area;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "area_id", insertable = false, updatable = false)
    private Long areaId;
}