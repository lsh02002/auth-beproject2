package me.seho.authbeproject2.service.authService;

import lombok.RequiredArgsConstructor;
import me.seho.authbeproject2.config.security.JwtTokenProvider;
import me.seho.authbeproject2.repository.userRoles.Roles;
import me.seho.authbeproject2.repository.userRoles.RolesRepository;
import me.seho.authbeproject2.repository.userRoles.UserRoles;
import me.seho.authbeproject2.repository.userRoles.UserRolesRepository;
import me.seho.authbeproject2.repository.users.User;
import me.seho.authbeproject2.repository.users.UserRepository;
import me.seho.authbeproject2.service.exceptions.BadRequestException;
import me.seho.authbeproject2.service.exceptions.ConflictException;
import me.seho.authbeproject2.service.exceptions.CustomBadCredentialsException;
import me.seho.authbeproject2.service.exceptions.NotFoundException;
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

        if(!email.matches(".+@.+\\..+")){
            throw new BadRequestException("이메일을 정확히 입력해주세요.", email);
        } else if (signupRequest.getName().matches("01\\d{9}")){
            throw new BadRequestException("전화번호를 이름으로 사용할수 없습니다.",signupRequest.getName());
        }

        if(userRepository.existsByEmail(email)){
            throw new ConflictException("이미 입력하신 " + email + " 이메일로 가입된 계정이 있습니다.", email);
        } else if(signupRequest.getName().length()>30){
            throw new BadRequestException("이름은 30자리 이하여야 합니다.", signupRequest.getName());
        } else if(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())){
            throw new ConflictException("이미 입력하신 "+signupRequest.getPhoneNumber()+" 전화번호로 가입된 계정이 있습니다.",signupRequest.getPhoneNumber());
        }else if(!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$")
                ||!(password.length()>=8&&password.length()<=20)){
            throw new BadRequestException("비밀번호는 8자 이상 20자 이하 숫자와 영문소문자 조합 이어야 합니다.",password);
        } else if(!signupRequest.getPasswordConfirm().equals(password)) {
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.","password : "+password+", password_confirm : "+signupRequest.getPasswordConfirm());
        }

        signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Roles roles = rolesRepository.findByName("ROLE_USER");

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName())
                .phoneNumber(signupRequest.getPhoneNumber())
                .build();

        userRepository.save(user);

        userRolesRepository.save(UserRoles.builder()
                        .user(user)
                        .roles(roles)
                .build());

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        return new AuthResponseDto(HttpStatus.OK.value(), user.getName() + "님 회원 가입 완료 되었습니다.", signupResponse);
    }

    public List<Object> login(LoginRequest request) {
        if(request.getEmail()==null||request.getPassword()==null){
            throw new BadRequestException("이메일이나 비밀번호 값이 비어있습니다.","email : "+request.getEmail()+", password : "+request.getPassword());
        }
        User user;

        if(request.getEmail().matches(".+@.+\\..+")) {
            user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("입력하신 이메일의 계정을 찾을 수 없습니다.", request.getEmail()));
        } else {
            throw new BadRequestException("이메일이 잘못 입력되었습니다.", request.getEmail());
        }
        String p1 = user.getPassword();

        if(!passwordEncoder.matches(request.getPassword(), p1)){
            throw new CustomBadCredentialsException("비밀번호가 일치하지 않습니다.", request.getPassword());
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = user.getUserRoles().stream()
                .map(u->u.getRoles()).map(r->r.getName()).toList();

        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        AuthResponseDto authResponseDto = new AuthResponseDto(HttpStatus.OK.value(), "로그인에 성공 하였습니다.", signupResponse);

        return Arrays.asList(jwtTokenProvider.createToken(user.getEmail()), authResponseDto);
    }
}
