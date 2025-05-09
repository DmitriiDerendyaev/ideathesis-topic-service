package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;
import ru.derendyaev.ideathesis_topic_service.exceptions.GigaChatException;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageRequest;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageResponse;
import ru.derendyaev.ideathesis_topic_service.model.*;
import ru.derendyaev.ideathesis_topic_service.repository.*;
import ru.derendyaev.ideathesis_topic_service.restUtils.GigaChatClient;
import ru.derendyaev.ideathesis_topic_service.utils.PromptBuilder;
import ru.derendyaev.ideathesis_topic_service.utils.TopicParser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicGenerationService {

    private static final String GIGA_CHAT_MODEL = "GigaChat";

    private final GigaChatClient gigaChatClient;
    private final PromptBuilder promptBuilder;
    private final TopicParser topicParser;
    private final CompetencyRepository competencyRepository;
    private final StudyAreaRepository studyAreaRepository;
    private final UserRequestRepository userRequestRepository;
    private final RequestCompetencyRepository requestCompetencyRepository;
    private final GeneratedTopicRepository generatedTopicRepository;
    private final TopicSkillRepository topicSkillRepository;

    @Transactional
    public GenerateTopicResponse generateAndSaveTopics(UUID studentGuid, GenerateTopicRequest request) {
        // Парсинг компетенций
        List<String> competencyNames = Arrays.stream(request.getCompetencies().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // Проверка или создание области применения
        StudyArea studyArea = studyAreaRepository.findByAreaName(request.getAreaOfStudy())
                .orElseGet(() -> {
                    StudyArea newArea = new StudyArea();
                    newArea.setAreaName(request.getAreaOfStudy());
                    newArea.setDescription("Область применения: " + request.getAreaOfStudy());
                    return studyAreaRepository.save(newArea);
                });

        // Создание запроса пользователя
        UserRequest userRequest = new UserRequest();
        userRequest.setStudentGuid(studentGuid);
        userRequest.setArea(studyArea);
        userRequest.setRequestTime(LocalDateTime.now());
        userRequest.setRequestText(request.toString());
        userRequest = userRequestRepository.save(userRequest);

        // Сохранение компетенций запроса
        for (String competencyName : competencyNames) {
            Competency competency = competencyRepository.findByCompetencyName(competencyName)
                    .orElseGet(() -> {
                        Competency newCompetency = new Competency();
                        newCompetency.setCompetencyName(competencyName);
                        newCompetency.setDescription("Компетенция: " + competencyName);
                        return competencyRepository.save(newCompetency);
                    });

            RequestCompetency requestCompetency = new RequestCompetency();
            requestCompetency.setRequest(userRequest);
            requestCompetency.setCompetency(competency);
            requestCompetency.setCreatedAt(LocalDateTime.now());
            requestCompetencyRepository.save(requestCompetency);
        }

        // Генерация тем через GigaChat
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

        // Сохранение сгенерированных тем
        for (GeneratedTopicDto topicDto : result.getTopics()) {
            GeneratedTopic topic = new GeneratedTopic();
            topic.setRequest(userRequest);
            topic.setTitle(topicDto.getTitle());
            topic.setDescription(topicDto.getDescription());
            topic.setActuality(topicDto.getActuality());
            topic.setProblems(topicDto.getProblems());
            topic.setCreatedAt(LocalDateTime.now());
            topic.setStatus(TopicStatus.DRAFT); // Устанавливаем статус DRAFT
            topic = generatedTopicRepository.save(topic);

            // Устанавливаем ID в DTO после сохранения
            //TODO: Посмотреть что ID приходит
            topicDto.setId(topic.getId());

            // Сохранение рекомендованных навыков
            for (String skillName : topicDto.getRecommendedSkills()) {
                Competency skill = competencyRepository.findByCompetencyName(skillName)
                        .orElseGet(() -> {
                            Competency newSkill = new Competency();
                            newSkill.setCompetencyName(skillName);
                            newSkill.setDescription("Рекомендованный навык: " + skillName);
                            return competencyRepository.save(newSkill);
                        });

                TopicSkill topicSkill = new TopicSkill();
                topicSkill.setTopic(topic);
                topicSkill.setCompetency(skill);
                topicSkill.setCreatedAt(LocalDateTime.now());
                topicSkillRepository.save(topicSkill);
            }
        }

        return result;
    }
}