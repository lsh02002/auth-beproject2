package me.seho.authbeproject2.web.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.repository.userDetails.CustomUserDetails;
import me.seho.authbeproject2.service.authService.AuthService;
import me.seho.authbeproject2.web.dto.auth.LoginRequest;
import me.seho.authbeproject2.web.dto.auth.AuthResponseDto;
import me.seho.authbeproject2.web.dto.auth.SignupRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public AuthResponseDto signUp(@RequestBody SignupRequest signupRequest){
        return authService.signUp(signupRequest);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){
        List<Object> tokenAndResponse = authService.login(loginRequest);
        httpServletResponse.setHeader("Token", (String) tokenAndResponse.get(0));
        return (AuthResponseDto) tokenAndResponse.get(1);
    }

    @GetMapping("/test")
    public Object test(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return customUserDetails.toString();
    }
}
