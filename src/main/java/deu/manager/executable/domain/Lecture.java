package deu.manager.executable.domain;


import deu.manager.executable.config.LazyFetcher;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 강좌 정보를 저장하는 도메인 클래스입니다.
 */

@Getter @Setter @Builder
public class Lecture {
    /**
     * 강좌 테이블 고유 키. 자동으로 생성됩니다.<br>
     * Database type - INT(10)
     */
    Long id;

    /**
     * 강좌 번호가 저장된 필드<br>
     * Database type - INT(6)
     */
    Integer lectureNum;

    /**
     * 강좌 이름가 저장된 필드<br>
     * Database type - VARCHAR(64)
     */
    String name;

    /**
     * 강좌 담당 교수가 저장된 필드. 외래키 형식으로 저장<br>
     * Database type - INT(10)
     */
    Professor professor;

    /**
     * 강좌 최대 학생 수가 저장된 필드.
     * Database type - INT(5)
     */
    Integer maxStudent;

    /**
     * 강좌 최소 학생 수가 저장된 필드.
     * Database type - INT(5)
     */
    Integer minStudent;

    /**
     * 강의 개설 여부가 저장된 필드.
     * Database type - TINYINT(1)
     */
    Boolean isOpened;

    /**
     * 강좌의 학점이 저장된 필드.<br>
     * Database type - INT(2)
     */
    Integer creditUnit;

    /**
     * 강의를 듣는 학생들 리스트. lecture_listener 테이블에 저장되며, LazyFetcher 클래스 안에 캐스트됩니다.
     * @see LazyFetcher
     */
    LazyFetcher<Long, List<Student>> studentList;
}
