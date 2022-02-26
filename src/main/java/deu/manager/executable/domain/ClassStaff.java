package deu.manager.executable.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 수업 담당자의 데이터를 저장하는 도메인 클래스입니다.
 */

@Getter @Setter @Builder @AllArgsConstructor
@Table(name = "staff_class")
public class ClassStaff {

    /**
     *  수업 담당자 데이터베이스 고유 키 , 자동으로 생성됩니다. <br>
     *  DataBase type - Int(10)
     */
    @Setter(AccessLevel.NONE)
    Long id;

    /**
     *  수업 담당자 이름이 저장된 필드 <br>
     *  DataBase type -   VARCHAR(64)
     */
    String name;

    /**
     * 수업 담당자 번호가 저장된 필드<br>
     * Database type - SMALLINT(3)
     */
    Integer staffNum;

    /**
     * 수업 담당자 비밀번호가 저장된 필드<br>
     * Database type - VARCHAR(128)
     */
    String password;

    /**
     * 주민번호가 저장된 필드<br>
     * Database type - VARCHAR(32)
     */
    String residentNum;

}
