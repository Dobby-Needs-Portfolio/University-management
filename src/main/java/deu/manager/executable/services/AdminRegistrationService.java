package deu.manager.executable.services;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminRegistrationService {


    // Repository
    private final StudentRepository studentRepository;
    private final ClassStaffRepository classStaffRepository;
    private final ProfessorRepository professorRepository;
    private final MajorRepository majorRepository;


    @Autowired
    public AdminRegistrationService(
            StudentRepository studentRepository,
            ClassStaffRepository classStaffRepository,
            ProfessorRepository professorRepository,
            MajorRepository majorRepository) {
        this.studentRepository = studentRepository;
        this.classStaffRepository = classStaffRepository;
        this.professorRepository = professorRepository;
        this.majorRepository = majorRepository;
    }


    /**
     * 학사 관리자는 학생을 등록하여 , DB에 넣는다.
     * 학생 등록 , controller에서 form에 입력한 학생 등록 정보를 json에서 map 형태로 받아들인다.
     * @param stuName 학새 이름
     * @param stuNum 학생 번호
     * @param residentNum 학생 13자리 주민번호
     * @param majorId 학생 학과 id
     * @return 저장된 학생 Data
     * @throws DbInsertWrongParamException 학생 등록시 , 필수 데이터가 없으면 throw 되는 예외
     */
    public Student stuRegister(String stuName , int stuNum , String residentNum , Long majorId) throws DbInsertWrongParamException {

        // 맨 마지막 인자로 major 객체가 아닌 ID로 받는 이유는 , view 쪽에서 학과 리스트를
        // 선택하면 해당 major_id가 반환되도록 구상하였다.

        // ex) residentNum , 13자리 연속된 숫자
        //초기 pw 는 주민번호 뒷자리 7자리로 등록시 받는다.
        String password = residentNum.substring(6, 13);
        Major major = majorRepository.findById(majorId).orElse(null);

        if(major == null){
            throw new RuntimeException();
        }
        Student stu = Student.builder()
                .name(stuName)
                .studentNum(stuNum)
                .password(password)
                .residentNum(residentNum)
                .major(major)
                .build();

        Student saved = studentRepository.save(stu);
        return saved;
    }


    /**
     * 학사 관리자가 교수를 등록한다. DB에 저장한다.
     * @param profName 교수 이름
     * @param profNum 교수 번호
     * @param residentNum 교수 주민등록 번호 13자리
     * @param majorId 교수 전공 id
     * @return 저장된 교수 Data
     * @throws DbInsertWrongParamException 교수 등록시 , 필수 데이터가 없으면 throw 되는 예외
     */
    public Professor profRegister(String profName , int profNum , String residentNum , Long majorId) throws DbInsertWrongParamException {

        // ex) residentNum , 13자리 연속된 숫자
        //초기 pw 는 주민번호 뒷자리 7자리로 등록시 받는다.
        String password = residentNum.substring(6, 13);
        Major major = majorRepository.findById(majorId).orElse(null);

        Professor prof = Professor.builder()
                .name(profName)
                .professorNum(profNum)
                .password(password)
                .residentNum(residentNum)
                .major(major)
                .build();

        Professor saved = professorRepository.save(prof);
        return saved;
    }

    /**
     * 학사 직원이 수업 담당자를 지정한다.
     * @param staffName 수업 담당자 이름
     * @param staffNum 수업 담당자 번호
     * @param residentNum 수업 담당자 주민 번호
     * @return DB에 저장된 수업 담당자 data
     * @throws DbInsertWrongParamException 수업담당자 등록시 , 필수 데이터가 없으면 throw 되는 예외
     */
    public ClassStaff staffRegister(String staffName , int staffNum , String residentNum ) throws DbInsertWrongParamException {

        // ex) residentNum , 13자리 연속된 숫자
        //초기 pw 는 주민번호 뒷자리 7자리로 등록시 받는다.
        String password = residentNum.substring(6, 13);

        ClassStaff staff = ClassStaff.builder()
                .name(staffName)
                .staffNum(staffNum)
                .password(password)
                .residentNum(residentNum)
                .build();

        ClassStaff saved = classStaffRepository.save(staff);
        return saved;
    }

}
