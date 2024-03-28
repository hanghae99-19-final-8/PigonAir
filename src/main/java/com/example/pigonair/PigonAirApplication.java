package com.example.pigonair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PigonAirApplication {

    public static void main(String[] args) {
        SpringApplication.run(PigonAirApplication.class, args);
    }

}
