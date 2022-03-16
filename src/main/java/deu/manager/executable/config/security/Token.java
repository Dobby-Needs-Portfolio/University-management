package deu.manager.executable.config.security;

import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.enums.UserType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class Token {
    /**
     * 토큰을 소유한 주체. 사용자의 DB id를 담고 있습니다.
     */
    private String aud;

    /**
     * 토큰을 소유한 주체의 유저 타입을 나타냅니다.(ex. student, admin_staff)
     */
    private UserType userType;

    /**
     * 토큰을 소유한 주체의 권한을 나타냅니다.
     */
    private List<Roles> roles;
}
