package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.exceptions.GigaChatException;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageRequest;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageResponse;
import ru.derendyaev.ideathesis_topic_service.restUtils.Client;
import ru.derendyaev.ideathesis_topic_service.utils.PromptBuilder;
import ru.derendyaev.ideathesis_topic_service.utils.TopicParser;

import static ru.derendyaev.ideathesis_topic_service.gigaChat.models.GigaChatConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicGenerationService {

    private final Client gigaChatClient;
    private final PromptBuilder promptBuilder;
    private final TopicParser topicParser;

    public GenerateTopicResponse generateTopics(GenerateTopicRequest request) {
        GigaMessageRequest gigaRequest = new GigaMessageRequest(
                GIGA_CHAT_MODEL,
                false,
                0,
                promptBuilder.buildMessages(request),
                1,
                1024,
                1.0
        );

        GigaMessageResponse response;
        try {
            response = gigaChatClient.gigaMessageGenerate(gigaRequest);
        } catch (Exception e) {
            log.error("Ошибка при вызове GigaChat: ", e);
            throw new GigaChatException("Не удалось получить ответ от GigaChat", e);
        }

        String rawAnswer = response.getChoices().get(0).getMessage().getContent();
        GenerateTopicResponse result = new GenerateTopicResponse();
        result.setTopics(topicParser.parseTopics(rawAnswer));
        return result;
    }
}