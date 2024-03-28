package com.example.pigonair.domain.flight.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pigonair.domain.flight.entity.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

	Page<Flight> findByDepartureTimeBetweenAndOriginAndDestination(
		LocalDateTime startDate, LocalDateTime endDate, String departure, String destination, Pageable pageable);
}
