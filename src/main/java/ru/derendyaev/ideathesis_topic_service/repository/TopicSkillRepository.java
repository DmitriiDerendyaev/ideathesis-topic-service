package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicSkill;

public interface TopicSkillRepository extends JpaRepository<TopicSkill, Long> {
}
