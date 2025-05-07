package ru.derendyaev.ideathesis_topic_service.utils;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.gigaChat.PromptConstants;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.Message;

import java.util.List;

import static ru.derendyaev.ideathesis_topic_service.gigaChat.models.GigaChatConstant.SYSTEM_ROLE;
import static ru.derendyaev.ideathesis_topic_service.gigaChat.models.GigaChatConstant.USER_ROLE;

@Component
public class PromptBuilder {

    public List<Message> buildMessages(GenerateTopicRequest request) {
        String systemPrompt = PromptConstants.SYSTEM_PROMPT;
        String userPrompt = "MASTER".equalsIgnoreCase(request.getEducationLevel())
                ? PromptConstants.getMasterPrompt(request.getCompetencies(), request.getAreaOfStudy())
                : PromptConstants.getBachelorPrompt(request.getCompetencies(), request.getAreaOfStudy());

        return List.of(
                new Message(SYSTEM_ROLE, systemPrompt),
                new Message(USER_ROLE, userPrompt)
        );
    }
}