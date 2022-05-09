package com.anillama.sessionmanagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@Slf4j
@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {
                "com.anillama.sessionmanagement",
                "com.anillama.amqp",
                "com.anillama.clients"
        }
)
public class ApplicationUserSessionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationUserSessionApplication.class, args);
    }
}
