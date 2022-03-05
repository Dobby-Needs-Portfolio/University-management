package deu.manager.executable.repository;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "StudentTest_Init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class StudentJdbcRepositoryTest {
    @Autowired StudentRepository repository;
    Logger logger = LogManager.getLogger(this.getClass());

    //Read
    @Test @DisplayName("search by id - single, success")
    public void searchById_singleSuccess(){
        Optional<Student> search1 = repository.findById(1L);

        Student target1 = Student.builder()
                .id(1L)
                .name("학생1")
                .studentNum(001)
                .password("password")
                .residentNum("1231231112221")
                .lectureList(null)
                .major(Major.builder()
                        .id(1L)
                        .name("학과1")
                        .build()).build();

        assertThat(search1.isPresent()).isTrue();
        Student actual1 = search1.get();
        actual1.setLectureList(null);
        assertThat(actual1).usingRecursiveComparison()
                .isEqualTo(target1);

        Optional<Student> search2 = repository.findById(2L);

        Student target2 = Student.builder()
                .id(2L)
                .name("학생2")
                .studentNum(002)
                .password("password")
                .residentNum("1231231112222")
                .lectureList(null)
                .major(Major.builder()
                        .id(2L)
                        .name("학과2")
                        .build()).build();

        assertThat(search2.isPresent()).isTrue();
        Student actual2 = search2.get();
        actual2.setLectureList(null);
        assertThat(actual2).usingRecursiveComparison()
                .isEqualTo(target2);
    }

    @Test @DisplayName("search by id - single, wrong input")
    public void searchById_singleFailed(){
        //search wrong id
        Optional<Student> searched = repository.findById(19L);

        assertThat(searched.isPresent()).isFalse();
    }

    @Test @DisplayName("search by id - multiple")
    public void searchById_multiple(){
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 4L));
        List<Student> target = new ArrayList<>(Arrays.asList(
                Student.builder()
                        .id(1L)
                        .name("학생1")
                        .password("password")
                        .studentNum(001)
                        .residentNum("1231231112221")
                        .lectureList(null)
                        .major(Major.builder()
                                .id(1L)
                                .name("학과1")
                                .build()).build(),
                Student.builder()
                        .id(2L)
                        .name("학생2")
                        .password("password")
                        .studentNum(002)
                        .residentNum("1231231112222")
                        .lectureList(null)
                        .major(Major.builder()
                                .id(2L)
                                .name("학과2")
                                .build()).build(),
                Student.builder()
                        .id(4L)
                        .name("학생4")
                        .password("password")
                        .studentNum(005)
                        .residentNum("1231231112224")
                        .lectureList(null)
                        .major(Major.builder()
                                .id(1L)
                                .name("학과1")
                                .build()).build()
        ));

        List<Student> searched = repository.findById(ids);
        searched.forEach((elem) -> elem.setLectureList(null));

        assertThat(searched.isEmpty()).isFalse();
        assertThat(searched).usingRecursiveComparison().isEqualTo(target);
    }

    @Test @DisplayName("search by Student Number - success")
    public void searchByStudentNum_success(){
        Student target = Student.builder()
                .id(1L)
                .name("학생1")
                .password("password")
                .residentNum("1231231112221")
                .studentNum(001)
                .lectureList(null)
                .major(Major.builder()
                        .id(1L)
                        .name("학과1")
                        .build()).build();

        Optional<Student> queryData = repository.findByStudentNum(001);
        assertThat(queryData.isPresent()).isTrue();
        Student searched = queryData.get();
        searched.setLectureList(null);

        assertThat(searched).usingRecursiveComparison().isEqualTo(target);
    }

    @Test @DisplayName("search by name")
    public void searchByName() {
        Student target = Student.builder()
                .id(1L)
                .name("학생1")
                .password("password")
                .residentNum("1231231112221")
                .studentNum(001)
                .lectureList(null)
                .major(Major.builder()
                        .id(1L)
                        .name("학과1")
                        .build()).build();

        List<Student> searched = repository.findByName("학생1");

        assertThat(searched.size()).isEqualTo(1);
        searched.get(0).setLectureList(null);
        assertThat(searched.get(0)).usingRecursiveComparison().isEqualTo(target);
    }

    //Create
    @Test @DisplayName("save - success")
    public void save_success() throws DbInsertWrongParamException {
        Student saveObject = Student.builder()
                .name("학생입력")
                .password("testPassword")
                .residentNum("1231231112227")
                .studentNum(004)
                .major(Major.builder()
                        .id(2L)
                        .name("학과2").build())
                .lectureList(null).build();
        Student savedObject = repository.save(saveObject);

        Optional<Student> queryObject = repository.findById(savedObject.getId());

        assertThat(queryObject.isPresent()).isTrue();
        Student searched = queryObject.get();
        searched.setLectureList(null);
        assertThat(savedObject).usingRecursiveComparison().isEqualTo(searched);
    }

    @Test @DisplayName("save - fail case")
    public void save_failure() throws DbInsertWrongParamException {
        //Check ID nullCheck Exception - ID should be null
        Student idCheckDto = Student.builder()
                .id(20L)
                .name("학생입력")
                .password("testPassword")
                .residentNum("1231231112227")
                .studentNum(004)
                .major(Major.builder()
                        .id(2L)
                        .name("학과2").build())
                .lectureList(null).build();

        assertThatThrownBy(() -> {
            repository.save(idCheckDto);
            logger.warn("Exception not thrown - Id Check");
        }).isInstanceOf(DbInsertWrongParamException.class);

        //Student Number Duplicate check
        Student studentNumDupCheck = Student.builder()
                .id(null)
                .name("학생입력")
                .password("testPassword")
                .residentNum("1231231112227")
                .studentNum(001)
                .major(Major.builder()
                        .id(2L)
                        .name("학과2").build())
                .lectureList(null).build();

        assertThatThrownBy(() -> {
            repository.save(studentNumDupCheck);
            logger.warn("Exception not thrown - Student Number Duplicate check");
        }).isInstanceOf(DataAccessException.class);

        //Invalid Major foreign key check
        Student majorWrongKeyCheck = Student.builder()
                .id(null)
                .name("학생입력")
                .password("testPassword")
                .residentNum("1231231112227")
                .studentNum(010)
                .major(Major.builder()
                        .id(120L)
                        .name("학과2").build())
                .lectureList(null).build();

        assertThatThrownBy(() -> {
            repository.save(majorWrongKeyCheck);
            logger.warn("Exception not thrown - major wrong key check");
        }).isInstanceOf(DataAccessException.class);
    }

    //Update
    @Test
    @DisplayName("Update test - success")
    public void updateTest_success() throws DbInsertWrongParamException{
        //Full-update
        {
            Student fullUpdate = Student.builder()
                    .id(1L)
                    .name("이름변경")
                    .password("passwordChange")
                    .residentNum("1112221231231")
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();

            repository.update(fullUpdate);

            Student target = Student.builder()
                    .id(1L)
                    .name("이름변경")
                    .password("passwordChange")
                    .studentNum(001)
                    .residentNum("1112221231231")
                    .lectureList(null)
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();

            Optional<Student> queryObject = repository.findById(1L);
            assertThat(queryObject.isPresent()).isTrue();
            Student searched = queryObject.get();

            searched.setLectureList(null);
            assertThat(searched).usingRecursiveComparison().isEqualTo(target);
        }

        //Optional update
        {
            Student optionalUpdate = Student.builder()
                    .id(1L)
                    .name("이름변경2").build();

            repository.update(optionalUpdate);

            Student target = Student.builder()
                    .id(1L)
                    .name("이름변경2")
                    .password("passwordChange")
                    .studentNum(001)
                    .residentNum("1112221231231")
                    .lectureList(null)
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();

            Optional<Student> queryObject = repository.findById(1L);
            assertThat(queryObject.isPresent()).isTrue();
            Student searched = queryObject.get();

            searched.setLectureList(null);
            assertThat(searched).usingRecursiveComparison().isEqualTo(target);

        }
    }

    @Test
    @DisplayName("update - failure")
    public void update_failure() throws DbInsertWrongParamException{
        //no ID case
        {
            Student noId = Student.builder()
                    .name("학생5")
                    .residentNum("1231231112221").build();

            assertThatThrownBy(() -> {
                repository.update(noId);
                logger.warn("No exception thrown - update with no id");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change Student number case
        {
            Student changeStudentNum = Student.builder()
                    .id(4L)
                    .studentNum(10)
                    .name("학생5")
                    .residentNum("1231231112221").build();

            assertThatThrownBy(() -> {
                repository.update(changeStudentNum);
                logger.warn("No exception thrown - update student number");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change invalid major id foreign key case
        {
            Student changeStudentNum = Student.builder()
                    .id(1L)
                    .name("학생변경")
                    .major(Major.builder()
                            .id(10L)
                            .name("잘못된학과")
                            .build()).build();

            assertThatThrownBy(() -> {
                repository.update(changeStudentNum);
                logger.warn("No exception thrown - update student number");
            }).isInstanceOf(DataAccessException.class);
        }
    }

    //Delete
    @Test
    @DisplayName("delete - success")
    public void delete_success() throws DbInsertWrongParamException {
        Optional<Student> searchedBefore = repository.findById(2L);
        assertThat(searchedBefore.isPresent()).isTrue();

        repository.delete(2L);
        Optional<Student> searched = repository.findById(2L);

        assertThat(searched.isPresent()).isFalse();
    }

    @Test
    @DisplayName("delete - multiple")
    public void delete_multiple() throws DbInsertWrongParamException {
        List<Student> searchedBefore = repository.findById(new ArrayList<>(Arrays.asList(2L, 3L)));
        assertThat(searchedBefore).hasSize(2);

        repository.delete(new ArrayList<>(Arrays.asList(2L, 3L)));
        List<Student> searched = repository.findById(new ArrayList<>(Arrays.asList(2L, 3L)));
        assertThat(searched.isEmpty()).isTrue();
    }

}