package ru.derendyaev.ideathesis_topic_service.mapper;

import org.springframework.stereotype.Component;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicSkill;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSkillRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class TopicMapper {

    private final TopicSkillRepository topicSkillRepository;

    public TopicMapper(TopicSkillRepository topicSkillRepository) {
        this.topicSkillRepository = topicSkillRepository;
    }

    // Оригинальный метод, остаётся без изменений
    public GeneratedTopicDto toGeneratedTopicDto(GeneratedTopic topic) {
        List<TopicSkill> skills = topicSkillRepository.findByTopicId(topic.getId());
        String[] recommendedSkills = skills.stream()
                .filter(skill -> skill.getCompetency() != null)
                .map(topicSkill -> topicSkill.getCompetency().getCompetencyName())
                .toArray(String[]::new);

        return new GeneratedTopicDto(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getActuality(),
                topic.getProblems(),
                recommendedSkills
        );
    }

    // Новый метод с поддержкой статуса
    public GeneratedTopicDto toGeneratedTopicDtoWithStatus(GeneratedTopic topic) {
        List<TopicSkill> skills = topicSkillRepository.findByTopicId(topic.getId());
        String[] recommendedSkills = skills.stream()
                .filter(skill -> skill.getCompetency() != null)
                .map(topicSkill -> topicSkill.getCompetency().getCompetencyName())
                .toArray(String[]::new);

        return new GeneratedTopicDto(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getActuality(),
                topic.getProblems(),
                recommendedSkills,
                topic.getStatus()
        );
    }

    public List<GeneratedTopicDto> toGeneratedTopicDtoList(List<GeneratedTopic> topics) {
        return topics.stream()
                .map(this::toGeneratedTopicDtoWithStatus) // Используем новую версию для включения статуса
                .collect(Collectors.toList());
    }
}