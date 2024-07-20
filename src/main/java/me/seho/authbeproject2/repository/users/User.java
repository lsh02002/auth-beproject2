package me.seho.authbeproject2.repository.users;

import jakarta.persistence.*;
import lombok.*;
import me.seho.authbeproject2.repository.entity.Cart;
import me.seho.authbeproject2.repository.entity.Payment;
import me.seho.authbeproject2.repository.entity.Sale;
import me.seho.authbeproject2.repository.users.userRoles.UserRoles;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "status")
    @ColumnDefault("'정상'")
    private String status;

    @Column(name = "deleted_at")
    private LocalDateTime deleteAt;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "user")
    private Collection<UserRoles> userRoles;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Sale> sales = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Cart cart;
}
