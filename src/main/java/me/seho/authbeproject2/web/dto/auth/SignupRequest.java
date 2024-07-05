package me.seho.authbeproject2.web.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String passwordConfirm;
    private String name;
    private String phoneNumber;
    private String address;
    private String gender;
    private String birthDate;
}
