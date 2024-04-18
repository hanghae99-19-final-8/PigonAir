package com.example.pigonair.domain.payment.service;

import static com.example.pigonair.global.config.common.exception.ErrorCode.*;

import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pigonair.domain.member.entity.Member;
import com.example.pigonair.domain.payment.dto.PaymentRequestDto.PostPayRequestDto;
import com.example.pigonair.domain.payment.dto.PaymentResponseDto.PayResponseDto;
import com.example.pigonair.domain.payment.entity.Payment;
import com.example.pigonair.domain.payment.repository.PaymentRepository;
import com.example.pigonair.domain.reservation.entity.Reservation;
import com.example.pigonair.domain.reservation.repository.ReservationRepository;
import com.example.pigonair.domain.seat.entity.Seat;
import com.example.pigonair.global.config.common.exception.CustomException;
import com.example.pigonair.global.config.common.ulid.Ulid;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ReservationRepository reservationRepository;
	private final PaymentRepository paymentRepository;
	@Value("${iamport.impKey}")
	private String impKey;
	private final RabbitTemplate rabbitTemplate;

	@Override
	@Transactional
	public PayResponseDto payProcess(Long ReservationId, Member member) {
		Reservation reservation = reservationRepository.findById(ReservationId).orElseThrow(() ->
			new CustomException(RESERVATION_NOT_FOUND));
		String payUlid = getUlid();

		return new PayResponseDto(reservation, member, payUlid, impKey);
	}

	@Override
	@Transactional
	public void postPayProcess(PostPayRequestDto requestDto) {
		Optional<Reservation> optionalReservation = reservationRepository.findById(requestDto.id());
		// 예약을 찾지 못할 경우 null 반환
		if (optionalReservation.isEmpty()) {
			throw new CustomException(RESERVATION_NOT_FOUND);
		}
		Reservation reservation = optionalReservation.get();

		// 이미 결제된 예약인지 확인
		if (reservation.isPayment()) {
			throw new CustomException(ALREADY_PAID_RESERVATION);
		}

		//결제 금액과 좌석 가격이 같은지 확인
		if (!Objects.equals(reservation.getSeat().getPrice(), requestDto.paidAmount())) {
			// 결제 금액 불일치 에러
			throw new CustomException(PAYMENT_AMOUNT_MISMATCH);
		}



		//결제 후 결제 여부 변경
		reservation.updateIsPayment();

		sendPaymentCompletedEvent(requestDto);

	}

	private void sendPaymentCompletedEvent(PostPayRequestDto requestDto) {
		rabbitTemplate.convertAndSend("payment.exchange", "payment.key", requestDto);
	}

	@Override
	@Transactional
	public void savePayInfo(PostPayRequestDto postPayRequestDto) { // 추후 데이터 삽입 시 외래키만 삽입하는 것으로 변경하는 것 고려
		Reservation reservation = reservationRepository.findById(postPayRequestDto.id()).orElseThrow(() ->
			new CustomException(RESERVATION_NOT_FOUND));
		Payment payment = new Payment(reservation, postPayRequestDto.serialNumber());
		paymentRepository.save(payment);
	}

	@Transactional
	public void updateSeatUnAvailable(Reservation reservation) {
		Seat seat = reservation.getSeat();
		if (!seat.isAvailable()) {
			throw new CustomException(UNAVAILABLE_SEAT);
		}
		seat.updateIsAvailable();
	}

	private String getUlid() {
		Ulid ulid = new Ulid();
		return ulid.nextULID();
	}

}
