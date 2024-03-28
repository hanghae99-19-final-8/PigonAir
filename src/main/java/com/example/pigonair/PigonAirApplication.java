package com.example.pigonair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.pigonair.domain.flight.service.FlightDataGenerator;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class PigonAirApplication {
	public static void main(String[] args) {
		// ConfigurableApplicationContext context =
		SpringApplication.run(PigonAirApplication.class, args);

		// FlightDataGenerator flightDataGenerator = context.getBean(FlightDataGenerator.class);
		//
		// // Generate random flight data for 100 flights
		// int numberOfFlights = 100;
		// flightDataGenerator.generateRandomFlightData(numberOfFlights);
		//
		// // Close the application context
		// context.close();
	}
}
