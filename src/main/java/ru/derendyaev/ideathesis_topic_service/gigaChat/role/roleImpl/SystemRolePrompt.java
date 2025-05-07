package ru.derendyaev.ideathesis_topic_service.gigaChat.role.roleImpl;


import ru.derendyaev.ideathesis_topic_service.gigaChat.role.RolePrompt;

public class SystemRolePrompt extends RolePrompt {

    public SystemRolePrompt() {
        this.context = "Ты отвечаешь как эксперт в области образования и научной работы, с глубокими знаниями в современной науки и технологиях.";
    }

    public String getRolePrompt() {
        return this.context;
    }
}
