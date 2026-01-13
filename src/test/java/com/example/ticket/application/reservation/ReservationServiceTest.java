package com.example.ticket.application.reservation;

import com.example.ticket.domain.hold.HoldStore;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.member.Role;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.presentation.reservation.dto.HoldResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {
    ReservationRepository reservationRepository = mock(ReservationRepository.class);
    SeatRepository seatRepository = mock(SeatRepository.class);
    MemberRepository memberRepository = mock(MemberRepository.class);
    HoldStore holdStore = mock(HoldStore.class);
    ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, seatRepository, memberRepository, holdStore);
    }

    @Test
    void create_tryHold가_false면_SEAT_ALREADY_HELD_예외() {
        Long eventId = 1L, seatId = 2L, memberId = 3L;
        Member member = Member.builder().email("a@a.com").password("pw").role(Role.USER).build();
        Seat seat = mock(Seat.class);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(holdStore.tryHold(eq(eventId), eq(seatId), anyLong(), any(Duration.class)))
                .thenReturn(false);
        ApiException ex = assertThrows(ApiException.class,
                () -> reservationService.create(eventId, seatId, memberId));
        assertEquals(ErrorCode.SEAT_ALREADY_HELD, ex.getErrorCode());


    }

    @Test
    void create_tryHold가_true면_HoldResponse_반환() {
        // given
        Long eventId = 1L, seatId = 2L, memberId = 3L;

        Member member = Member.builder()
                .email("a@a.com")
                .password("pw")
                .role(null)
                .build();

        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(seatId);
        when(seat.getSeatNo()).thenReturn("A1");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(seatRepository.findByIdAndEventId(eventId, seatId)).thenReturn(Optional.of(seat));
        when(holdStore.tryHold(eq(eventId), eq(seatId), anyLong(), any(Duration.class)))
                .thenReturn(true);
        HoldResponse res = reservationService.create(eventId, seatId, memberId);
        assertEquals(seatId, res.seatId());
        assertEquals("A1", res.seatNo());
        assertEquals(5L, res.ttlMinutes());
        verify(holdStore).tryHold(eq(eventId), eq(seatId), anyLong(), any(Duration.class));


    }
}