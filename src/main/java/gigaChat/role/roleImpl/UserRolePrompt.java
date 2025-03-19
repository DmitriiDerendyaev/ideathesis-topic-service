package ru.derendyaev.mospolytech.gigaChat.role.roleImpl;

import ru.derendyaev.mospolytech.gigaChat.role.RolePrompt;
import ru.derendyaev.mospolytech.gigaChat.role.RolePromptAction;

public class UserRolePrompt extends RolePrompt implements RolePromptAction {

    private final String competencies;
    private final String areaOfStudy;

    public UserRolePrompt(String competencies, String areaOfStudy) {
        this.competencies = competencies;
        this.areaOfStudy = areaOfStudy;
    }

    @Override
    public String getBachelorRolePrompt() {
        return "Помоги студенту бакалавриата выбрать тему для дипломной работы на основе его компетенций и направления научной деятельности. " +
                "Рекомендованные темы должны быть ориентированы на практическую реализацию и включать конкретные аспекты разработки. " +
                "Предложи три темы для диплома, каждая из которых должна начинаться со слова «Разработка», содержать краткое описание и рекомендации по необходимым навыкам и технологиям для реализации.\n\n" +
                "Данные о студенте:\n" +
                "Компетенции: " + this.competencies + "\n" +
                "Предметная область: " + this.areaOfStudy;
    }

    @Override
    public String getMasterRolePrompt() {
        return "Помоги студенту магистратуры выбрать тему для дипломной работы на основе его компетенций и направления научной деятельности. " +
                "Рекомендованные темы должны быть ориентированы на исследовательскую деятельность и анализ сложных вопросов в выбранной области. " +
                "Предложи три темы для диплома, каждая из которых должна начинаться со слова «Исследование», включать краткое описание и рекомендации по необходимым навыкам и методам исследования.\n\n" +
                "Данные о студенте:\n" +
                "Компетенции: " + this.competencies + "\n" +
                "Предметная область: " + this.areaOfStudy;
    }
}