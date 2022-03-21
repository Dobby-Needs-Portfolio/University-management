package deu.manager.executable.config;

import com.auth0.jwt.exceptions.*;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import deu.manager.executable.config.security.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenProviderTest {

    private final String secretKey = "testKey";
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(null,secretKey, 1L);

    Logger logger = LogManager.getLogger(this.getClass());


    @Test
    @DisplayName("Create Token Test")
    public void CreateTokenTest(){
        String token = jwtTokenProvider.createToken(412L, Arrays.asList(Roles.ADMIN, Roles.USER), UserType.AdminStaff);

        System.out.println(token); //jwt.io에서 검사해야 합니다.
    }

    @Test
    @DisplayName("getAuthentication Method Test - success")
    public void getAuthentication_Test(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJhdWQiOiI0MTIiLCJ1c2VyX3R5cGUiOiJzdGFmZl9hZG1pbiIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJleHAiOjgxMTQwODc0MDZ9.SpnuxhOIhtM4sEc92jZryWI-LtiEW4UFzdZ7fENXGUbG8R2O7tZjwQsIqQc_3Qdp";

        Authentication auth = jwtTokenProvider.getAuthentication(token);
        Token targetToken = Token.builder()
                .aud("412")
                .roles(Arrays.asList(Roles.ADMIN, Roles.USER))
                .userType(UserType.AdminStaff).build();
        Authentication targetAuth = new UsernamePasswordAuthenticationToken(targetToken, "", targetToken.getAuthorities());

        assertThat(auth).usingRecursiveComparison().isEqualTo(targetAuth);
    }

    @Test
    @DisplayName("getAuthentication Method test - exception(invalid token)")
    public void getAuthenticationInvalid(){
        // #1. Wrong payload token
        {
            String wrongToken = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoxOTE2MjM5MDIyfQ.8Pn71yx--oWbM05v-rC-0VRpnv8P-b-NhYGXka0I3L4vDSMrjkDkJULD6QoQCVf5";
            assertThatThrownBy(() ->
                    jwtTokenProvider.getAuthentication(wrongToken))
                    .isInstanceOf(InvalidClaimException.class);
        }
        // #2. Wrong signature token
        {
            String wrongToken = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIyMjAiLCJleHAiOjE5MTYyMzkwMjIsInVzZXJfdHlwZSI6InN0YWZmX2FkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdfQ.0OmLvqOXzJINLNAYUzo9b7fEpmX3gEWEMFhPlkgsUqgml61er-ocgTWR2M8SbroW";
            assertThatThrownBy(() ->
                    jwtTokenProvider.getAuthentication(wrongToken))
                    .isInstanceOf(SignatureVerificationException.class);
        }
        // #3. Wrong algorithm type
        {
            String wrongToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIyMjAiLCJleHAiOjE5MTYyMzkwMjIsInVzZXJfdHlwZSI6InN0YWZmX2FkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdfQ.kJX0OB2BzNAIr5YJ13WfL1q3lKcaMIOPdEI-suuk1f8";
            assertThatThrownBy(() ->
                jwtTokenProvider.getAuthentication(wrongToken))
                    .isInstanceOf(AlgorithmMismatchException.class);
        }
        // #4. Time expired token
        {
            String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJhdWQiOiI0MTIiLCJ1c2VyX3R5cGUiOiJzdGFmZl9hZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwiZXhwIjoxNjQ3NDU1MDExfQ.t0egZRCsA2buAZ01_RVnvz_f_UNRGB1z2uSo-v2StooKMgVntFGxlHrHN7dnb1Db";

            assertThatThrownBy(() ->
                jwtTokenProvider.getAuthentication(wrongToken))
                    .isInstanceOf(TokenExpiredException.class);
        }
    }


    @Test
    @DisplayName("check User Type")
    public void getUserType_Test(){

        String token = jwtTokenProvider.createToken(1L, Collections.singletonList(Roles.ADMIN), UserType.AdminStaff);

        UserType userType = jwtTokenProvider.getUserType(token);
        assertThat(userType).isEqualTo(UserType.AdminStaff);
    }

}
