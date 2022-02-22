package deu.manager.executable.domain;

import lombok.*;

import javax.persistence.Table;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "major")
public class Major {

    /**
     * 관리자 데이터베이스 고유 키. 자동으로 생성됩니다.<br>
     *  Database type - INT(10)
     */
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * 관리자 이름이 저장된 필드<br>
     * Database type - VARCHAR(64)
     */
    private String name;

}
