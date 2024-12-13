package com.example.manualservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ManualServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManualServiceApplication.class, args);
    }
}