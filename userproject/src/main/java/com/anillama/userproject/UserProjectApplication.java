package com.anillama.userproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.anillama.userproject",
                "com.anillama.amqp",
                "com.anillama.clients"
        }
)
public class UserProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserProjectApplication.class, args);
    }
}