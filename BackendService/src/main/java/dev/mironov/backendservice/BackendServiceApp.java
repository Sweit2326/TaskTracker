package dev.mironov.backendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BackendServiceApp {
    static void main(String[] args) {
        SpringApplication.run(BackendServiceApp.class, args);
    }
}
