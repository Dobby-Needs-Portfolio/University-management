package deu.manager.executable.repository.interfaces;

import deu.manager.executable.domain.TestDomain;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * <h2>The template class that indicates Repository</h2>
 * <p>
 * 다른 Repository interface를 위해 작성된 클래스로, 실 프로젝트에서는 사용하지 않습니다.<br>
 * 반드시 각 필드마다 JavaDoc 주석이 작성되어 있어야 합니다(필요 없는 부분은 임의로 삭제 가능합니다). <br>
 * 인터페이스를 통해 SpringConfig 클래스에서의 DI에서 다형성이 보장된 Injection을 생성합니다.
 */

public interface TestRepository {
    /**
     * TestDomain을 관리하는 DB에 데이터를 추가(post)하는 메소드
     * @param input
     * @return void
     */
    TestDomain post(TestDomain input);

    /**
     * TestDomain을 관리하는 DB에서 id를 사용하여 데이터를 검색하는 메소드
     * @param id
     * @return
     */
    Optional<TestDomain> findById(Long id);

    /**
     * TestDomain을 관리하는 DB에서 name을 사용하여 데이터를 검색하는 메소드
     * @param name
     * @return
     */
    Optional<TestDomain> findByName(String name);

    /**
     * TestDomain을 관리하는 DB에서 id가 일치하는 row의 값을 변경하는 메소드<br>
     * param에 TestDomainParam.builder()를 사용해서 값을 입력한다.
     * @param id
     * @param param
     * @return
     */
    boolean put(Long id, TestDomainParam param);

    @Getter
    @Builder
    class TestDomainParam implements Cloneable{
        private Long id;
        private String name;
        private String password;
    }
}

