package deu.manager.executable.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC 모델 테스트 - Controller Class
 * Controller Unit test를 시행하기 위한 기본적인 Architecture를 제공합니다.
 * 모든 Controller Test는 해당 Architecture를 기반으로 작성해 주시기 바랍니다.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TestController.class)
public class TestControllerTest {


    //Request를 실행할 MockMvc 클래스의 메소드를 사용하기 위해 DI를 시행합니다.
    @Autowired
    MockMvc mvc;

    /**
     * Get API Test
     */
    @Test
    public void get_Test() throws Exception{
        String expect = "this is test";
        mvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().string(expect));
    }

    /**
     * Post API Test
     * HttpHeaders 클래스를 사용해서 Header 생성 후
     * post()메소드로 리턴된 Request Builder의 headers 등을 수정
     */
    @Test
    public void post_Test() throws Exception{
        String expect = "{" +
                "\"response\" : " +
                "\"Hello, this is the test, requested by POST method\"" +
                "}";

        HttpHeaders reqHeader = new HttpHeaders();
        reqHeader.add("Authorization", "TESTTOKEN");

        mvc.perform(post("/test").headers(reqHeader))
                .andExpect(status().isOk())
                .andExpect(content().json(expect));
    }
}
