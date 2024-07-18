package me.seho.authbeproject2.web.filters;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.config.security.JwtTokenProvider;
import me.seho.authbeproject2.repository.users.User;
import me.seho.authbeproject2.repository.users.UserRepository;
import me.seho.authbeproject2.repository.users.refreshToken.RefreshToken;
import me.seho.authbeproject2.repository.users.refreshToken.RefreshTokenRepository;
import me.seho.authbeproject2.service.exceptions.BadRequestException;
import me.seho.authbeproject2.service.exceptions.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.getAccessAndRefreshTokenCookies(request).get(0);
        String refreshToken = jwtTokenProvider.getAccessAndRefreshTokenCookies(request).get(1);

        if(accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (refreshToken != null) {
                if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    String email = jwtTokenProvider.getEmail(refreshToken);
                    String newAccessToken = jwtTokenProvider.createAccessToken(email);

                    jwtTokenProvider.setAccessTokenCookies(response, newAccessToken);

                    Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
