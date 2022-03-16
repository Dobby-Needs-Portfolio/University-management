package deu.manager.executable.config.enums;

public enum Roles {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;
    Roles(String roles) { this.value = roles; }
    public String getValue() { return value; }
    /**
     * find and returns the Roles that matches with value
     * @param value the value want to search
     * @return the searched ENUM object. null if nothing matches
     */
    public static Roles valueToRoles(String value) {
        for (Roles e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
