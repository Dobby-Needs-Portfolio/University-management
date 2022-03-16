package deu.manager.executable.config.enums;

public enum Roles {
    USER("ROLES_USER"),
    ADMIN("ROLES_ADMIN");

    private final String value;
    Roles(String roles) { this.value = roles; }
    public String getValue() { return value; }
}
