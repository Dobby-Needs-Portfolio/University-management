package deu.manager.executable.config;


import deu.manager.executable.config.exception.authentication.RegexNotMatchException;
import deu.manager.executable.config.exception.authentication.WrongIdPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class ControllerExceptionAdvisor {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected String defaultExceptionHandler(HttpServletRequest request, Exception e){
        return "Unexpected error occurred in internal server";
    }

    @ExceptionHandler(WrongIdPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String loginFailureExceptionHandler(HttpServletRequest request, Exception e){
        return "Id not exists or password is wrong";
    }

    @ExceptionHandler(RegexNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String regexNotMatchExceptionHandler(HttpServletRequest request, Exception e){
        return "Password OR ID's format is wrong";
    }



}
