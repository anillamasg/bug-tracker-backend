package com.anillama.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (
        scanBasePackages = {
                "com.anillama.authentication",
                "com.anillama.amqp",
                "com.anillama.clients"
        }
)
public class ApplicationUserAuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationUserAuthenticationApplication.class, args);
    }
}
