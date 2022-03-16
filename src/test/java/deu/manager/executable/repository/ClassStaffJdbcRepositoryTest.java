package deu.manager.executable.repository;


// https://pomo0703.tistory.com/100
// https://www.amitph.com/testing-spring-data-jdbc/

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.repository.interfaces.ClassStaffRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

//You should run this test after run database_init.sql and insert_example.sql at project/sql folder.
@SpringBootTest @Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(value = "ClassStaffTest_init.sql")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ClassStaffJdbcRepositoryTest {

    @Autowired
    private ClassStaffRepository classStaffRepository;

    // Setup을 통해 저장되는 수업 직원을 클래스 멤버로 저장하여 활용할 수 있게 한다.
    private ClassStaff classStaff;

    /**
     * Test 방향을 다양하게 하기 위해 Test 하기 전 임시 데이터 DB에 저장
     */
    @Before
    public void setup() {
        classStaff = ClassStaff.builder()
                .id(2L)
                .name("직원2")
                .staffNum(222)
                .password("1221221")
                .residentNum("1112221231231").build();
    }

    @Test
    public void findById_success(){
        Optional<ClassStaff> searched = classStaffRepository.findById(1L);

        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get().getName()).isEqualTo("직원1");

    }

    @Test
    public void findById_failed(){
        Optional<ClassStaff> searched = classStaffRepository.findById(5L);

        // 값이 존재하면 에러(버그) 발생
        assertThat(searched.isPresent()).isFalse();
    }

    @Test
    public void findByName_success(){
        List<ClassStaff> searched1 = classStaffRepository.findByName("직원1");
        List<ClassStaff> searched2 = classStaffRepository.findByName("직원2");

        ClassStaff target1 = ClassStaff.builder()
                .id(1L)
                .name("직원1")
                .staffNum(1)
                .password("staff_password")
                .residentNum("1111111111112").build();

        ClassStaff target2 = ClassStaff.builder()
                .id(classStaff.getId())
                .name("직원2")
                .staffNum(222)
                .password("1221221")
                .residentNum("1112221231231").build();

        assertThat(searched1.stream().findAny().isPresent()).isTrue();
        assertThat(searched1.stream().findAny().get()).usingRecursiveComparison().isEqualTo(target1);

        assertThat(searched2.stream().findAny().isPresent()).isTrue();
        assertThat(searched2.stream().findAny().get()).usingRecursiveComparison().isEqualTo(target2);
    }


    @Test
    public void findByName_failed(){
        List<ClassStaff> searched = classStaffRepository.findByName("직원3");

        //리스트 내에 결과가 존재한다면, 오류 발생
        assertThat(searched.isEmpty()).isTrue();
    }

    @Test
    public void findByStaffNum_success() {
        Optional<ClassStaff> searched = classStaffRepository.findByStaffNum(222);

        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get().getName()).isEqualTo("직원2");
    }

    @Test
    public void findByStaffNum_failed() {
        Optional<ClassStaff> searched = classStaffRepository.findByStaffNum(3);

        assertThat(searched.isPresent()).isFalse();
    }


    //save
    @Test
    public void save() throws DbInsertWrongParamException {
        ClassStaff saveObject = ClassStaff.builder()
                .name("직원3")
                .staffNum(123)
                .password("123459")
                .residentNum("111122223").build();

        ClassStaff savedObject = classStaffRepository.save(saveObject);
        // 실제로 db에 잘 저장이 되었는지 확인해야 한다.
        Optional<ClassStaff> searchedObject = classStaffRepository.findById(savedObject.getId());

        assertThat(saveObject.getName()).isEqualTo(savedObject.getName());
        assertThat(searchedObject.isPresent()).isTrue();
        assertThat(saveObject.getName()).isEqualTo(searchedObject.get().getName());


    }

    //Update
    @Test
    public void update() throws DbInsertWrongParamException {

        Optional<ClassStaff> searched = classStaffRepository.findById(1L);
        assertThat(searched.isPresent()).isTrue();

        ClassStaff editObject = searched.get();
        editObject.setName("직원4");

        classStaffRepository.update(editObject);

        assertThat(classStaffRepository.findByName("직원4")).isNotEmpty();
        Optional<ClassStaff> staff = classStaffRepository.findByName("직원4").stream().findAny();
        assertThat(staff.isPresent()).isTrue();
        assertThat(staff.get()).usingRecursiveComparison().isEqualTo(editObject);

    }


    //delete
    @Test
    public void delete() throws DbInsertWrongParamException {

        List<ClassStaff> searched = classStaffRepository.findByName("직원2");

        Optional<ClassStaff> staff = searched.stream().findAny();
        assertThat(staff.isPresent()).isTrue();
        classStaffRepository.delete(staff.get().getId());

        assertThat(classStaffRepository.findByName("직원2").stream().findAny().isPresent()).isFalse();
    }


    @Test
    public void delete_array() throws DbInsertWrongParamException {

        // "직원2"를 가진 데이터 하나 더 넣기 , db에 동명이인 존재
        ClassStaff save1 = ClassStaff.builder()
                .name("직원2")
                .staffNum(30)
                .password("12312312")
                .residentNum("147258369").build();

        classStaffRepository.save(save1);

        // "직원2" 2개 데이터의 id값을 list에 id담기
        List<Long> deleteList = classStaffRepository.findByName("직원2").stream()
                .map(ClassStaff::getId).collect(Collectors.toList());

        // Id 목록을 인자로 넣어야 한다.
        classStaffRepository.delete(deleteList);

        // 더 이상 동명이인이 없는것을 확인!
        assertThat(classStaffRepository.findByName("직원2")).isEmpty();

    }
}
