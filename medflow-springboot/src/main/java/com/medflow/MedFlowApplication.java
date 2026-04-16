package com.medflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MedFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedFlowApplication.class, args);
    }
}
