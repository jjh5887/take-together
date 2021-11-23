package me.powerarc.taketogether.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());

        ExceptionResponse responseMessage =
                ExceptionResponse.builder().status(HttpStatus.FORBIDDEN.value()).message("인증이 필요합니다").build();
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        objectMapper.writeValue(outputStream, responseMessage);
        outputStream.flush();
    }
}
