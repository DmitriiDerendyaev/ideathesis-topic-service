package ru.derendyaev.ideathesis_topic_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;



@Entity
@Table(name = "user_requests")
@Data
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "student_guid", nullable = false)
    private UUID studentGuid;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private StudyArea area;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "request_text", nullable = false)
    private String requestText;
}