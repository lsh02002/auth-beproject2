package me.seho.authbeproject2.repository.users;

import jakarta.persistence.*;
import lombok.*;
import me.seho.authbeproject2.repository.userRoles.UserRoles;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "nick_name", unique = true, nullable = false)
    private String nickName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

//    @Column(name = "join_date", nullable = false)
//    private LocalDateTime joinDate;

    @Column(name = "deletion_date")
    private LocalDateTime deletionDate;

    @Column(name = "lock_date")
    private LocalDateTime lockDate;

    @OneToMany(mappedBy = "user")
    private Collection<UserRoles> userRoles;
}
