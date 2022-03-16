package deu.manager.executable.config;

import com.auth0.jwt.exceptions.*;
import deu.manager.executable.SpringConfig;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.apache.catalina.core.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@RunWith(SpringJUnit4ClassRunner.class)
public class TokenProviderTest {

    private final String secretKey = "testKey";
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(null,secretKey);

    Logger logger = LogManager.getLogger(this.getClass());


    @Test
    @DisplayName("Create Token Test")
    public void CreateTokenTest(){

        String token = jwtTokenProvider.createToken(1L, Collections.singletonList(Roles.ADMIN), UserType.AdminStaff.getValue());

        System.out.println(token); //jwt.io에서 검사해야 합니다.

    }


    @Test
    @DisplayName("getAuthentication Method Test")
    public void getAuthentication_Test(){

        String token = jwtTokenProvider.createToken(1L, Collections.singletonList(Roles.ADMIN), UserType.AdminStaff.getValue());

        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // UserAuthToken과 DB의 데이터와 비교 해야하는 코드가 필요하다.


        }catch(AlgorithmMismatchException a){
            logger.warn("Algorithm Mis MatchException - Algorithm should be Algorithm.HMAC384");
        }catch(SignatureVerificationException s){
            logger.warn("SignatureVerificationException - Signature was forged");
        }catch(TokenExpiredException t){
            logger.warn("TokenExpiredException - Token is Expired");
        }catch(InvalidClaimException i){
            logger.warn("InvalidClaimException - Token might miss Essential Claims");
        }catch(JWTVerificationException j){
            logger.fatal("JWTVerificationException - There are Big errors in Token ");
        }


    }


    @Test
    @DisplayName("check User Type")
    public void getUserType_Test(){

        String token = jwtTokenProvider.createToken(1L, Collections.singletonList(Roles.ADMIN), UserType.AdminStaff.getValue());

        UserType userType = jwtTokenProvider.getUserType(token);
        assertThat(userType).isEqualTo(UserType.AdminStaff);
    }



}
