package com.example.pigonair.domain.payment.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.pigonair.domain.email.EmailService;
import com.example.pigonair.domain.payment.dto.EmailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

	private final EmailService emailService;

	@RabbitListener(queues = "payment.queue")
	public void handlePaymentCompletedEvent(EmailDto.EmailSendDto emailDto) {
		try {
			System.out.println("Payment completed for payment ID : " + emailDto.paymentId() + "\n");

			String recipientEmail = emailDto.email();
			String subject = "티켓 결제 완료";
			String body = "티켓 번호: " + emailDto.paymentId();
			emailService.sendEmail(recipientEmail, subject, body);
		} catch (Exception ex) {
			log.error("Payment 처리 중 오류 발생: {}", ex.getMessage(), ex);
		}

	}
}
