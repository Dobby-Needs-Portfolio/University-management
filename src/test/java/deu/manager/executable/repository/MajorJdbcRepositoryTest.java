package deu.manager.executable.repository;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "LectureListenerTest_init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MajorJdbcRepositoryTest {

    @Autowired MajorRepository majorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired ProfessorRepository professorRepository;
    @Autowired LectureRepository lectureRepository;
    @Autowired LectureListenerRepository lectureListenerRepository;


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
        assertThat(searched.isPresent()).isTrue();
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
        assertThat(searched.isEmpty()).isFalse();
    }


    @Test
    public void saveTest() throws DbInsertWrongParamException {
        Major saveObject = Major.builder()
                .name("학과4").build();


        Major savedObject = null;
        Optional<Major> searchedObject = null;
        try {
            savedObject = majorRepository.save(saveObject);
            searchedObject = majorRepository.findByName("학과4");
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

        //학과 3L 삭제
        majorRepository.delete(3L);

        // 학과 3L 삭제 -> 학생 5L , 6L 삭제 -> 교수 3L 삭제 -> 강의 3L 삭제
        //ll에서 확인
        List<Student> students3 = lectureListenerRepository.searchStudent(3L);
        assertThat(students3.stream().findAny().isPresent()).isFalse();

        //lecture에서 확인
        Optional<Lecture> lecture3 = lectureRepository.searchById(3L);
        assertThat(lecture3.stream().findAny().isPresent()).isFalse();

        // student에석 확인
        List<Student> byId = studentRepository.findById(Arrays.asList(5L, 6L));
        assertThat(byId.stream().findAny().isPresent()).isFalse();

        //professor에서 확인
        Optional<Professor> byId1 = professorRepository.findById(3L);
        assertThat(byId1.stream().findAny().isPresent()).isFalse();

        //major에서 확인
        Optional<Major> byId2 = majorRepository.findById(3L);
        assertThat(byId2.stream().findAny().isPresent()).isFalse();
    }


}
