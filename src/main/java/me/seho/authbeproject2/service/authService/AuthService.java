package me.seho.authbeproject2.service.authService;

import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.config.security.JwtTokenProvider;
import me.seho.authbeproject2.repository.userRoles.Roles;
import me.seho.authbeproject2.repository.userRoles.RolesRepository;
import me.seho.authbeproject2.repository.userRoles.UserRoles;
import me.seho.authbeproject2.repository.userRoles.UserRolesRepository;
import me.seho.authbeproject2.repository.users.User;
import me.seho.authbeproject2.repository.users.UserRepository;
import me.seho.authbeproject2.web.dto.auth.LoginRequest;
import me.seho.authbeproject2.web.dto.auth.AuthResponseDto;
import me.seho.authbeproject2.web.dto.auth.SignupRequest;
import me.seho.authbeproject2.web.dto.auth.SignupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final UserRolesRepository userRolesRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDto signUp(SignupRequest signupRequest){
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();

        signupRequest.setPassword(passwordEncoder.encode(password));

        Roles roles = rolesRepository.findByName("ROLE_USER");

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .nickName(signupRequest.getNickName())
                .build();

        userRepository.save(user);

        userRolesRepository.save(UserRoles.builder()
                        .user(user)
                        .roles(roles)
                .build());

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .build();

        return new AuthResponseDto(HttpStatus.OK.value(), user.getNickName() + "님 회원 가입 완료 되었습니다.", signupResponse);
    }

    public List<Object> login(LoginRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new Exception("계정을 찾을 수 없습니다."));
        String p1 = user.getPassword();

        if(!passwordEncoder.matches(request.getPassword(), p1)){
            throw new Exception("계정을 찾을 수 없습니다");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = user.getUserRoles().stream()
                .map(u->u.getRoles()).map(r->r.getName()).toList();

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .build();

        AuthResponseDto authResponseDto = new AuthResponseDto(HttpStatus.OK.value(), "로그인에 성공 하였습니다.", signupResponse);

        return Arrays.asList(jwtTokenProvider.createToken(user.getEmail()), authResponseDto);
    }
}
