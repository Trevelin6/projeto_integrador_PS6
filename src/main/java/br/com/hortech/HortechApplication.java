package br.com.hortech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HortechApplication {
    public static void main(String[] args) {
        SpringApplication.run(HortechApplication.class, args);
    }
}
