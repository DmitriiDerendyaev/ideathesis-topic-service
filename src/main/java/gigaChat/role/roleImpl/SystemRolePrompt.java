package gigaChat.role.roleImpl;


import gigaChat.role.RolePrompt;

public class SystemRolePrompt extends RolePrompt {

    public SystemRolePrompt() {
        this.context = "Ты отвечаешь как эксперт в области образования и научной работы, с глубокими знаниями в современных технологиях.";
    }

    public String getRolePrompt() {
        return this.context;
    }
}
