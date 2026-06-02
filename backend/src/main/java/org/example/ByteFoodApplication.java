package org.example;

import org.example.config.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppSecurityProperties.class)
public class ByteFoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByteFoodApplication.class, args);
    }
}
