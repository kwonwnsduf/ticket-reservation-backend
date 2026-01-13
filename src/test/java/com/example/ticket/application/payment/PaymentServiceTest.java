package com.example.ticket.application.payment;

import com.example.ticket.domain.hold.HoldStore;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.payment.Payment;
import com.example.ticket.domain.payment.PaymentRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {
    PaymentRepository paymentRepository = mock(PaymentRepository.class);
    ReservationRepository reservationRepository = mock(ReservationRepository.class);
    SeatRepository seatRepository = mock(SeatRepository.class);
    MemberRepository memberRepository = mock(MemberRepository.class);
    PaymentGateway paymentGateway = mock(PaymentGateway.class);
    HoldStore holdStore = mock(HoldStore.class);

    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository, reservationRepository, seatRepository, memberRepository, paymentGateway, holdStore
        );
    }
    @Test
    void pay_성공하면_reservationId_반환하고_finally로_release_호출(){
        Long eventId = 1L, seatId = 2L, memberId = 3L, amount = 1000L;
        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
        Member member = Member.builder().email("a@a.com").password("pw").role(null).build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        Seat seat = mock(Seat.class);
        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(seat.isOccupied()).thenReturn(false);
        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(777L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        Payment payment = mock(Payment.class);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Long reservationId = paymentService.pay(eventId, seatId, memberId, amount);
        assertEquals(777L, reservationId);
        verify(paymentGateway).charge(memberId, amount);
        verify(seat).occupy();
        verify(payment).succeed();
        verify(holdStore).release(eventId, seatId);

    }
    @Test
    void pay_결제실패해도_finally로_release_는_무조건_호출(){
        Long eventId = 1L, seatId = 2L, memberId = 3L, amount = 1000L;

        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);

        Member member = Member.builder().email("a@a.com").password("pw").role(null).build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Seat seat = mock(Seat.class);
        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(seat.isOccupied()).thenReturn(false);
        doThrow(new RuntimeException("pg fail")).when(paymentGateway).charge(memberId, amount);
        ApiException ex = assertThrows(ApiException.class,
                () -> paymentService.pay(eventId, seatId, memberId, amount));
        assertEquals(ErrorCode.PAYMENT_FAILED, ex.getErrorCode());
        verify(holdStore).release(eventId, seatId);
    }


}
