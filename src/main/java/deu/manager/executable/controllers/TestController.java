package deu.manager.executable.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Sample Architecture
 * <p>
 * Main Domain당 Controller 1개를 기준으로 작성해 주시기 바랍니다.
 * RESTful API 작성이 요구됩니다.
 * 반드시 Unit Test 코드를 먼저 작성하시고, 구현체를 작성하시기 바랍니다.
 * Controller는 JavaDoc 작성이 필요하지 않습니다.
 */
@Controller
public class TestController {

    @GetMapping("test")
    @ResponseBody
    public String getTest(Model model){
        return "this is test";
    }

    @PostMapping("test")
    @ResponseBody
    public TestResponse postTest(
            @RequestHeader(required = true, value = "Authorization")String token,
            Model model){
        TestResponse res = new TestResponse();
        res.setResponse("Hello, this is the test, requested by POST method");

        return res;
    }

    class TestResponse {
        private String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }


}
