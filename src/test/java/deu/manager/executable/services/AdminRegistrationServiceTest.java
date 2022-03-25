package deu.manager.executable.services;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.ClassStaffRepository;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "AdminRegistrationServiceTest.sql" , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class AdminRegistrationServiceTest {

    @Autowired AdminRegistrationService adminRegistrationService;

    @Autowired StudentRepository studentRepository;
    @Autowired ProfessorRepository professorRepository;
    @Autowired ClassStaffRepository classStaffRepository;

    Logger logger = LogManager.getLogger(this.getClass());

    @Test
    @DisplayName("Test Student register")
    public void stu_registration() throws DbInsertWrongParamException {

        // form 에서 이름, 학번 , 주민번호 , 학과 정보 등록
        Student stu = Student.builder()
                .id(3L)
                .name("이수찬")
                .studentNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .build();

        //TODO: 테스트 오류 해결할것
        //Student saved = adminRegistrationService.staffRegister(stu.getName(),stu.getStudentNum(),stu.getResidentNum(),stu.getMajor());

        //assertThat(stu).usingRecursiveComparison().isEqualTo(saved);

    }

    @Test
    @DisplayName(" Student register failed" )
    public void stu_registration_failed()  {

        // 학생 이름 누락
        Student stu = Student.builder()
                .id(3L)
                .studentNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .build();

        assertThatThrownBy(() -> {
            adminRegistrationService.stuRegister(stu.getName(), stu.getStudentNum(), stu.getResidentNum(), stu.getMajor().getId());
            logger.warn("Exception not thrown - check essential field for registration");
        }).isInstanceOf(DbInsertWrongParamException.class);

    }


    @Test
    @DisplayName("Test prof register")
    public void prof_registration() throws DbInsertWrongParamException {

        // form 에서 이름, 학번 , 주민번호 , 학과 정보 등록
        Professor prof = Professor.builder()
                .id(3L)
                .name("이수찬")
                .professorNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .build();

        Professor saved = adminRegistrationService.profRegister(prof.getName(),prof.getProfessorNum(),prof.getResidentNum(),prof.getMajor());

        assertThat(prof).usingRecursiveComparison().isEqualTo(saved);

    }

    @Test
    @DisplayName(" Prof register failed" )
    public void prof_registration_failed()  {

        // 교수 이름 누락
        Professor prof = Professor.builder()
                .id(3L)
                .professorNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .build();

        assertThatThrownBy(() -> {
            adminRegistrationService.profRegister(prof.getName(), prof.getProfessorNum(), prof.getResidentNum(), prof.getMajor());
            logger.warn("Exception not thrown - check essential field for registration");
        }).isInstanceOf(DbInsertWrongParamException.class);

    }


    @Test
    @DisplayName("Test class_staff register")
    public void staff_registration() throws DbInsertWrongParamException {

        // form 에서 이름, 학번 , 주민번호 , 학과 정보 등록
        ClassStaff staff = ClassStaff.builder()
                .id(2L)
                .name("이수찬")
                .staffNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .build();

        ClassStaff saved = adminRegistrationService.staffRegister(staff.getName(),staff.getStaffNum(),staff.getResidentNum());

        assertThat(staff).usingRecursiveComparison().isEqualTo(saved);

    }

    @Test
    @DisplayName(" class_staff register failed")
    public void staff_registration_failed()  {

        // 이름 누락
        ClassStaff staff = ClassStaff.builder()
                .id(2L)
                .staffNum(003)
                .password("4455667")
                .residentNum("1122334455667")
                .build();

        assertThatThrownBy(() -> {
            adminRegistrationService.staffRegister(staff.getName(), staff.getStaffNum(), staff.getResidentNum());
            logger.warn("Exception not thrown - check essential field for registration");
        }).isInstanceOf(DbInsertWrongParamException.class);


    }



}
