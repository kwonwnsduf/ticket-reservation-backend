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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock SeatRepository seatRepository;
    @Mock MemberRepository memberRepository;
    @Mock PaymentGateway paymentGateway;
    @Mock HoldStore holdStore;

    @InjectMocks PaymentService paymentService;

    Long eventId = 1L;
    Long seatId = 2L;
    Long memberId = 3L;
    Long amount = 10000L;

    Member member;
    Seat seat;

    @BeforeEach
    void setUp() {
        member = mock(Member.class);
        seat = mock(Seat.class);
    }

    /**
     * (1) 정상 성공: charge 성공 -> DB 확정 성공 -> cancel 호출 안 됨 + hold release 됨
     */
    @Test
    void pay_success() {
        // given
        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        when(paymentGateway.charge(memberId, amount)).thenReturn("tx-123");

        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(seat.isOccupied()).thenReturn(false);

        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(999L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // when
        Long reservationId = paymentService.pay(eventId, seatId, memberId, amount);

        // then
        assertEquals(999L, reservationId);

        verify(paymentGateway, times(1)).charge(memberId, amount);
        verify(paymentGateway, never()).cancel(anyString());
        verify(holdStore, times(1)).release(eventId, seatId);
        verify(seat, times(1)).occupy();
    }

    /**
     * (2) "결제 성공 -> DB 확정 실패(낙관적 락 충돌)" 시나리오:
     * charge는 성공했으니 cancel(보상)이 반드시 호출되어야 함
     */
    @Test
    void pay_compensate_when_db_confirm_fails_by_optimistic_lock() {
        // given
        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        when(paymentGateway.charge(memberId, amount)).thenReturn("tx-optimistic");

        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(seat.isOccupied()).thenReturn(false);

        // DB 확정 중(예약 저장/플러시)에서 충돌 발생했다고 가정
        when(reservationRepository.save(any(Reservation.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Reservation.class, 1L));

        // when
        ApiException ex = assertThrows(ApiException.class,
                () -> paymentService.pay(eventId, seatId, memberId, amount));

        // then
        assertEquals(ErrorCode.CONCURRENT_SEAT_UPDATE, ex.getErrorCode());
        verify(paymentGateway, times(1)).charge(memberId, amount);
        verify(paymentGateway, times(1)).cancel("tx-optimistic"); // ✅ 보상 호출
        verify(holdStore, times(1)).release(eventId, seatId);
    }

    /**
     * (3) "결제 성공 -> DB에서 중복/제약 위반(DataIntegrityViolation)" 시나리오:
     * 이 경우도 결제는 성공했을 수 있으니 cancel(보상)을 호출해야 함
     */
    @Test
    void pay_compensate_when_db_confirm_fails_by_data_integrity() {
        // given
        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        when(paymentGateway.charge(memberId, amount)).thenReturn("tx-dup");

        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(seat.isOccupied()).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        // when
        ApiException ex = assertThrows(ApiException.class,
                () -> paymentService.pay(eventId, seatId, memberId, amount));

        // then
        assertEquals(ErrorCode.DUPLICATE_PAYMENT, ex.getErrorCode());
        verify(paymentGateway, times(1)).cancel("tx-dup"); // ✅ 보상 호출
        verify(holdStore, times(1)).release(eventId, seatId);
    }
}


//public class PaymentServiceTest {
//    PaymentRepository paymentRepository = mock(PaymentRepository.class);
//    ReservationRepository reservationRepository = mock(ReservationRepository.class);
//    SeatRepository seatRepository = mock(SeatRepository.class);
//    MemberRepository memberRepository = mock(MemberRepository.class);
//    PaymentGateway paymentGateway = mock(PaymentGateway.class);
//    HoldStore holdStore = mock(HoldStore.class);
//
//    PaymentService paymentService;
//
//    @BeforeEach
//    void setUp() {
//        paymentService = new PaymentService(
//                paymentRepository, reservationRepository, seatRepository, memberRepository, paymentGateway, holdStore
//        );
//    }
//    @Test
//    void pay_성공하면_reservationId_반환하고_finally로_release_호출(){
//        Long eventId = 1L, seatId = 2L, memberId = 3L, amount = 1000L;
//        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
//        Member member = Member.builder().email("a@a.com").password("pw").role(null).build();
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//        Seat seat = mock(Seat.class);
//        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
//        when(seat.isOccupied()).thenReturn(false);
//        Reservation reservation = mock(Reservation.class);
//        when(reservation.getId()).thenReturn(777L);
//        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
//        Payment payment = mock(Payment.class);
//        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
//        Long reservationId = paymentService.pay(eventId, seatId, memberId, amount);
//        assertEquals(777L, reservationId);
//        verify(paymentGateway).charge(memberId, amount);
//        verify(seat).occupy();
//        verify(payment).succeed();
//        verify(holdStore).release(eventId, seatId);
//
//    }
//    @Test
//    void pay_결제실패해도_finally로_release_는_무조건_호출(){
//        Long eventId = 1L, seatId = 2L, memberId = 3L, amount = 1000L;
//
//        when(holdStore.getHolder(eventId, seatId)).thenReturn(memberId);
//
//        Member member = Member.builder().email("a@a.com").password("pw").role(null).build();
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//
//        Seat seat = mock(Seat.class);
//        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
//        when(seat.isOccupied()).thenReturn(false);
//        doThrow(new RuntimeException("pg fail")).when(paymentGateway).charge(memberId, amount);
//        ApiException ex = assertThrows(ApiException.class,
//                () -> paymentService.pay(eventId, seatId, memberId, amount));
//        assertEquals(ErrorCode.PAYMENT_FAILED, ex.getErrorCode());
//        verify(holdStore).release(eventId, seatId);
//    }
//
//
//}
