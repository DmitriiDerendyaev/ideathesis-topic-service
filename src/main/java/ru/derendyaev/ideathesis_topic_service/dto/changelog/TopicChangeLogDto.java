package ru.derendyaev.ideathesis_topic_service.dto.changelog;

import lombok.Data;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TopicChangeLogDto {
    private Long id;
    private Long topicId;
    private UUID studentGuid;
    private String title;
    private String description;
    private String actuality;
    private String problems;
    private TopicStatus status;
    private LocalDateTime changeTime;
    private String[] skills;
}