package com.example.pigonair.domain.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pigonair.domain.member.entity.Member;
import com.example.pigonair.domain.reservation.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByMemberAndIsPayment(Member member, Boolean isPayment);

	List<Reservation> findAllByMemberId(Long memberId);

	List<Reservation> findByIsPaymentFalseAndReservationDateBefore(LocalDateTime cutoffDate);

}
