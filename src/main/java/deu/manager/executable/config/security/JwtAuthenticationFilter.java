package deu.manager.executable.config.security;

import com.auth0.jwt.exceptions.*;
import deu.manager.executable.config.JwtTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private final Logger log = LogManager.getLogger(this.getClass());
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try{
            String token = ((HttpServletResponse) request).getHeader("Authentication");

            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        catch (ClassCastException e){
            log.warn("ClassCastException occurred, Unacceptable request received from - " + request.getRemoteHost(), e);
        }
        // JWT Exception
        catch (InvalidClaimException e){
            log.warn("wrong format of token has received. InvalidClaimException occurred - ", e);
        }
        catch (TokenExpiredException e){
            log.warn("expired token has received from ("+ request.getRemoteAddr() +") TokenExpiredException occurred - " , e) ;
        }
        catch (SignatureVerificationException e){
            log.warn("Received token's signature is invalid - ", e);
        }
        catch (JWTVerificationException e){
            log.warn("unexpected JWT exception occurred - ", e);
        }
        chain.doFilter(request, response);
    }
}
