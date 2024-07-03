package me.seho.authbeproject2.web.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.repository.userDetails.CustomUserDetails;
import me.seho.authbeproject2.service.authService.AuthService;
import me.seho.authbeproject2.web.dto.auth.LoginRequest;
import me.seho.authbeproject2.web.dto.auth.ResponseDto;
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
    public ResponseDto signUp(@RequestBody SignupRequest signupRequest){
        return authService.signUp(signupRequest);
    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) throws Exception{
        List<Object> tokenAndResponse = authService.login(loginRequest);
        httpServletResponse.setHeader("Token", (String) tokenAndResponse.get(0));
        return (ResponseDto) tokenAndResponse.get(1);
    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return customUserDetails.getNickName();
    }
}
