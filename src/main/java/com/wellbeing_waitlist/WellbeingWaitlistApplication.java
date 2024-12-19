package com.wellbeing_waitlist;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class WellbeingWaitlistApplication {

	public static void main(String[] args) {
		System.out.println("Starting Application.");

		// Program starts to run from here
		SpringApplication.run(WellbeingWaitlistApplication.class, args);

		// Creating a thread to run the files for decreasing the time taking during the flow of the program
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		// Thread for stablising the connection between the database
		executor.execute(() -> {
			System.out.println("Testing database connection...");
			try {
				DatabaseConnection.getConnection();
				System.out.println("Database connection stabilized.");
			} catch (SQLException e) {
				System.err.println("Failed to stabilize database connection: " + e.getMessage());
			}
		});
		
		// Thread for loading the python model before needed
		executor.execute(() -> {
			System.out.println("Preloading Python model...");
			try {
				ProcessBuilder pb = new ProcessBuilder("python", System.getenv("PYTHON_SCRIPT_PATH"), "test");
				pb.redirectErrorStream(true);
				Process process = pb.start();
				int exitCode = process.waitFor();
				
				if (exitCode == 0) {
					System.out.println("Python model loaded successfully.");
				} else {
					System.err.println("Python model preload failed with exit code: " + exitCode);
				}
			} catch (IOException | InterruptedException e) {
				System.err.println("Failed to preload Python model: " + e.getMessage());
			}
		});

		// These all threads are made to load the model and make connection so the program dont take long time
		executor.shutdown();
		System.out.println("Application started");
	}
}
