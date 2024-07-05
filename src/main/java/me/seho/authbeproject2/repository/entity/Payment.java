package me.seho.authbeproject2.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import me.seho.authbeproject2.repository.users.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<PaymentItem> paymentItems = new ArrayList<>();
}
