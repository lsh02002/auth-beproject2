package me.seho.authbeproject2.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.getAccessAndRefreshTokenCookies(request).get(0);
        if(accessToken != null){
            response.sendRedirect("/auth/entrypoint?token=" + accessToken);
        }else{
            response.sendRedirect("/auth/entrypoint");
        }
    }
}
