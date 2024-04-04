package com.example.pigonair.Flight;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pigonair.domain.flight.repository.FlightRepository;
import com.example.pigonair.domain.flight.service.FlightDataGenerator;

@SpringBootTest
public class FlightDataGeneratorTest {

	@Autowired
	private FlightDataGenerator flightDataGenerator;
	@Autowired
	private FlightRepository flightRepository;

	@Test
	public void generateRandomFlights() {
		int numberOfFlights = 10;
		int flightSize = flightRepository.findAll().size();
		flightDataGenerator.generateRandomFlightData(numberOfFlights);
		int newFlightSize = flightRepository.findAll().size();
		assertThat(newFlightSize-flightSize).isEqualTo(numberOfFlights);
	}
}