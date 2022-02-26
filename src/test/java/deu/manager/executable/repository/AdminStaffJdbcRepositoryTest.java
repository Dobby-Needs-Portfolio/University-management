package deu.manager.executable.repository;

import deu.manager.executable.domain.AdminStaff;
import deu.manager.executable.repository.interfaces.AdminStaffRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

// https://pomo0703.tistory.com/100
// https://www.amitph.com/testing-spring-data-jdbc/


//You should run this test after run database_init.sql and insert_example.sql at project/sql folder.
@SpringBootTest @Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class AdminStaffJdbcRepositoryTest {

    @Autowired private AdminStaffRepository repository;

    @Before
    public void setup(){
        AdminStaff adminStaff = AdminStaff.builder()
                .name("TestName1")
                .staffNum(321)
                .residentNum("1112221231231")
                .password("1221221").build();

        try{
            adminStaff = repository.save(adminStaff);
        } catch(Throwable e) { assert false; }
    }

    //Read
    @Test
    public void findById_success(){
        //ID를 통해 검색
        Optional<AdminStaff> searched = repository.findById(1L);

        //검색 후, 데이터가 같은 지 확인
        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get().getName()).isEqualTo("어드민");
    }

    @Test
    public void findById_failed(){
        Optional<AdminStaff> searched = repository.findById(2L);

        //검색 후, 데이터가 존재한다면 버그 발생
        assertThat(searched.isPresent()).isFalse();
    }

    @Test
    public void findByName_success(){
        List<AdminStaff> searched = repository.findByName("어드민");
        AdminStaff target = AdminStaff.builder()
                        .id(1L)
                        .name("어드민")
                        .password("admin_password")
                        .staffNum(111)
                        .residentNum("1111111111111").build();

        //리스트 내에 입력한 사람이 존재하는지 확인
        assertThat(searched.stream().findAny().isPresent()).isTrue();
        assertThat(searched.stream().findAny().get()).usingRecursiveComparison().isEqualTo(target);
    }

    @Test
    public void findByName_failed(){
        List<AdminStaff> searched = repository.findByName("TestName4");

        //리스트 내에 결과가 존재한다면, 오류 발생
        assertThat(searched.isEmpty()).isTrue();
    }

    //Create
    @Test
    public void saveTest(){
        AdminStaff saveObject = AdminStaff.builder()
                .name("TestName2")
                .password("password")
                .staffNum(122)
                .residentNum("1112221231231").build();
        AdminStaff savedObject = null;
        Optional<AdminStaff> searchedObject = null;
        try {
            savedObject = repository.save(saveObject);
            searchedObject = repository.findByStaffNum(122);

        } catch (Throwable e) {
            assert false;
        }
        assertThat(saveObject.getName()).isEqualTo(savedObject.getName());
        assertThat(searchedObject.isPresent()).isTrue();
        assertThat(saveObject.getName()).isEqualTo(searchedObject.get().getName());
    }

    //Update
    @Test
    public void update(){

        Optional<AdminStaff> searched = repository.findById(1L);
        assertThat(searched.isPresent()).isTrue();

        AdminStaff editObject = searched.get();
        editObject.setName("TestNameEdit");
        try {
            repository.update(editObject);
        } catch ( Throwable e ) {
            assertThat(true).isFalse();
        }

        assertThat(repository.findByName("TestNameEdit").isEmpty()).isFalse();
        assertThat(repository.findByName("TestNameEdit")
                .stream().findAny().get()).usingRecursiveComparison().isEqualTo(editObject);

    }

    //Delete
    @Test
    public void delete(){
        AdminStaff save = AdminStaff.builder()
                .name("TestName2")
                .password("password")
                .staffNum(122)
                .residentNum("1112221231231").build();
        try {
            AdminStaff toDelete = repository.save(save);
            repository.delete(toDelete.getId());

            assertThat(repository.findById(toDelete.getId()).isPresent()).isFalse();
        } catch (Throwable e) {
            assert false;
        }

    }

    @Test
    public void delete_array(){
        AdminStaff save1 = AdminStaff.builder()
                .name("TestDelete1")
                .password("tp1")
                .staffNum(300)
                .residentNum("1112221231231").build();
        AdminStaff save2 = AdminStaff.builder()
                .name("TestDelete2")
                .password("tp2")
                .staffNum(301)
                .residentNum("1112221231232").build();
        AdminStaff save3 = AdminStaff.builder()
                .name("TestDelete3")
                .password("tp3")
                .staffNum(302)
                .residentNum("1112221231233").build();

        List<Long> idListToDelete = new ArrayList<>();

        try {
            idListToDelete.add(repository.save(save1).getId());
            idListToDelete.add(repository.save(save2).getId());
            idListToDelete.add(repository.save(save3).getId());

            repository.delete(idListToDelete);

            for (var id : idListToDelete){
                assertThat(repository.findById(id).isPresent()).isFalse();
            }
        } catch (Throwable e) {
            assert false;
        }
    }
}