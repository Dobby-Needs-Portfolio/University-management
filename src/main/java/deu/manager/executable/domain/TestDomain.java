package deu.manager.executable.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * <h2>Domain Template Class</h2>
 * <p>
 * 다른 domain 클래스의 템플릿을 위해 작성된 클래스로, 실 프로젝트에서는 사용하지 않습니다.<br>
 * 모든 domain 클래스의 필드에는 반드시 JavaDoc 주석이 작성되어야 합니다.<br>
 * 클래스 내에는 <b>변수 외에 다른 Getter, Setter</b>를 선언하지 마시기 바랍니다.<br>
 * 대신, lombok의 @Getter, @Setter, @ToString을 사용해서 변수 접근 메소드를 생성하세요.
 */

@Entity
@Getter
@Setter
public class TestDomain {
    /**
     * 고유 ID 값으로, 자동으로 생성됩니다.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 사용자의 이름입니다.
     */
    @Column(name = "name")
    private String name;

    /**
     * 사용자가 설정한 비밀번호입니다. 값은 null일 수 없습니다.
     */
    @Column(name = "pw", nullable = false)
    private String password;
}

