package deu.manager.executable.repository;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.config.exception.database.DbUpdateRecordNotAvailable;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import deu.manager.executable.repository.interfaces.LectureRepository;
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
@Sql(value = "LectureListenerTest_init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LectureJdbcRepositoryTest {

    @Autowired LectureRepository repository;
    @Autowired LectureListenerRepository llrepository;

    Logger logger = LogManager.getLogger(this.getClass());


    @Test
    @DisplayName("save _ success")
    public void save_success() throws DbInsertWrongParamException {

        Lecture saveObject = Lecture.builder()
                .lectureNum(006)
                .name("강의6")
                .professor(Professor.builder()
                        .id(3L)
                        .name("교수3")
                        .professorNum(003)
                        .password("password")
                        .residentNum("1231231112223")
                        .lectures(null)
                        .major(Major.builder().id(3L).name("학과3").build())
                        .build())
                .creditUnit(3)
                .maxStudent(34)
                .minStudent(14)
                .isOpened(false)
                .studentList(null).build();

        Lecture savedObject = repository.save(saveObject);
        Optional<Lecture> searchedObject = repository.searchById(savedObject.getId());

        assertThat(searchedObject.isPresent()).isTrue();
        Lecture searchedObject1 = searchedObject.get();
        searchedObject1.getProfessor().setLectures(null);
        searchedObject1.setStudentList(null);

        assertThat(savedObject).usingRecursiveComparison().isEqualTo(searchedObject1);

        //실제로 save할때 필요한 값만 입력하였음 , lectures , studentList 제외
        Lecture testObject = Lecture.builder()
                .lectureNum(010)
                .name("강의6")
                .professor(Professor.builder()
                        .id(3L)
                        .name("교수3")
                        .professorNum(003)
                        .password("password")
                        .residentNum("1231231112223")
                        .major(Major.builder().id(3L).name("학과3").build())
                        .build())
                .creditUnit(3)
                .maxStudent(34)
                .minStudent(14)
                .isOpened(false)
                .build();

        Lecture testObject1 = repository.save(testObject);

        //save 되었던 데이터와 비교하기 위해 만든 target 코드
        Lecture target = Lecture.builder()
                .id(testObject1.getId())
                .lectureNum(010)
                .name("강의6")
                .professor(Professor.builder()
                        .id(3L)
                        .name("교수3")
                        .professorNum(003)
                        .password("password")
                        .lectures(new LazyFetcher<>(3L,this.repository::searchByProfessor))
                        .residentNum("1231231112223")
                        .major(Major.builder().id(3L).name("학과3").build())
                        .build())
                .creditUnit(3)
                .maxStudent(34)
                .minStudent(14)
                .isOpened(false)
                .studentList(new LazyFetcher<>(testObject1.getId(), this.llrepository::searchStudent))
                .build();


        Lecture findObject = repository.searchById(testObject1.getId()).get();
        assertThat(findObject).usingRecursiveComparison().isEqualTo(target);

    }


    @Test
    @DisplayName("save - fail case")
    public void save_failed(){

        {
            //Check ID nullCheck Exception - ID should be null
            Lecture idCheckDto = Lecture.builder()
                    .id(6L)
                    .lectureNum(006)
                    .name("강의6")
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(null).build();

            assertThatThrownBy(() -> {
                repository.save(idCheckDto);
                logger.warn("Exception not thrown - Id Check");
            }).isInstanceOf(DbInsertWrongParamException.class);

        }

        {
            //Lecture Number Duplicate check
            Lecture lectureNumDupCheck = Lecture.builder()
                    .lectureNum(001) // Duplicate Check
                    .name("강의6")
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(null).build();

            assertThatThrownBy(() -> {
                repository.save(lectureNumDupCheck);
                logger.warn("Exception not thrown - Lecture Number Duplicate check");
            }).isInstanceOf(DataAccessException.class);

        }

        {

            //Invalid Professor foreign key check
            Lecture prfessorWrongKeyCheck = Lecture.builder()
                    .lectureNum(006)
                    .name("강의6")
                    .professor(Professor.builder()
                            .id(40L) // invalid foreign key check
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(null).build();

            assertThatThrownBy(() -> {
                repository.save(prfessorWrongKeyCheck);
                logger.warn("Exception not thrown - professor wrong key check");
            }).isInstanceOf(DataAccessException.class);


        }

    }


    @Test
    @DisplayName("Update test - success")
    public void updateTest_success() throws DbInsertWrongParamException, DbUpdateRecordNotAvailable {

        {  // 1L , 강의 1 -> 강의 6에 대한 전체 데이터로 변경
            Lecture fullUpdate = Lecture.builder()
                    .id(1L)
                    .name("강의6")
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112223")
                            .lectures(null)
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L, this.llrepository::searchStudent)).build();

            repository.update(fullUpdate);

            // usingRecursiveComparison 와 비교하기 위해 위의 데이터 복사
            Lecture fullUpdated = Lecture.builder()
                    .id(1L)
                    .name("강의6")
                    .lectureNum(001)
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112223")
                            .lectures(new LazyFetcher<>(3L , this.repository::searchByProfessor))
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L, this.llrepository::searchStudent)).build();

            Optional<Lecture> searched = repository.searchById(1L);
            assertThat(searched.isPresent()).isTrue();
            Lecture actual1 = searched.get();

            assertThat(actual1).usingRecursiveComparison().isEqualTo(fullUpdated);

        }

        {
            Lecture optionalUpdate = Lecture.builder()
                    .id(1L)
                    .name("강의1").build();

            repository.update(optionalUpdate);

            Lecture target = Lecture.builder()
                    .id(1L)
                    .lectureNum(001)
                    .name("강의1")
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112223")
                            .lectures(new LazyFetcher<>(3L , this.repository::searchByProfessor))
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(34)
                    .minStudent(14)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L, this.llrepository::searchStudent)).build();

            Optional<Lecture> searched = repository.searchById(1L);
            assertThat(searched.isPresent()).isTrue();
            Lecture searched1 = searched.get();

            assertThat(searched1).usingRecursiveComparison().isEqualTo(target);

        }
    }

    @Test
    @DisplayName("update - failure")
    public void update_failure() throws DbInsertWrongParamException, DbUpdateRecordNotAvailable {
        //no ID case
        {
            Lecture noId = Lecture.builder()
                    .lectureNum(001)
                    .name("강의1")
                    .professor(Professor.builder()
                            .id(1L)
                            .name("교수1")
                            .professorNum(002)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(1L).name("학과1").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(30)
                    .minStudent(10)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();

            assertThatThrownBy(() -> {
                repository.update(noId);
                logger.warn("No exception thrown - update with no id");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change Lecture number case
        {
            Lecture changeLectureNum = Lecture.builder()
                    .id(1L)
                    .lectureNum(001)
                    .name("강의1")
                    .professor(Professor.builder()
                            .id(1L)
                            .name("교수1")
                            .professorNum(002)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(1L).name("학과1").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(30)
                    .minStudent(10)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();

            assertThatThrownBy(() -> {
                repository.update(changeLectureNum);
                logger.warn("No exception thrown - update lecture number");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change invalid professor id foreign key case
        {
            Lecture changeWrongProfKey = Lecture.builder()
                    .id(1L)
                    .name("강의1")
                    .professor(Professor.builder()
                            .id(10L)
                            .name("교수1")
                            .professorNum(002)
                            .password("password")
                            .residentNum("1231231112222")
                            .lectures(null)
                            .major(Major.builder().id(1L).name("학과1").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(30)
                    .minStudent(10)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();

            assertThatThrownBy(() -> {
                repository.update(changeWrongProfKey);
                logger.warn("No exception thrown - update Wrong foreign key");
            }).isInstanceOf(DataAccessException.class);
        }

        {
            //한번 ture로 없데이트 되면 더 이상 수정 불가능 , 2L 데이터 변경 , false -> true로만 변경
            Lecture changeLectureOpened = Lecture.builder()
                    .id(2L)
                    .name("강의2")
                    .professor(Professor.builder()
                            .id(2L)
                            .name("교수2")
                            .professorNum(002)
                            .password("password")
                            .residentNum("1231231112221")
                            .lectures(null)
                            .major(Major.builder().id(2L).name("학과2").build())
                            .build())
                    .creditUnit(2)
                    .maxStudent(31)
                    .minStudent(11)
                    .isOpened(true)
                    .studentList(new LazyFetcher<>(2L,this.llrepository::searchStudent)).build();

            repository.update(changeLectureOpened);
            //한번 개설된 강의에 대한 강의 이름 변경 시도
            Lecture changeOnceOpened = Lecture.builder()
                    .id(2L)
                    .name("강의200").build();

            assertThatThrownBy(() -> {
                repository.update(changeOnceOpened);
                logger.warn("No exception thrown - not available if it once Opened");
            }).isInstanceOf(DbUpdateRecordNotAvailable.class);


        }
    }

    //Read
    @Test
    @DisplayName("find by Id - single success")
    public void searchById_success(){
        Optional<Lecture> searched1 = repository.searchById(1L);

        Lecture target1 = Lecture.builder()
                .id(1L)
                .lectureNum(001)
                .name("강의1")
                .professor(Professor.builder()
                        .id(1L)
                        .name("교수1")
                        .professorNum(002)
                        .password("password")
                        .residentNum("1231231112222")
                        .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                        .major(Major.builder().id(1L).name("학과1").build())
                        .build())
                .creditUnit(3)
                .maxStudent(30)
                .minStudent(10)
                .isOpened(false)
                .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();

        assertThat(searched1.isPresent()).isTrue();
        assertThat(searched1.get()).usingRecursiveComparison().isEqualTo(target1);

    }

    @Test
    @DisplayName("find by Id - single failed")
    public void searchById_failed(){
        Optional<Lecture> searched1 = repository.searchById(20L);
        assertThat(searched1.isPresent()).isFalse();
    }

    @Test
    @DisplayName("find by Ids - multiple success")
    public void searchByIds_success(){
        List<Lecture> searchedList = repository.searchById(Arrays.asList(1L, 2L));

        List<Lecture> targetList = new ArrayList<>(Arrays.asList(
                Lecture.builder()
                        .id(1L)
                        .lectureNum(001)
                        .name("강의1")
                        .professor(Professor.builder()
                                .id(1L)
                                .name("교수1")
                                .professorNum(002)
                                .password("password")
                                .residentNum("1231231112222")
                                .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                                .major(Major.builder().id(1L).name("학과1").build())
                                .build())
                        .creditUnit(3)
                        .maxStudent(30)
                        .minStudent(10)
                        .isOpened(false)
                        .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build(),


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
                                .lectures(new LazyFetcher<>(2L , this.repository::searchByProfessor))
                                .major(Major.builder()
                                        .id(2L)
                                        .name("학과2").build()).build())
                        .creditUnit(2)
                        .maxStudent(31)
                        .minStudent(11)
                        .isOpened(false)
                        .studentList(new LazyFetcher<>(2L, this.llrepository::searchStudent)).build()
        ));

        assertThat(searchedList.isEmpty()).isFalse();
        assertThat(searchedList).usingRecursiveComparison().isEqualTo(targetList);
    }

    @Test
    @DisplayName("find by lectureNum _success")
    public void searchByLectureNym_success(){
        Optional<Lecture> searched = repository.searchByLectureNum(001);

        Lecture target = Lecture.builder()
                .id(1L)
                .lectureNum(001)
                .name("강의1")
                .professor(Professor.builder()
                        .id(1L)
                        .name("교수1")
                        .professorNum(002)
                        .password("password")
                        .residentNum("1231231112222")
                        .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                        .major(Major.builder().id(1L).name("학과1").build())
                        .build())
                .creditUnit(3)
                .maxStudent(30)
                .minStudent(10)
                .isOpened(false)
                .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();

        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get()).usingRecursiveComparison().isEqualTo(target);

    }

    @Test
    @DisplayName("find by lectureNum _ failed")
    public void searchByLectureNym_failed(){
        Optional<Lecture> searched1 = repository.searchByLectureNum(010);
        assertThat(searched1.isPresent()).isFalse();
    }

    @Test
    @DisplayName("find by name _ success")
    public void searchByName_success(){
        List<Lecture> searchedList = repository.searchByName("강의1");

        Lecture target = Lecture.builder()
                .id(1L)
                .lectureNum(001)
                .name("강의1")
                .professor(Professor.builder()
                        .id(1L)
                        .name("교수1")
                        .professorNum(002)
                        .password("password")
                        .residentNum("1231231112222")
                        .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                        .major(Major.builder().id(1L).name("학과1").build())
                        .build())
                .creditUnit(3)
                .maxStudent(30)
                .minStudent(10)
                .isOpened(false)
                .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build();


        assertThat(searchedList.stream().findFirst()).isPresent();
        assertThat(searchedList.stream().findAny().get()).usingRecursiveComparison().isEqualTo(target);

    }

    @Test
    @DisplayName("find by name _ failed")
    public void searchByName_failed() {
        List<Lecture> searchedList = repository.searchByName("강의10");
        assertThat(searchedList.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("find by professor _ success")
    public void searchByProfessor_success(){
        {
            List<Lecture> searchedList = repository.searchByProfessor(3L);

            Lecture target = Lecture.builder()
                    .id(3L)
                    .lectureNum(003)
                    .name("강의3")
                    .professor(Professor.builder()
                            .id(3L)
                            .name("교수3")
                            .professorNum(003)
                            .password("password")
                            .residentNum("1231231112223")
                            .lectures(new LazyFetcher<>(3L , this.repository::searchByProfessor))
                            .major(Major.builder().id(3L).name("학과3").build())
                            .build())
                    .creditUnit(3)
                    .maxStudent(32)
                    .minStudent(12)
                    .isOpened(false)
                    .studentList(new LazyFetcher<>(3L, this.llrepository::searchStudent)).build();


            assertThat(searchedList.stream().findAny().isPresent()).isTrue();
            assertThat(searchedList.stream().findAny().get()).usingRecursiveComparison().isEqualTo(target);

        }

        {
            List<Lecture> searchedList = repository.searchByProfessor(1L);

            List<Lecture> targetList = new ArrayList<>(Arrays.asList(
                    Lecture.builder()
                            .id(1L)
                            .lectureNum(001)
                            .name("강의1")
                            .professor(Professor.builder()
                                    .id(1L)
                                    .name("교수1")
                                    .professorNum(002)
                                    .password("password")
                                    .residentNum("1231231112222")
                                    .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                                    .major(Major.builder().id(1L).name("학과1").build())
                                    .build())
                            .creditUnit(3)
                            .maxStudent(30)
                            .minStudent(10)
                            .isOpened(false)
                            .studentList(new LazyFetcher<>(1L,this.llrepository::searchStudent)).build(),


                    Lecture.builder()
                            .id(4L)
                            .lectureNum(004)
                            .name("강의4")
                            .professor(Professor.builder()
                                    .id(1L)
                                    .name("교수1")
                                    .professorNum(002)
                                    .password("password")
                                    .residentNum("1231231112222")
                                    .lectures(new LazyFetcher<>(1L , this.repository::searchByProfessor))
                                    .major(Major.builder()
                                            .id(1L)
                                            .name("학과1").build()).build())
                            .creditUnit(3)
                            .maxStudent(33)
                            .minStudent(13)
                            .isOpened(false)
                            .studentList(new LazyFetcher<>(4L, this.llrepository::searchStudent)).build()
            ));

            assertThat(searchedList.stream().findAny().isPresent()).isTrue();
            assertThat(searchedList).usingRecursiveComparison().isEqualTo(targetList);

        }

    }


    //Delete
    @Test
    @DisplayName("delete by id _ success")
    public void delete_id() throws DbInsertWrongParamException{

        repository.delete(1L);

        Optional<Lecture> searched = repository.searchById(1L);
        assertThat(searched.isPresent()).isFalse();

    }

    @Test
    @DisplayName("delete by ids _ success")
    public void delete_ids() throws DbInsertWrongParamException{
        repository.delete(Arrays.asList(1L, 2L));

        List<Lecture> searchedList = repository.searchById(Arrays.asList(1L, 2L));
        assertThat(searchedList.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("delete by prof_id _ success")
    public void delete_prof_id() throws DbInsertWrongParamException {

        repository.deleteByProfId(1L); // 강의 1, 강의 4 삭제 예정

        // ll에 더 이상 존재 하지 않는지 확인 강의 번호 1L , 4L
        assertThat(this.llrepository.searchStudent(1L).isEmpty()).isTrue();
        assertThat(this.llrepository.searchStudent(4L).isEmpty()).isTrue();

        // l에 더 이상 존재하지 않는지 확인 , 교수 id로 검색
        assertThat(this.repository.searchByProfessor(1L).isEmpty()).isTrue();



    }

    @Test
    @DisplayName("delete by prof_ids _ success")
    public void delete_prof_ids() throws DbInsertWrongParamException {

        repository.deleteByProfId(Arrays.asList(1L , 3L)); // 강의 1, 강의3, 강의 4 삭제 예정

        // ll에 더 이상 존재 하지 않는지 확인 강의 번호 1L , 3L , 4L
        assertThat(this.llrepository.searchStudent(1L).isEmpty()).isTrue();
        assertThat(this.llrepository.searchStudent(3L).isEmpty()).isTrue();
        assertThat(this.llrepository.searchStudent(4L).isEmpty()).isTrue();

        // l에 더 이상 존재하지 않는지 확인 , 교수 id로 검색
        assertThat(this.repository.searchByProfessor(1L).isEmpty()).isTrue();
        assertThat(this.repository.searchByProfessor(3L).isEmpty()).isTrue();



    }



}
