package io.orangewest.ailostproperty.pojo.enums;

public enum ChatRole {

    USER("user"),
    ASSISTANT("assistant"),

    ;

    private final String role;

    ChatRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
