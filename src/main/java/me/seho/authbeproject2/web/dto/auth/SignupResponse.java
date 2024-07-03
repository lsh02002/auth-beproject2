package me.seho.authbeproject2.web.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private Integer userId;
    private String nickname;
}
