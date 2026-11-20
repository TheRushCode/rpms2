package com.kiranacademy.rpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.kiranacademy.rpms.entity")
@EnableJpaRepositories(basePackages = "com.kiranacademy.rpms.repository")
public class RpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpmsApplication.class, args);
    }
}
