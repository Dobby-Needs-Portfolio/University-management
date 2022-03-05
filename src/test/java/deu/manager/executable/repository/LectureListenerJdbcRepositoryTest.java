package deu.manager.executable.repository;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import deu.manager.executable.repository.interfaces.LectureRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "LectureListenerTest_init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LectureListenerJdbcRepositoryTest {
    @Autowired LectureListenerRepository repository;
    @Autowired LectureRepository lectureRepository;

    Logger logger = LogManager.getLogger(this.getClass());

    //Create
    @Test @DisplayName("save_success")
    public void save_success() throws DbInsertWrongParamException{
        //Read both of the data
        repository.save(2L,5L);
        List<Lecture> searchedLecture = repository.searchLecture(2L);
        List<Student> searchedStudent = repository.searchStudent(5L);

        Lecture targetLecture = Lecture.builder()
                .id(5L)
                .lectureNum(005)
                .name("강의5")
                .professor(Professor.builder()
                        .id(2L)
                        .professorNum(001)
                        .name("교수2")
                        .password("password")
                        .residentNum("1231231112221")
                        .lectures(null)
                        .major(Major.builder()
                                .id(2L)
                                .name("학과2").build()).build())
                .creditUnit(2)
                .maxStudent(33)
                .minStudent(13)
                .isOpened(false)
                .studentList(new LazyFetcher<>(5L, this.repository::searchStudent)).build();

        Student targetStudent = Student.builder()
                .id(2L)
                .studentNum(002)
                .lectureList(new LazyFetcher<>(2L, this.repository::searchLecture))
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .name("학생2")
                .password("password")
                .residentNum("1231231112222").build();

        Optional<Lecture> filteredLecture = searchedLecture.stream().filter(lecture -> lecture.getId() == 5L).findAny();
        Optional<Student> filteredStudent = searchedStudent.stream().filter(student -> student.getId() == 2L).findAny();

        assertThat(filteredLecture.isPresent()).isTrue();
        assertThat(filteredStudent.isPresent()).isTrue();

        assertThat(filteredLecture.get()).usingRecursiveComparison().isEqualTo(targetLecture);
        assertThat(filteredStudent.get()).usingRecursiveComparison().isEqualTo(targetStudent);
    }

    @Test @DisplayName("save_failure")
    public void save_failure(){
        // save already exists case
        {
            assertThatThrownBy(() -> repository.save(1L, 1L) )
                    .isInstanceOf(DataAccessException.class);
        }

        // Invalid foreign key insert check
        {
            assertThatThrownBy(() -> repository.save(12L, 10L))
                    .isInstanceOf(DataAccessException.class);
        }
    }

    //Read
    @Test @DisplayName("search_student")
    public void searchStudent(){
        List<Student> searched = repository.searchStudent(1L);

        ArrayList<Student> target = new ArrayList<Student>(Arrays.asList(
                Student.builder()
                        .id(1L)
                        .name("학생1")
                        .studentNum(1)
                        .password("password")
                        .residentNum("1231231112221")
                        .major(Major.builder()
                                .id(1L).name("학과1").build())
                        .lectureList(new LazyFetcher<>(1L, this.repository::searchLecture))
                        .build(),
                Student.builder()
                        .id(2L)
                        .name("학생2")
                        .studentNum(2)
                        .password("password")
                        .residentNum("1231231112222")
                        .major(Major.builder()
                                .id(1L).name("학과1").build())
                        .lectureList(new LazyFetcher<>(2L, this.repository::searchLecture))
                        .build()
        ));

        assertThat(searched.size()).isEqualTo(2);
        assertThat(searched).usingRecursiveComparison().isEqualTo(target);
    }

    @Test @DisplayName("search_lecture")
    public void searchLecture(){
        List<Lecture> searched = repository.searchLecture(1L);

        ArrayList<Lecture> target = new ArrayList<>(Arrays.asList(
                Lecture.builder()
                        .id(1L)
                        .lectureNum(001)
                        .name("강의1")
                        .professor(Professor.builder()
                                .id(1L)
                                .professorNum(002)
                                .name("교수1")
                                .password("password")
                                .residentNum("1231231112222")
                                .lectures(null)
                                .major(Major.builder()
                                        .id(1L)
                                        .name("학과1").build()).build())
                        .creditUnit(3)
                        .maxStudent(30)
                        .minStudent(10)
                        .isOpened(false)
                        .studentList(new LazyFetcher<>(1L, this.repository::searchStudent)).build(),
                Lecture.builder()
                        .id(2L)
                        .lectureNum(002)
                        .name("강의2")
                        .professor(Professor.builder()
                                .id(2L)
                                .name("교수2")
                                .professorNum(001)
                                .password("password")
                                .residentNum("1231231112221")
                                .lectures(null)
                                .major(Major.builder()
                                        .id(2L)
                                        .name("학과2").build()).build())
                        .creditUnit(2)
                        .maxStudent(31)
                        .minStudent(11)
                        .isOpened(false)
                        .studentList(new LazyFetcher<>(2L, this.repository::searchStudent)).build()
        ));

        assertThat(searched.size()).isEqualTo(2);
        assertThat(searched).usingRecursiveComparison().isEqualTo(target);
    }

    //Delete
    @Test @DisplayName("delete_single")
    public void deleteSingle() throws DbInsertWrongParamException {
        repository.deleteSingle(5L, 5L);
        List<Student> student = repository.searchStudent(5L);
        List<Lecture> lecture = repository.searchLecture(5L);

        assertThat(student.isEmpty()).isTrue();
        assertThat(lecture.isEmpty()).isTrue();
    }

    @Test @DisplayName("delete_student(one, List)")
    public void deleteStudent() throws DbInsertWrongParamException {
        //Delete one
        {
            repository.deleteStudent(1L);
            List<Student> searched = repository.searchStudent(1L);

            Student target = Student.builder()
                    .id(2L)
                    .name("학생2")
                    .studentNum(2)
                    .password("password")
                    .residentNum("1231231112222")
                    .major(Major.builder()
                            .id(1L).name("학과1").build())
                    .lectureList(new LazyFetcher<>(2L, this.repository::searchLecture))
                    .build();

            assertThat(searched.size()).isEqualTo(1);
            Optional<Student> extracted = searched.stream().findAny();
            assertThat(extracted.isPresent()).isTrue();
            assertThat(extracted.get()).usingRecursiveComparison().isEqualTo(target);
        }
        //List delete
        {
            repository.deleteStudent(Arrays.asList(1L, 2L));
            List<Student> searched = repository.searchStudent(2L);

            assertThat(searched.isEmpty()).isTrue();
        }
    }

    @Test @DisplayName("delete_lecture(one, List)")
    public void deleteLecture() throws DbInsertWrongParamException {
        //Delete one
        {
            repository.deleteLecture(1L);
            List<Student> searched = repository.searchStudent(1L);

            assertThat(searched.size()).isEqualTo(1);
        }
        //List delete
        {
            repository.deleteLecture(Arrays.asList(2L, 3L));
            List<Student> searched = repository.searchStudent(2L);

            assertThat(searched.isEmpty()).isTrue();
        }
    }




}