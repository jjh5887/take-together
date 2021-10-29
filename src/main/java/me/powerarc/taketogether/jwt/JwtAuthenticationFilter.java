package me.powerarc.taketogether.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());

                ExceptionResponse responseMessage =
                        ExceptionResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
                ServletOutputStream outputStream = httpServletResponse.getOutputStream();
                objectMapper.writeValue(outputStream, responseMessage);
                outputStream.flush();
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
