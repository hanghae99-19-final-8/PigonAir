package com.example.pigonair.domain.payment.dto;

import java.time.LocalDateTime;

import com.example.pigonair.domain.reservation.entity.Reservation;

public class PaymentResponseDto {
	public record TicketResponseDto(
		Long reservationId,
		LocalDateTime departureTime,
		String origin,
		String destination,
		String name,
		boolean isPayment
	) {
		public TicketResponseDto(Reservation reservation) {
			this(
				reservation.getId(),
				reservation.getFlight().getDepartureTime(),
				reservation.getFlight().getOrigin(),
				reservation.getFlight().getDestination(),
				reservation.getMember().getName(),
				reservation.isPayment()
			);
		}
	}

}
