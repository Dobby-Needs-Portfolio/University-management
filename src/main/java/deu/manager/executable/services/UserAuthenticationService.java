package deu.manager.executable.services;

import deu.manager.executable.config.JwtTokenProvider;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import deu.manager.executable.config.exception.authentication.RegexNotMatchException;
import deu.manager.executable.config.exception.authentication.WrongIdPasswordException;
import deu.manager.executable.domain.AdminStaff;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.AdminStaffRepository;
import deu.manager.executable.repository.interfaces.ClassStaffRepository;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserAuthenticationService {

    private final JwtTokenProvider tokenProvider;

    // Repository
    private final StudentRepository studentRepository;
    private final AdminStaffRepository adminStaffRepository;
    private final ClassStaffRepository classStaffRepository;
    private final ProfessorRepository professorRepository;

    @Autowired
    public UserAuthenticationService(
            StudentRepository studentRepository,
            AdminStaffRepository adminStaffRepository,
            ClassStaffRepository classStaffRepository,
            ProfessorRepository professorRepository,
            JwtTokenProvider tokenProvider){
        this.studentRepository = studentRepository;
        this.adminStaffRepository = adminStaffRepository;
        this.classStaffRepository = classStaffRepository;
        this.professorRepository = professorRepository;
        this.tokenProvider = tokenProvider;
    }

    // Regex filtered id will be input with encrypted password
    // https://regexr.com/6hhvd

    /**
     * 로그인을 시도하고 성공 시 토큰을 반환, 실패 시 예외를 throw하는 메소드
     * @param id 사용자의 id, G102 등의 형식으로 입력됩니다.
     * @param password 사용자가 로그인을 하기 위해 입력한 패스워드. 올바른 패스워드일 시 토큰을 반환합니다.
     * @return 올바른 사용자 정보로 생성된 사용자 JWT 토큰
     * @throws WrongIdPasswordException ID 또는 비밀번호가 잘못됬을 시 throw되는 예외
     * @throws RegexNotMatchException ID 또는 비밀번호가 입력 포맷에 맞지 않을 시 throw되는 예외
     */
    public String login(String id, String password) throws WrongIdPasswordException, RegexNotMatchException {
        // ID Regexp check
        if(!id.matches("^[SPHG][0-9]{3}$")) throw new RegexNotMatchException();
        if(!password.matches("^[0-9a-zA-Z]{7}$")) throw new RegexNotMatchException();

        String userType = id.substring(0,1);
        int userNum = Integer.parseInt(id.substring(1, 4));

        // AdminStaff Login logic
        if(userType.equals("H")){
            AdminStaff searched = adminStaffRepository.findByStaffNum(userNum).orElseThrow(WrongIdPasswordException::new);
            if(!password.equals(searched.getPassword())) throw new WrongIdPasswordException();

            return tokenProvider.createToken(
                    searched.getId(),
                    Arrays.asList(Roles.ADMIN, Roles.USER), // TODO: Roles 추가시 같이 추가할 것
                    UserType.AdminStaff);
        }

        // ClassStaff Login logic
        if(userType.equals("G")){
            ClassStaff searched = classStaffRepository.findByStaffNum(userNum).orElseThrow(WrongIdPasswordException::new);
            if(!password.equals(searched.getPassword())) throw new WrongIdPasswordException();

            return tokenProvider.createToken(
                    searched.getId(),
                    Arrays.asList(Roles.ADMIN, Roles.USER), // TODO: Roles 추가시 같이 추가할 것
                    UserType.ClassStaff);
        }

        // Professor Login logic
        if(userType.equals("P")){
            Professor searched = professorRepository.findByProfessorNum(userNum).orElseThrow(WrongIdPasswordException::new);
            if(!password.equals(searched.getPassword())) throw new WrongIdPasswordException();

            return tokenProvider.createToken(
                    searched.getId(),
                    Arrays.asList(Roles.USER), // TODO: Roles 추가시 같이 추가할 것
                    UserType.Professor);
        }

        if(userType.equals("S")){
            Student searched = studentRepository.findByStudentNum(userNum).orElseThrow(WrongIdPasswordException::new);
            if(!password.equals(searched.getPassword())) throw new WrongIdPasswordException();

            return tokenProvider.createToken(
                    searched.getId(),
                    Arrays.asList(Roles.USER), // TODO: Roles 추가시 같이 추가할 것
                    UserType.Student);
        }

        throw new RuntimeException("Regex has passed but there is no user type matches on if phrase");
    }

}
