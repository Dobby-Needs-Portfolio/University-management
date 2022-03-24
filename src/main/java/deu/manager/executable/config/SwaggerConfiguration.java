package deu.manager.executable.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

//https://daddyprogrammer.org/post/313/swagger-api-doc/
//https://nahwasa.com/entry/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-Swagger-UI-292-300-%EB%A7%88%EC%9D%B4%EA%B7%B8%EB%A0%88%EC%9D%B4%EC%85%98-%EB%B0%A9%EB%B2%95-Spring-Boot-Swagger-UI
//https://nahwasa.com/entry/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-Swagger-UI-300-%EC%A0%81%EC%9A%A9-%EB%B0%A9%EB%B2%95-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-22-%EC%9D%B4%EC%83%81-Spring-Boot-Swagger-UI?category=1050989
//https://kim-jong-hyun.tistory.com/49
@Configuration
@EnableWebMvc
public class SwaggerConfiguration {

    private ApiInfo swaggerInfo(){
        return new ApiInfoBuilder()
                .title("University Administration Manager")
                .version("0.1.1")
                .description("Application that manages university administration. RESTFul API support").build();
    }

    @Bean
    public Docket swaggerApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("deu.manager.executable.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

}
