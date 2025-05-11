package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.UserRequest;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
}
