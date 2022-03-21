package deu.manager.executable.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import deu.manager.executable.config.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Sql(value = "TestExampleInsertSql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//https://imbf.github.io/spring/2020/08/18/Test-Spring-MVC-Controller-Applying-Spring-Security.html
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired    MockMvc mvc;
    @Autowired    ObjectMapper objectMapper;
    // "/auth/login" test
    @Test @DisplayName("[POST] Login API Mockup test")
    public void loginApi() throws Exception{
        {
            String content = "{\"id\":\"H111\", \"password\":\"adminpw\"}";
            HttpHeaders reqHeader = new HttpHeaders();
            // ContentType, Accept를 등록하지 않으면 에러가 남. 서버가 어떤 형식으로 리턴해야 할 지 모르기 때문.
            reqHeader.add("Content-Type", "application/json");
            reqHeader.add("Accept", "*/*");

            mvc.perform(post("/auth/login").headers(reqHeader).content(content))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
        {
            String content = "{\"id\":\"G001\", \"password\":\"staffpw\"}";
            HttpHeaders reqHeader = new HttpHeaders();
            reqHeader.add("Content-Type", "application/json");
            reqHeader.add("Accept", "*/*");

            mvc.perform(post("/auth/login").headers(reqHeader).content(content))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
        {
            String content = "{\"id\":\"P001\", \"password\":\"profpw1\"}";
            HttpHeaders reqHeader = new HttpHeaders();
            reqHeader.add("Content-Type", "application/json");
            reqHeader.add("Accept", "*/*");

            mvc.perform(post("/auth/login").headers(reqHeader).content(content))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
        {
            String content = "{\"id\":\"S002\", \"password\":\"studpw2\"}";
            HttpHeaders reqHeader = new HttpHeaders();
            reqHeader.add("Content-Type", "application/json");
            reqHeader.add("Accept", "*/*");

            mvc.perform(post("/auth/login").headers(reqHeader).content(content))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }


}
