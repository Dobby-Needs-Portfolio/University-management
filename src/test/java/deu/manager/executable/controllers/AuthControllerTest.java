package deu.manager.executable.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;


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
            // ContentType, Accept를 등록하지 않으면 에러가 남. 서버가 어떤 형식으로 리턴해야 할 지 모르기때문.
            // https://webstone.tistory.com/66
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
