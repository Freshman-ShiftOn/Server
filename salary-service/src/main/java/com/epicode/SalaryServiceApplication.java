package com.epicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SalaryServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SalaryServiceApplication.class, args);
	}

}
