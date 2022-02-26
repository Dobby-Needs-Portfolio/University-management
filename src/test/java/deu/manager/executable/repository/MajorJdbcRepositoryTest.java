package deu.manager.executable.repository;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.AdminStaff;
import deu.manager.executable.domain.Major;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MajorJdbcRepositoryTest {

    @Autowired MajorRepository majorRepository;

    @Test
    public void findById_success()  {
        //ID를 통해서 검색
        Optional<Major> searched = majorRepository.findById(1L);

        // 검색 후, 데이터가 같은지 확인
        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get().getName()).isEqualTo("학과1");

    }

    @Test
    public void findById_failed(){
        Optional<Major> searched = majorRepository.findById(3L);

        //검색 후, 데이터가 존재한다면 버그 발생
        assertThat(searched.isPresent()).isFalse();
    }


    @Test
    public void findByName_success() {
        Optional<Major> searched = majorRepository.findByName("학과1");
        Major target = Major.builder()
                .id(1L)
                .name("학과1").build();

        // 검색한 사람이 존재하는지 확인
        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get()).usingRecursiveComparison().isEqualTo(target);
    }

    @Test
    public void findByName_failed(){
        Optional<Major> searched = majorRepository.findByName("학과3");

        //리스트 내에 결과가 존재한다면, 오류 발생
        assertThat(searched.isEmpty()).isTrue();
    }


    @Test
    public void saveTest() throws DbInsertWrongParamException {
        Major saveObject = Major.builder()
                .name("학과3").build();


        Major savedObject = null;
        Optional<Major> searchedObject = null;
        try {
            savedObject = majorRepository.save(saveObject);
            searchedObject = majorRepository.findByName("학과3");
        } catch (Throwable e) {
            assert false;
        }

        assertThat(saveObject.getName()).isEqualTo(savedObject.getName());
        assertThat(searchedObject.isPresent()).isTrue();
        assertThat(saveObject.getName()).isEqualTo(searchedObject.get().getName());

    }

    //Update
    @Test
    public void updateTest() throws DbInsertWrongParamException {
        Major major = new Major();
        major.setName("Test1");

        // Test1으로 처음 저장이 됨.
        Major savedObject = majorRepository.save(major);

        //Test1을 Test2로 학과 이름 변경
        savedObject.setName("Test2");

        // 변경된 학과 이름 update
        try {
            majorRepository.update(savedObject);
        } catch (Throwable e){
            assertThat(true).isFalse();
        }

        // 없다는 것은 실패이다. -> TRUE
        assertThat(majorRepository.findByName("Test2").isEmpty()).isFalse();
        assertThat(majorRepository.findByName("Test1").isEmpty()).isTrue();

        Optional<Major> searchedObject = majorRepository.findByName("Test2");
        assertThat(searchedObject.get().getId()).isEqualTo(savedObject.getId());

    }


    //Delete
    @Test
    public void deleteTest() throws DbInsertWrongParamException {
        Major major1 = new Major();
        major1.setName("test1");

        Major saved1 = majorRepository.save(major1);
        majorRepository.delete(saved1.getId());


    }


}
