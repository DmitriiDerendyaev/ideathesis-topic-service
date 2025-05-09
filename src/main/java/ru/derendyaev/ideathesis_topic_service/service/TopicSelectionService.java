package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;
import ru.derendyaev.ideathesis_topic_service.repository.GeneratedTopicRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSelectionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicSelectionService {

    private final GeneratedTopicRepository generatedTopicRepository;
    private final TopicSelectionRepository topicSelectionRepository;

    @Transactional
    public void selectTopic(UUID studentGuid, Long topicId) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + topicId + " не найдена"));

        TopicSelection selection = new TopicSelection();
        selection.setStudentGuid(studentGuid);
        selection.setTopic(topic);
        selection.setSelectedAt(LocalDateTime.now());
        topicSelectionRepository.save(selection);
    }
}