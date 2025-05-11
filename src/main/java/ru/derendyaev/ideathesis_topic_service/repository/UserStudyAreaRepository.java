package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.UserStudyArea;

public interface UserStudyAreaRepository extends JpaRepository<UserStudyArea, Long> {
}
