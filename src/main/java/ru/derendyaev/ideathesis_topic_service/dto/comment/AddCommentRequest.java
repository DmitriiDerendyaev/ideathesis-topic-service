package ru.derendyaev.ideathesis_topic_service.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {

    @NotBlank
    private String commentText;
    private Long parentCommentId;
}
