package deu.manager.executable.domain;

import deu.manager.executable.config.LazyFetcher;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 학생 정보를 저장하는 도메인 클래스입니다.
 */

@Getter @Setter @Builder @AllArgsConstructor
public class Student {
    /**
     * 학생 테이블 고유 키. 자동으로 생성됩니다.<br>
     * Database type - INT(10)
     */
    @Setter(AccessLevel.NONE)
    Long id;

    /**
     * 학생 이름이 저장된 필드<br>
     * Database type - VARCHAR(64)
     */
    String name;

    /**
     * 학생 번호가 저장된 필드<br>
     * Database type - SMALLINT(3) UNSIGNED
     */
    Integer studentNum;

    /**
     * 로그인에 필요한 비밀번호가 저장된 필드<br>
     * Database type - VARCHAR(128)
     */
    String password;

    /**
     * 학생의 주민번호가 저장된 필드<br>
     * Database type - VARCHAR(32)
     */
    String residentNum;

    /**
     * 학생의 학과가 저장된 필드. 학과 데이터베이스와 연결됩니다.<br>
     * Database type - INT(10)
     */
    Major major;

    /**
     * 학생이 수강신청한 강의 리스트입니다. 강의 리스트, 강의 수강생 리스트와 연결됩니다.<br>
     * Schema - student <- lecture_listener -> lecture
     */
    LazyFetcher<Long, List<Lecture>> lectureList;
}
