package deu.manager.executable.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import deu.manager.executable.config.enums.ExitCode;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// https://llshl.tistory.com/28
// https://needjarvis.tistory.com/595
// https://daddyprogrammer.org/post/636/springboot2-springsecurity-authentication-authorization/
@PropertySource("classpath:keylist.properties")
@Component
public class JwtTokenProvider {
    private final Logger log = LogManager.getLogger(this.getClass());

    private final Algorithm hmac384;
    private final JWTVerifier verifier;

    /**
     * Constructor of JwtTokenProvider. initializes hash mapper, jwt verifier.
     * Log4J2 dependency need to run this.
     * @param shutdownManager Spring application shutdown manager for shutdown on fatal error
     * @param privateKey privateKey received by properties file(keylist.properties)
     */
    @Autowired
    public JwtTokenProvider(
            ShutdownManager shutdownManager,
            @Value("${jwt.key}") String privateKey
    ) {
        if(privateKey == null) {
            log.fatal("no secret key for JWT detected, check keylist,properties to find jwt.key property");
            shutdownManager.shutdownInitiate(ExitCode.JWT_PRIVATEKEY_FETCH_FAILURE);
        }
        this.hmac384 = Algorithm.HMAC384(privateKey);
        // https://datatracker.ietf.org/doc/html/rfc7519#section-4.1
        // JWT Token Format
        // Header -> alg = HS384, typ = JWT
        // Payload -> aud = ${USER_ID}, exp = ${EXPIRE_TIME}, user_type = ${USER_TYPE}, roles = ${ROLES_ARRAY}
        this.verifier = JWT.require(hmac384)
                .acceptLeeway(5) // 시간 오차범위. +- 5초의 토큰 만료 예외 인정
                .withClaim("alg", "HS384") // 알고리즘은 반드시 HS384여야함
                .withClaim("typ", "JWT") // 토큰 타입은 반드시 JWT여야함
                .withClaimPresence("aud") // aud 클레임이 존재해야 함
                .withClaimPresence("exp") // exp 클레임이 존재해야 함
                .withClaimPresence("user_type") // user_type 클레임이 존재해야 함
                .withClaimPresence("roles") // roles 클레임이 존재해야 함
                .build();
        log.info("JWT token provider initialization completed");
    }

    /**
     * creates JWT token
     * @param userId the ID of user. should be database ID
     * @param roles the roles that this token can owns
     * @param userType type of user of this token owner. ex) student, staff_admin, professor
     * @return generated JWT token
     */
    public String createToken(Long userId, List<Roles> roles, String userType) {
        return null;
    }

    /**
     * Authentication 객체 생성. filter에서 반드시 비교해야 하며, 리턴되는 Authentication은 ID 정보만 담고 있음
     * @param token 검사하고자 하는 토큰
     * @return 사용자의 db id가 담긴 Authentication 객체 클래스. Credential은 비어있음
     * @throws JWTVerificationException 토큰 인증에 문제가 있을 시 throw됨. 반드시 filter 레벨에서 catch를 통해 토큰 오류를 검사할 것
     */
    // 반드시 JWTVerificationException의 SubException을 catch해야함
    public Authentication getAuthentication(String token) throws JWTVerificationException {
        return null;
    }

    public UserType getUserType(String token) throws NullPointerException{
        DecodedJWT decodedJWT = JWT.decode(token);
        Map<String, Claim> claims = decodedJWT.getClaims();
        return UserType.valueToUserType(claims.get("user_type").asString());
    }
}

