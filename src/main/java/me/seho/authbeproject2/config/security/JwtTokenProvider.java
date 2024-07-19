package me.seho.authbeproject2.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.repository.users.refreshToken.RefreshToken;
import me.seho.authbeproject2.repository.users.refreshToken.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    @Value("lsh02002aa-lsh02002aa")
    private String keySource;

    private String key;

    @PostConstruct
    public void setUp(){
        key = Base64.getEncoder().encodeToString(keySource.getBytes());
    }

    public boolean validateToken(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(key).parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        }catch (Exception e){
            log.warn(e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token){
        if(!validateToken(token)) return false;
        // DB에 저장한 토큰 비교
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(()->new RuntimeException("토큰을 찾을 수 없습니다."));
        return refreshToken != null && token.equals(refreshToken.getRefreshToken());
    }

    public Authentication getAuthentication(String token){
        String email = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createAccessToken(String email){
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setSubject(email)
                .setExpiration(new Date(now.getTime() + 1000L * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))//유효시간 (3일)
                .signWith(SignatureAlgorithm.HS256, key) //HS256알고리즘으로 key를 암호화 해줄것이다.
                .compact();
    }

    public String getEmail(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    public void setAccessTokenCookies(HttpServletResponse response, String accessToken){
        Cookie myCookie = new Cookie("accessToken", accessToken);
        myCookie.setMaxAge(60 * 60 * 24 * 14);
        myCookie.setPath("/");
        myCookie.setHttpOnly(true);
        response.addCookie(myCookie);
    }

    public void setRefreshTokenCookies(HttpServletResponse response, String refreshToken){
        Cookie myCookie = new Cookie("refreshToken", refreshToken);
        myCookie.setMaxAge(60 * 60 * 24 * 14);
        myCookie.setPath("/");
        myCookie.setHttpOnly(true);
        response.addCookie(myCookie);
    }
}
