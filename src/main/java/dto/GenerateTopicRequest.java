package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateTopicRequest {

    @NotBlank
    private String competencies;

    @NotBlank
    private String areaOfStudy;

    /**
     * Уровень обучения: "BACHELOR", "MASTER", и т.д.
     */
    @NotBlank
    private String educationLevel;
}