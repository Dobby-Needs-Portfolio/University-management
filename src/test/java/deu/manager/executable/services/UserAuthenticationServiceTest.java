package deu.manager.executable.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "UserAuthenticationServiceTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@PropertySource("classpath:keylist.properties")
public class UserAuthenticationServiceTest {
    @Autowired UserAuthenticationService authenticationService;
    @Value(value = "${jwt.key}") String secretKey;

    @Autowired    StudentRepository studentRepository;
    @Autowired    AdminStaffRepository adminStaffRepository;
    @Autowired    ClassStaffRepository classStaffRepository;
    @Autowired    ProfessorRepository professorRepository;


    Logger log = LogManager.getLogger(this.getClass());

    @Test @DisplayName("Test ID Regex Filter")
    public void regexFilterIdTest(){
        assertThatThrownBy(() -> {
            authenticationService.login("A123", "1231235");
            authenticationService.login("GG20", "1231235");
            authenticationService.login("GP210", "1231235");
            authenticationService.login("G12", "1231235");
            authenticationService.login("G1236", "1231235");
        }).isInstanceOf(RegexNotMatchException.class);
    }

    @Test @DisplayName("Test Password Regex Filter")
    public void regexFilterPasswordTest(){
        assertThatThrownBy(() -> {
            authenticationService.login("G123", "%&$123G");
            authenticationService.login("G123", "1231235G");
            authenticationService.login("G123", "12312");
            authenticationService.login("G123", "A 1235J");
        }).isInstanceOf(RegexNotMatchException.class);
    }

    @Test @DisplayName("Wrong ID Password Match Test")
    public void idPasswordMatchTest(){
        assertThatThrownBy(() -> {
            authenticationService.login("H110", "adminpw");
            authenticationService.login("G002", "staffpw");
            authenticationService.login("P003", "profpw1");
            authenticationService.login("S003", "studpw1");

            authenticationService.login("H111", "adminpp");
            authenticationService.login("G001", "staffpp");
            authenticationService.login("P002", "profpw1");
            authenticationService.login("S002", "studpw1");
        }).isInstanceOf(WrongIdPasswordException.class);
    }

    @Test @DisplayName("Login Success Test")
    public void loginSuccess(){
        // TODO: Roles 추가 시 테스트에 있는 Roles 또한 추가할 것

        JWTVerifier verifier = JWT.require(Algorithm.HMAC384(secretKey))
                .withAudience() // aud 클레임이 존재해야 함
                .acceptExpiresAt(5L)
                //.withClaimPresence("exp") // exp 클레임이 존재해야 함 //이거 쓰니까 Expired를 감지를 못함, 그냥 자르자
                .withClaimPresence("user_type") // user_type 클레임이 존재해야 함
                .withClaimPresence("roles") // roles 클레임이 존재해야 함
                .build();

        // 어드민 로그인 시도
        {
            String token = authenticationService.login("H111", "adminpw");
            log.info("Admin login token - " + token);

            DecodedJWT verifiedToken = verifier.verify(token);
            AdminStaff searched = adminStaffRepository.findByStaffNum(111).orElseThrow(RuntimeException::new);
            // Audience check
            assertThat(verifiedToken.getAudience().stream().findAny().orElseThrow(RuntimeException::new))
                    .isEqualTo(searched.getId().toString());
            Map<String, Claim> tokenClaims = verifiedToken.getClaims();

            // user_type check
            assertThat(tokenClaims.get("user_type").asString())
                    .isEqualTo(UserType.AdminStaff.getValue());

            // roles check
            assertThat(tokenClaims.get("roles").asArray(String.class))
                    .containsAll(Arrays.asList(Roles.USER.getValue(), Roles.ADMIN.getValue()));
        }
        // 수업 직원 로그인 시도
        {
            String token = authenticationService.login("G001", "staffpw");
            log.info("ClassStaff login token - " + token);

            DecodedJWT verifiedToken = verifier.verify(token);
            ClassStaff searched = classStaffRepository.findByStaffNum(001).orElseThrow(RuntimeException::new);
            // Audience check
            assertThat(verifiedToken.getAudience().stream().findAny().orElseThrow(RuntimeException::new))
                    .isEqualTo(searched.getId().toString());
            Map<String, Claim> tokenClaims = verifiedToken.getClaims();

            // user_type check
            assertThat(tokenClaims.get("user_type").asString())
                    .isEqualTo(UserType.ClassStaff.getValue());

            // roles check
            assertThat(tokenClaims.get("roles").asArray(String.class))
                    .containsAll(Arrays.asList(Roles.USER.getValue(), Roles.ADMIN.getValue()));
        }
        // 교수 로그인 시도
        {
            String token = authenticationService.login("P001", "profpw1");
            log.info("Professor login token - " + token);
            Professor searched = professorRepository.findByProfessorNum(1).orElseThrow(RuntimeException::new);
            DecodedJWT verifiedToken = verifier.verify(token);

            // Audience check
            assertThat(verifiedToken.getAudience().stream().findAny().orElseThrow(RuntimeException::new))
                    .isEqualTo(searched.getId().toString());
            Map<String, Claim> tokenClaims = verifiedToken.getClaims();

            // user_type check
            assertThat(tokenClaims.get("user_type").asString())
                    .isEqualTo(UserType.Professor.getValue());

            // roles check
            assertThat(tokenClaims.get("roles").asArray(String.class))
                    .containsAll(Arrays.asList(Roles.USER.getValue()));
        }
        // 학생 로그인 시도
        {
            String token = authenticationService.login("S001", "studpw1");
            log.info("Student login token - " + token);

            DecodedJWT verifiedToken = verifier.verify(token);
            Student searched = studentRepository.findByStudentNum(1).orElseThrow(RuntimeException::new);
            // Audience check
            assertThat(verifiedToken.getAudience().stream().findAny().orElseThrow(RuntimeException::new))
                    .isEqualTo(searched.getId().toString());
            Map<String, Claim> tokenClaims = verifiedToken.getClaims();

            // user_type check
            assertThat(tokenClaims.get("user_type").asString())
                    .isEqualTo(UserType.Student.getValue());

            // roles check
            assertThat(tokenClaims.get("roles").asArray(String.class))
                    .containsAll(Arrays.asList(Roles.USER.getValue()));
        }
    }

}