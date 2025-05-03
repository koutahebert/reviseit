package com.example.reviseit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // Import EnableAsync

@SpringBootApplication
@EnableAsync // Enable asynchronous method execution
public class ReviseitApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReviseitApplication.class, args);
  }
}
