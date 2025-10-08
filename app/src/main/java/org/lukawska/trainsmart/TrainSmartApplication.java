package org.lukawska.trainsmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class TrainSmartApplication {
	public static void main(String[] args) {
		SpringApplication.run(TrainSmartApplication.class, args);
	}
}
