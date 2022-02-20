package deu.manager.executable.repository;

import deu.manager.executable.domain.AdminStaff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

// https://pomo0703.tistory.com/100
// https://www.amitph.com/testing-spring-data-jdbc/

@DataJdbcTest
@RunWith(SpringRunner.class)
class AdminStaffJdbcRepositoryTest {

    @Autowired private AdminStaffRepository repository;

    private AdminStaff adminStaff;

    @Before
    public void setup(){
        adminStaff = AdminStaff.builder()
                .id(1L)
                .name("TestName1")
                .staffNum(321)
                .residentNum("1112221231231")
                .password("1221221").build();

        repository.save(adminStaff);
    }

    //Read
    @Test
    public void findById_success(){
        //ID를 통해 검색
        Optional<AdminStaff> searched = repository.findById(1L);

        //검색 후, 데이터가 같은 지 확인
        assertThat(searched.isPresent()).isTrue();
        assertThat(searched.get()).isEqualTo(adminStaff);
    }

    @Test
    public void findById_failed(){
        Optional<AdminStaff> searched = repository.findById(2L);

        //검색 후, 데이터가 존재한다면 버그 발생
        assertThat(searched.isPresent()).isFalse();
    }

    @Test
    public void findByName_success(){
        List<AdminStaff> searched = repository.findByName("TestName1");

        //리스트 내에 입력한 사람이 존재하는지 확인
        assertThat(searched.contains(adminStaff)).isTrue();
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

        AdminStaff savedObject = repository.save(saveObject);
        Optional<AdminStaff> searchedObject = repository.findByStaffNum(122);

        assertThat(saveObject).isEqualTo(savedObject);
        assertThat(searchedObject.isPresent()).isTrue();
        assertThat(saveObject).isEqualTo(searchedObject.get());
    }

    //Update
    @Test
    public void update(){
        Optional<AdminStaff> searched = repository.findById(1L);
        assertThat(searched.isPresent()).isTrue();

        AdminStaff editObject = searched.get();
        editObject.setName("TestNameEdit");
        repository.update(editObject);

        assertThat(repository.findByName("TestNameEdit")
                .get(0)).isEqualTo(editObject);
    }

    //Delete
    @Test
    public void delete(){
        AdminStaff save = AdminStaff.builder()
                .name("TestName2")
                .password("password")
                .staffNum(122)
                .residentNum("1112221231231").build();

        AdminStaff toDelete = repository.save(save);

        repository.delete(toDelete.getId());

        assertThat(repository.findById(toDelete.getId()).isPresent()).isFalse();
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

        idListToDelete.add(repository.save(save1).getId());
        idListToDelete.add(repository.save(save2).getId());
        idListToDelete.add(repository.save(save3).getId());

        repository.delete(idListToDelete);

        for (var id : idListToDelete){
            assertThat(repository.findById(id).isPresent()).isFalse();
        }
    }
}