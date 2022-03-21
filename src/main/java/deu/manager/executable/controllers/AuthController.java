package deu.manager.executable.controllers;

import deu.manager.executable.config.exception.request.WrongBodyRequestException;
import deu.manager.executable.services.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    UserAuthenticationService authenticationService;

    @Autowired
    public AuthController(UserAuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    /**
     * 로그인 메소드. 사용자가 로그인을 시도할 시 이 메소드로 리다이렉트됩니다. Path - POST /auth/login
     * @param requestBody POST 요청에 담길 body element. id, password 필드가 존재해야 합니다.
     * @return 로그인한 사용자의 토큰 값
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody Map<String, Object> requestBody
            ){

        String id = (String) requestBody.getOrDefault("id", null);
        String password = (String) requestBody.getOrDefault("password", null);

        if(id == null || password == null) throw new WrongBodyRequestException();

        String response = authenticationService.login(id, password);

        return ResponseEntity.ok(response);
    }

}