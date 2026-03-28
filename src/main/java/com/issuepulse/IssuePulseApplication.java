package com.issuepulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IssuePulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(IssuePulseApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  IssuePulse Backend is running!");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("=========================================");
    }
}
