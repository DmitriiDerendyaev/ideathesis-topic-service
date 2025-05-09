package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;
import ru.derendyaev.ideathesis_topic_service.dto.TopicStatusUpdateRequest;
import ru.derendyaev.ideathesis_topic_service.mapper.TopicMapper;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;
import ru.derendyaev.ideathesis_topic_service.repository.GeneratedTopicRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSelectionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicManagementService {

    private final GeneratedTopicRepository generatedTopicRepository;
    private final TopicSelectionRepository topicSelectionRepository;
    private final TopicMapper topicMapper;

    @Transactional
    public void selectTopic(UUID studentGuid, Long topicId, UUID supervisorGuid) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));

        // Проверка, что тема ещё не выбрана другим студентом или не в статусе, который запрещает выбор
        List<TopicSelection> activeSelections = topicSelectionRepository.findByTopicIdAndStudentGuidNot(topicId, studentGuid);
        if (!activeSelections.isEmpty()) {
            throw new IllegalStateException("Тема уже выбрана другим студентом.");
        }

        // Проверка на активные темы у текущего студента
        List<GeneratedTopic> activeTopics = generatedTopicRepository.findByRequestStudentGuidAndStatusNotIn(
                studentGuid, List.of(TopicStatus.DRAFT, TopicStatus.REJECTED));
        if (!activeTopics.isEmpty()) {
            throw new IllegalStateException("У вас уже есть активная тема. Завершите текущий процесс перед выбором новой.");
        }

        // Создание записи о выборе темы
        TopicSelection selection = new TopicSelection();
        selection.setStudentGuid(studentGuid);
        selection.setTopic(topic);
        selection.setSupervisorGuid(supervisorGuid);
        topicSelectionRepository.save(selection);

        // Обновление статуса темы
        topic.setStatus(TopicStatus.PENDING);
        generatedTopicRepository.save(topic);
    }

    @Transactional
    public void updateTopicStatus(Long topicId, TopicStatusUpdateRequest request) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));
        topic.setStatus(request.getStatus());
        generatedTopicRepository.save(topic);
    }

    @Transactional(readOnly = true)
    public GenerateTopicResponse getLastTenTopics(UUID studentGuid) {
        Pageable topTen = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<GeneratedTopic> topicsPage = generatedTopicRepository.findByRequestStudentGuidOrderByCreatedAtDesc(studentGuid, topTen);
        List<GeneratedTopic> topics = topicsPage.getContent();
        GenerateTopicResponse response = new GenerateTopicResponse();
        response.setTopics(topicMapper.toGeneratedTopicDtoList(topics));
        return response;
    }
}