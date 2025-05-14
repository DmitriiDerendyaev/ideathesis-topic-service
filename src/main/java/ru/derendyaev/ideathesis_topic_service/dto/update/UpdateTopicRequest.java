package ru.derendyaev.ideathesis_topic_service.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTopicRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String actuality;
    @NotBlank
    private String problems;
    private String[] recommendedSkills;
}
