package com.wellbeing_waitlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class WellbeingWaitlistApplication {

	public static void main(String[] args) {
		System.out.println("Starting Application.");
		SpringApplication.run(WellbeingWaitlistApplication.class, args);
		System.out.println("Application started");
	}
}
