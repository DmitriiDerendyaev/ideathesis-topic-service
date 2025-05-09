package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicSkill;

import java.util.List;

public interface TopicSkillRepository extends JpaRepository<TopicSkill, Long> {
    List<TopicSkill> findByTopicId(Long topicId);

    List<TopicSkill> findByTopicIdIn(List<Long> topicIds);
}