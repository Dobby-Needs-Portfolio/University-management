package deu.manager.executable.config.security;

import deu.manager.executable.config.JwtTokenProvider;
import deu.manager.executable.config.enums.Roles;
import deu.manager.executable.config.security.JwtAuthenticationFilter;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// https://daddyprogrammer.org/post/636/springboot2-springsecurity-authentication-authorization/
@RequiredArgsConstructor
@Configuration
@Api(tags = {"Auth"}, value = "user Authentication API")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider tokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Override //Set security Network filter configuration
    protected void configure(HttpSecurity http) throws Exception{
        http
                // 비인증 상태에서의 로그인 리다이렉팅 기능 사용 안함
                .httpBasic().disable()
                // csrf 보안 해제. 필요하다면 다시 켜야 함
                .csrf().disable()
                // 토큰제이기 때문에 세션이 필요하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 리퀘스트 Authorization 매핑
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()     //도메인 수정 - 도메인은 "/"으로 시작해야 함
                .antMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
                .anyRequest().hasRole(Roles.USER.getValue())
                .and()
                // 필터 설정. JWT토큰 필터가 먼저 실행되고, 그 다음 UsernamePassword 필터가 실행됨
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    }


    @Override //Ignore swagger resource
    public void configure(WebSecurity web){
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui/index.html",
                        "/swagger-ui/index", "/swagger-ui.html", "/webjars/**", "/swagger/**")
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }
}