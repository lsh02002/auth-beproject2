package me.seho.authbeproject2.repository.users.userRoles;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roles_id")
    private Integer rolesId;

    @Column(name = "name",nullable = false)
    private String name;
}
