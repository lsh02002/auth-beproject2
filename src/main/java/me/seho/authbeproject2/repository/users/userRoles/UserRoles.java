package me.seho.authbeproject2.repository.users.userRoles;

import jakarta.persistence.*;
import lombok.*;
import me.seho.authbeproject2.repository.users.User;

@Table(name = "user_roles")
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_roles_id")
    private Integer userRolesId;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Roles roles;
}
