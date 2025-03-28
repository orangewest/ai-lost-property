package io.orangewest.ailostproperty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AiLostPropertyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLostPropertyApplication.class, args);
    }

}
