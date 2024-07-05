package me.seho.authbeproject2.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByName(String name);
    Boolean existsByPhoneNumber(String phoneNumber);
}
