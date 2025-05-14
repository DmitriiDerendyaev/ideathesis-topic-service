package ru.derendyaev.ideathesis_topic_service.dto.comment;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TopicCommentDto {
    private Long id;
    private Long topicId;
    private String authorType;
    private UUID authorGuid;
    private String commentText;
    private LocalDateTime createdAt;
    private Long parentCommentId;
}
