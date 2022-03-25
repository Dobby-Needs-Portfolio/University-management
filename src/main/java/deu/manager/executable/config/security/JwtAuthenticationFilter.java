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
import javax.servlet.http.HttpServletRequest;
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
            String token = ((HttpServletRequest) request).getHeader("Authorization");
            // token이 존재하지 않을 경우 권한을 부여하지 않고 진행한다
            if (token != null){
                Authentication auth = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
        }
        catch (ClassCastException e){
            log.warn("ClassCastException occurred, Unacceptable request received from - " + request.getRemoteHost(), e);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        // JWT Exception
        // Error code is specified in Bearer token ietf 5-2
        // https://datatracker.ietf.org/doc/html/rfc6749#section-5.2
        catch (InvalidClaimException e){
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid_scope");
        }
        catch (TokenExpiredException | SignatureVerificationException e){
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid_grant");
        }
        catch (JWTVerificationException e){
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "internal_token_error");
        }
    }
}
