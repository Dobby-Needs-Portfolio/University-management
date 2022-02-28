package deu.manager.executable.domain;


import deu.manager.executable.config.LazyFetcher;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * 교수 정보를 저장하는 도메인 클래스입니다.
 */

@Getter @Setter @Builder
public class Professor {
    /**
     * 교수 테이블 고유 키. 자동으로 생성됩니다.<br>
     * Database type - INT(10)
     */
    @Setter(AccessLevel.NONE)
    Long id;

    /**
     * 교수 이름이 저장된 필드<br>
     * Database type - VARCHAR(64)
     */
    String name;

    /**
     * 교수 번호가 저장된 필드<br>
     * Database type - SMALLINT(3)
     */
    int professorNum;

    /**
     * 교수 비밀번호가 저장된 필드<br>
     * Database type - VARCHAR(128)
     */
    String password;

    /**
     * 교수 주민번호가 저장된 필드<br>
     * Database type - VARCHAR(32)
     */
    String residentNum;

    /**
     * 교수의 학과가 저장된 필드. 학과 데이터베이스와 연결됩니다.<br>
     * Database type - INT(10)
     */
    Major major;

    /**
     * 교수가 강의하는 강의의 담당 목록입니다. LazyFetcher에 저장됩니다.
     * @see LazyFetcher
     */
    LazyFetcher<Long, List<Lecture>> lectures;
}
