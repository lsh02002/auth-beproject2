package me.seho.authbeproject2.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seho.authbeproject2.repository.userDetails.CustomUserDetails;
import me.seho.authbeproject2.repository.users.User;
import me.seho.authbeproject2.repository.users.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Primary
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         User user = userRepository.findByEmail(email).orElseThrow(()->
                 new UsernameNotFoundException("(토큰에러) 해당 이메일을 찾을 수 없습니다."));

         return CustomUserDetails.builder()
                 .userId(user.getUserId())
                 .email(user.getEmail())
                 .password(user.getPassword())
                 .phoneNumber(user.getPhoneNumber())
                 .nickName(user.getNickName())
                 .authorities(user.getUserRoles()
                         .stream().map(u->u.getRoles())
                         .map(r->r.getName())
                         .collect(Collectors.toList()))
                 .build();
    }
}
