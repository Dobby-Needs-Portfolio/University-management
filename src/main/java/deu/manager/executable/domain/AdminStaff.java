package deu.manager.executable.domain;


import lombok.*;

import javax.persistence.*;

/**
 * 관리자 데이터를 저장하는 도메인 클래스입니다.
 */

@Getter @Setter @Builder @AllArgsConstructor
@Table(name = "staff_admin")
public class AdminStaff{
    public AdminStaff() {}

    /**
     * 관리자 데이터베이스 고유 키. 자동으로 생성됩니다.<br>
     * Database type - INT(10)
     */
    @Setter(AccessLevel.NONE)
    Long id;

    /**
     * 관리자 이름이 저장된 필드<br>
     * Database type - VARCHAR(64)
     */
    String name;

    /**
     * 관리자 번호가 저장된 필드<br>
     * Database type - SMALLINT(3)
     */
    Integer staffNum;

    /**
     * 관리자 비밀번호가 저장된 필드<br>
     * Database type - VARCHAR(128)
     */
    String password;

    /**
     * 주민번호가 저장된 필드<br>
     * Database type - VARCHAR(32)
     */
    String residentNum;
}
