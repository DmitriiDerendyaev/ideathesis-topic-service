package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.derendyaev.ideathesis_topic_service.dto.changelog.TopicChangeLogDto;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicChangeLog;
import ru.derendyaev.ideathesis_topic_service.model.TopicSkill;
import ru.derendyaev.ideathesis_topic_service.repository.TopicChangeLogRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSkillRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicChangeLogService {

    private final TopicChangeLogRepository topicChangeLogRepository;
    private final TopicSkillRepository topicSkillRepository;

    @Transactional
    public void saveTopicSnapshot(GeneratedTopic topic, UUID studentGuid) {
        TopicChangeLog log = new TopicChangeLog();
        log.setTopicId(topic.getId());
        log.setStudentGuid(studentGuid);
        log.setTitle(topic.getTitle());
        log.setDescription(topic.getDescription());
        log.setActuality(topic.getActuality());
        log.setProblems(topic.getProblems());
        log.setStatus(topic.getStatus());
        log.setChangeTime(LocalDateTime.now());

        // Получаем и сохраняем навыки как строку
        List<TopicSkill> skills = topicSkillRepository.findByTopicId(topic.getId());
        String skillsString = skills.stream()
                .map(skill -> skill.getCompetency().getCompetencyName())
                .collect(Collectors.joining(","));
        log.setSkills(skillsString);

        topicChangeLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<TopicChangeLogDto> getTopicHistory(Long topicId) {
        return topicChangeLogRepository.findByTopicIdOrderByChangeTimeDesc(topicId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TopicChangeLogDto toDto(TopicChangeLog log) {
        TopicChangeLogDto dto = new TopicChangeLogDto();
        dto.setId(log.getId());
        dto.setTopicId(log.getTopicId());
        dto.setStudentGuid(log.getStudentGuid());
        dto.setTitle(log.getTitle());
        dto.setDescription(log.getDescription());
        dto.setActuality(log.getActuality());
        dto.setProblems(log.getProblems());
        dto.setStatus(log.getStatus());
        dto.setChangeTime(log.getChangeTime());
        dto.setSkills(log.getSkills() != null ? log.getSkills().split(",") : new String[]{});
        return dto;
    }
}
