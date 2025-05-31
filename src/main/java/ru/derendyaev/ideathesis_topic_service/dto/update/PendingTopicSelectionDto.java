package ru.derendyaev.ideathesis_topic_service.dto.update;

import lombok.Data;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PendingTopicSelectionDto {
    private GeneratedTopicDto topic;
    private UUID studentGuid;
    private UUID supervisorGuid;
    private LocalDateTime createdAt;
}

