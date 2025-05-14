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
import ru.derendyaev.ideathesis_topic_service.dto.StudentTopicSelectionDto;
import ru.derendyaev.ideathesis_topic_service.dto.TopicStatusUpdateRequest;
import ru.derendyaev.ideathesis_topic_service.dto.update.PendingTopicSelectionDto;
import ru.derendyaev.ideathesis_topic_service.dto.update.UpdateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.mapper.TopicMapper;
import ru.derendyaev.ideathesis_topic_service.model.*;
import ru.derendyaev.ideathesis_topic_service.repository.CompetencyRepository;
import ru.derendyaev.ideathesis_topic_service.repository.GeneratedTopicRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSelectionRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSkillRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TopicManagementService {

    private final GeneratedTopicRepository generatedTopicRepository;
    private final TopicSelectionRepository topicSelectionRepository;
    private final TopicSkillRepository topicSkillRepository;
    private final CompetencyRepository competencyRepository;
    private final TopicMapper topicMapper;
    private final TopicChangeLogService topicChangeLogService;

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
        topicChangeLogService.saveTopicSnapshot(topic, studentGuid);
    }

    @Transactional
    public void updateTopicStatus(Long topicId, TopicStatusUpdateRequest request, UUID studentGuid) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));
        topic.setStatus(request.getStatus());
        generatedTopicRepository.save(topic);
        topicChangeLogService.saveTopicSnapshot(topic, studentGuid);
    }

    @Transactional(readOnly = true)
    public Page<GeneratedTopicDto> getLastTenTopics(UUID studentGuid, Pageable pageable) {
        Page<GeneratedTopic> topicsPage = generatedTopicRepository.findByRequestStudentGuidOrderByCreatedAtDesc(studentGuid, pageable);
        return topicsPage.map(topicMapper::toGeneratedTopicDtoWithStatus);
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

    @Transactional(readOnly = true)
    public List<StudentTopicSelectionDto> getActiveTopicsForStudent(UUID studentGuid, TopicStatus status) {
        List<TopicSelection> selections = topicSelectionRepository.findByStudentGuid(studentGuid);
        return selections.stream()
                .filter(selection -> status == null || selection.getTopic().getStatus().equals(status))
                .filter(selection -> !selection.getTopic().getStatus().equals(TopicStatus.REJECTED))
                .map(selection -> {
                    StudentTopicSelectionDto dto = new StudentTopicSelectionDto();
                    dto.setTopic(topicMapper.toGeneratedTopicDtoWithStatus(selection.getTopic()));
                    dto.setSupervisorGuid(selection.getSupervisorGuid());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void withdrawTopic(Long topicId, UUID studentGuid) {
        TopicSelection selection = topicSelectionRepository.findByStudentGuidAndTopicId(studentGuid, topicId)
                .orElseThrow(() -> new IllegalArgumentException("TopicSelection not found"));
        GeneratedTopic topic = selection.getTopic();
        if (topic.getStatus() == TopicStatus.PENDING) {
            topic.setStatus(TopicStatus.REJECTED);
            generatedTopicRepository.save(topic);
            topicChangeLogService.saveTopicSnapshot(topic, studentGuid);
        } else {
            throw new IllegalStateException("Cannot withdraw topic with status: " + topic.getStatus());
        }
    }

    @Transactional(readOnly = true)
    public GeneratedTopicDto getTopic(Long topicId) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));
        return topicMapper.toGeneratedTopicDtoWithStatus(topic);
    }

    @Transactional
    public void updateTopic(Long topicId, UUID studentGuid, UpdateTopicRequest request) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));

        // Проверка права на редактирование
        TopicSelection selection = topicSelectionRepository.findByStudentGuidAndTopicId(studentGuid, topicId)
                .orElseThrow(() -> new IllegalStateException("Вы не выбрали эту тему"));

        // Проверка статуса
        if (!topic.getStatus().equals(TopicStatus.NEEDS_REVISION)) {
            throw new IllegalStateException("Тема не находится в статусе NEEDS_REVISION");
        }

        // Обновление полей
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setActuality(request.getActuality());
        topic.setProblems(request.getProblems());

        // Обновление навыков
        topicSkillRepository.deleteByTopicId(topicId); // Теперь метод существует
        for (String skillName : request.getRecommendedSkills()) {
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

        generatedTopicRepository.save(topic);
        topicChangeLogService.saveTopicSnapshot(topic, studentGuid);
    }

    @Transactional(readOnly = true)
    public Page<GeneratedTopicDto> getGeneratedTopics(UUID studentGuid, Pageable pageable) {
        Page<GeneratedTopic> topicsPage = generatedTopicRepository.findByRequestStudentGuid(studentGuid, pageable);
        return topicsPage.map(topicMapper::toGeneratedTopicDtoWithStatus);
    }

    // Обновлено: использование PendingTopicSelectionDto
    @Transactional(readOnly = true)
    public List<PendingTopicSelectionDto> getPendingTopicsForTeacher(UUID teacherGuid) {
        List<TopicSelection> selections = topicSelectionRepository.findBySupervisorGuidAndTopicStatusIn(
                teacherGuid, List.of(TopicStatus.PENDING, TopicStatus.NEEDS_REVISION));
        return selections.stream()
                .map(selection -> {
                    PendingTopicSelectionDto dto = new PendingTopicSelectionDto();
                    dto.setTopic(topicMapper.toGeneratedTopicDtoWithStatus(selection.getTopic()));
                    dto.setStudentGuid(selection.getStudentGuid());
                    dto.setSupervisorGuid(selection.getSupervisorGuid());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}