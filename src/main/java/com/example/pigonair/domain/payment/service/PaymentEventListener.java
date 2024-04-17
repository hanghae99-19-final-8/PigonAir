package com.example.pigonair.domain.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

    @RabbitListener(queues = "payment.queue")
    public void handlePaymentCompletedEvent(String paymentId) {
        // 결제 완료 이벤트를 로그에 기록
        System.out.printf("Payment completed for payment ID: {}", paymentId+"\n");
        logger.info("Payment completed for payment ID: {}", paymentId);
    }
}
