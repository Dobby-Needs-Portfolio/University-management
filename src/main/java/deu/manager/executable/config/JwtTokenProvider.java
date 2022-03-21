package deu.manager.executable.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import deu.manager.executable.config.enums.ExitCode;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import deu.manager.executable.config.security.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// https://llshl.tistory.com/28
// https://needjarvis.tistory.com/595
// https://daddyprogrammer.org/post/636/springboot2-springsecurity-authentication-authorization/
@PropertySource("classpath:keylist.properties")
@Component
public class JwtTokenProvider {
    private final Logger log = LogManager.getLogger(this.getClass());

    private final Algorithm hmac384;
    private final JWTVerifier verifier;
    private final Long expireTime;

    /**
     * Constructor of JwtTokenProvider. initializes hash mapper, jwt verifier.
     * Log4J2 dependency need to run this.
     * @param shutdownManager Spring application shutdown manager for shutdown on fatal error
     * @param privateKey privateKey received by properties file(keylist.properties)
     */
    @Autowired
    public JwtTokenProvider(
            ShutdownManager shutdownManager,
            @Value(value = "${jwt.key}") String privateKey,
            @Value(value = "${jwt.expireMin}") Long expireMinute
    ) {
        if(privateKey == null || expireMinute == null) {
            log.fatal("no secret key or expire time for JWT detected, check keylist,properties to find jwt.key, jwt.expireMin property");
            shutdownManager.shutdownInitiate(ExitCode.JWT_PRIVATEKEY_FETCH_FAILURE);
        }
        this.expireTime = expireMinute;
        this.hmac384 = Algorithm.HMAC384(privateKey);
        // https://datatracker.ietf.org/doc/html/rfc7519#section-4.1
        // JWT Token Format
        // Header -> alg = HS384, typ = JWT
        // Payload -> aud = ${USER_ID}, exp = ${EXPIRE_TIME}, user_type = ${USER_TYPE}, roles = ${ROLES_ARRAY}
        this.verifier = JWT.require(hmac384)
                //.acceptLeeway(5) // 시간 오차범위. +- 5초의 토큰 만료 예외 인정
                .withAudience() // aud 클레임이 존재해야 함
                .acceptExpiresAt(5L)
                //.withClaimPresence("exp") // exp 클레임이 존재해야 함 //이거 쓰니까 Expired를 감지를 못함, 그냥 자르자
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
    public String createToken(Long userId, List<Roles> roles, UserType userType) {
        LocalDateTime expire = LocalDateTime.now().plusMinutes(expireTime); // Generates expire time: current time + expireTime
        String[] mappedRoles = roles.stream().map(Roles::getValue).toArray(String[]::new); // Map all roles value to String Array

        return JWT.create()
                .withAudience(userId.toString())                      // 토큰을 가지고 있을 사용자 정보. DB id
                .withExpiresAt(Timestamp.valueOf(expire))             // 토큰의 만료 시간
                .withClaim("user_type", userType.getValue())    // 사용자 종류. 사용하는 service에 차이가 생긴다.
                .withArrayClaim("roles", mappedRoles)           // 권한
                .sign(hmac384);                                       // HS384로 암호화
    }

    /**
     * Authentication 객체 생성. filter에서 반드시 비교해야 하며, 리턴되는 Authentication은 ID 정보만 담고 있음
     * @param token 검사하고자 하는 토큰
     * @return 사용자의 db id가 담긴 Authentication 객체 클래스. Credential은 비어있음
     * @throws JWTVerificationException 토큰 인증에 문제가 있을 시 throw됨. 반드시 filter 레벨에서 catch를 통해 토큰 오류를 검사할 것
     */
    // 반드시 JWTVerificationException의 SubException을 catch해야함
    public Authentication getAuthentication(String token) throws JWTVerificationException {
        DecodedJWT verifiedJwt = verifier.verify(token);        // Decode JWT
        Map<String, Claim> claims = verifiedJwt.getClaims();    // get claims as map

        Optional<String> audience = verifiedJwt.getAudience().stream().findAny();  // extract audience from token
        UserType userType = UserType.valueToUserType(claims.get("user_type").asString());  // extract userType and map as UserType type
        List<Roles> roles = claims.get("roles").asList(String.class).stream()
                .map(Roles::valueToRoles).collect(Collectors.toList());//extract roles and map as Roles type

        if (roles.isEmpty()) {
            throw new InvalidClaimException("roles are not present");
        }
        Token authGeneratedToken = Token.builder().aud(audience.orElseThrow(() -> new InvalidClaimException("aud is not present")))
                .userType(userType).roles(roles).build();

        return new UsernamePasswordAuthenticationToken(authGeneratedToken, "", authGeneratedToken.getAuthorities());
    }

    public UserType getUserType(String token) throws NullPointerException{
        DecodedJWT decodedJWT = JWT.decode(token);
        Map<String, Claim> claims = decodedJWT.getClaims();
        return UserType.valueToUserType(claims.get("user_type").asString());
    }
}

