package me.seho.authbeproject2.repository.users.refreshToken;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash(value = "refresh_token")
public class RefreshToken {
    @Id
    private String authId;
    @Indexed
    private String refreshToken;
    private String email;

    public RefreshToken update(String refreshToken){
        this.refreshToken = refreshToken;
        return this;
    }
}
