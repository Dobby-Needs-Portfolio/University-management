package deu.manager.executable.config.enums;

public enum UserType {
    Student("student"),
    AdminStaff("staff_admin"),
    ClassStaff("staff_class"),
    Professor("professor");

    private final String value;
    UserType(String user) { this.value = user; }
    public String getValue() { return value; }

    /**
     * find and returns the UserType that matches with value
     * @param value the value want to search
     * @return the searched ENUM object. null if nothing matches
     */
    public static UserType valueToUserType(String value) {
        for (UserType e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }

}
