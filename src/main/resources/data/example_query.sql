SELECT gt.topic_id, gt.title, gt.description, gt.actuality, gt.problems,
       array_agg(c.competency_name) as recommended_skills
FROM generated_topics gt
LEFT JOIN topic_skills ts ON gt.topic_id = ts.topic_id
LEFT JOIN competencies c ON ts.competency_id = c.competency_id
WHERE gt.request_id = 1
GROUP BY gt.topic_id;

SELECT c.competency_name
FROM user_competencies uc
JOIN competencies c ON uc.competency_id = c.competency_id
WHERE uc.student_guid = '123e4567-e89b-12d3-a456-426614174000';

SELECT gt.title
FROM topic_selections ts
JOIN generated_topics gt ON ts.topic_id = gt.topic_id
WHERE ts.student_guid = '123e4567-e89b-12d3-a456-426614174000';

-- Запрос 1: Получение всех данных по одному пользователю
-- Возвращает компетенции, области применения, запросы, сгенерированные темы, рекомендованные навыки и выбранные темы
SELECT
    'competencies' AS data_type,
    array_agg(c.competency_name) AS data
FROM user_competencies uc
JOIN competencies c ON uc.competency_id = c.competency_id
WHERE uc.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type
UNION
SELECT
    'study_areas' AS data_type,
    array_agg(sa.area_name) AS data
FROM user_study_areas usa
JOIN study_areas sa ON usa.area_id = sa.area_id
WHERE usa.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type
UNION
SELECT
    'requests' AS data_type,
    array_agg(ur.request_id || ': ' || ur.request_text) AS data
FROM user_requests ur
WHERE ur.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type
UNION
SELECT
    'generated_topics' AS data_type,
    array_agg(gt.title) AS data
FROM generated_topics gt
JOIN user_requests ur ON gt.request_id = ur.request_id
WHERE ur.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type
UNION
SELECT
    'recommended_skills' AS data_type,
    array_agg(DISTINCT c.competency_name) AS data
FROM topic_skills ts
JOIN generated_topics gt ON ts.topic_id = gt.topic_id
JOIN user_requests ur ON gt.request_id = ur.request_id
JOIN competencies c ON ts.competency_id = c.competency_id
WHERE ur.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type
UNION
SELECT
    'selected_topics' AS data_type,
    array_agg(gt.title) AS data
FROM topic_selections ts
JOIN generated_topics gt ON ts.topic_id = gt.topic_id
WHERE ts.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY data_type;

-- Запрос 2: Получение данных по всем запросам
-- Возвращает информацию о запросах, включая пользователя, область применения, компетенции и текст запроса
SELECT
    ur.request_id,
    ur.student_guid,
    sa.area_name,
    ur.request_text,
    ur.request_time,
    array_agg(c.competency_name) AS competencies
FROM user_requests ur
JOIN study_areas sa ON ur.area_id = sa.area_id
LEFT JOIN request_competencies rc ON ur.request_id = rc.request_id
LEFT JOIN competencies c ON rc.competency_id = c.competency_id
GROUP BY ur.request_id, ur.student_guid, sa.area_name, ur.request_text, ur.request_time
ORDER BY ur.request_time DESC;

-- Запрос 3: Получение данных по ответам LLM (сгенерированные темы)
-- Возвращает все сгенерированные темы с их атрибутами и рекомендованными навыками
SELECT
    gt.topic_id,
    gt.request_id,
    ur.student_guid,
    gt.title,
    gt.description,
    gt.actuality,
    gt.problems,
    gt.created_at,
    array_agg(c.competency_name) AS recommended_skills
FROM generated_topics gt
JOIN user_requests ur ON gt.request_id = ur.request_id
LEFT JOIN topic_skills ts ON gt.topic_id = ts.topic_id
LEFT JOIN competencies c ON ts.competency_id = c.competency_id
GROUP BY gt.topic_id, gt.request_id, ur.student_guid, gt.title, gt.description, gt.actuality, gt.problems, gt.created_at
ORDER BY gt.created_at DESC;

-- Запрос 4: Получение данных по выбранным топикам
-- Возвращает информацию о темах, выбранных пользователями
SELECT
    ts.selection_id,
    ts.student_guid,
    gt.topic_id,
    gt.title,
    gt.description,
    ts.selected_at
FROM topic_selections ts
JOIN generated_topics gt ON ts.topic_id = gt.topic_id
ORDER BY ts.selected_at DESC;

-- Запрос 5: Получение данных о запросах, в которых использовались разные компетенции
-- Возвращает запросы и компетенции, использованные в каждом запросе
SELECT
    ur.request_id,
    ur.student_guid,
    sa.area_name,
    ur.request_text,
    array_agg(c.competency_name) AS used_competencies
FROM user_requests ur
JOIN study_areas sa ON ur.area_id = sa.area_id
JOIN request_competencies rc ON ur.request_id = rc.request_id
JOIN competencies c ON rc.competency_id = c.competency_id
GROUP BY ur.request_id, ur.student_guid, sa.area_name, ur.request_text
ORDER BY ur.request_id;

-- Запрос 6: Получение самых популярных компетенций
-- Возвращает компетенции, наиболее часто используемые в запросах и темах
SELECT
    c.competency_name,
    COUNT(DISTINCT rc.request_id) AS request_count,
    COUNT(DISTINCT ts.topic_id) AS topic_count
FROM competencies c
LEFT JOIN request_competencies rc ON c.competency_id = rc.competency_id
LEFT JOIN topic_skills ts ON c.competency_id = ts.competency_id
GROUP BY c.competency_name
HAVING COUNT(DISTINCT rc.request_id) > 0 OR COUNT(DISTINCT ts.topic_id) > 0
ORDER BY request_count DESC, topic_count DESC;

-- Запрос 7: Получение тем, соответствующих компетенциям пользователя
-- Возвращает темы, рекомендованные навыки которых пересекаются с компетенциями пользователя
SELECT
    gt.topic_id,
    gt.title,
    gt.description,
    array_agg(c.competency_name) AS matching_skills
FROM generated_topics gt
JOIN topic_skills ts ON gt.topic_id = ts.topic_id
JOIN user_competencies uc ON ts.competency_id = uc.competency_id
JOIN competencies c ON ts.competency_id = c.competency_id
WHERE uc.student_guid = '123e4567-e89b-12d3-a456-426614174000'
GROUP BY gt.topic_id, gt.title, gt.description
ORDER BY gt.topic_id;

-- Запрос 8: Получение активности пользователей
-- Возвращает количество запросов, сгенерированных тем и выбранных тем для каждого пользователя
SELECT
    ur.student_guid,
    COUNT(DISTINCT ur.request_id) AS request_count,
    COUNT(DISTINCT gt.topic_id) AS generated_topic_count,
    COUNT(DISTINCT ts.selection_id) AS selected_topic_count
FROM user_requests ur
LEFT JOIN generated_topics gt ON ur.request_id = gt.request_id
LEFT JOIN topic_selections ts ON ur.student_guid = ts.student_guid
GROUP BY ur.student_guid
ORDER BY request_count DESC;