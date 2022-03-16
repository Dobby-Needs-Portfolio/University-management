package deu.manager.executable.config.enums;

public enum Tables {
    AdminStaff("staff_admin"),
    ClassStaff("staff_class"),
    Professor("professor"),
    Student("student"),
    Major("major"),
    LectureListener("lecture_listener"),
    Lecture("lecture");

    private final String value;
    Tables(String tableName) { this.value = tableName; }
    public String getValue() { return value; }
}
