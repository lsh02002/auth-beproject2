package me.seho.authbeproject2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AuthBeProject2Application {

    public static void main(String[] args) {
        SpringApplication.run(AuthBeProject2Application.class, args);
    }

}
