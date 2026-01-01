package com.example.ticket.application.reservation;

import com.example.ticket.application.event.EventService;
import com.example.ticket.application.member.MemberService;
import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.presentation.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public ReservationResponse reserve(Long eventId, Long seatId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        Seat seat = seatRepository.findByIdWithLock(eventId, seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (seat.isReserved()) {
            throw new ApiException(ErrorCode.ALREADY_RESERVED);
        }
        seat.reserve();
        Reservation saved = reservationRepository.save(
                Reservation.builder().member(member).seat(seat).build()
        );
        return new ReservationResponse(
                saved.getId(),
                member.getId(),
                seat.getId(),
                seat.getSeatNo(),
                saved.getReservedAt());}
    @Transactional(readOnly = true)
    public ReservationResponse get(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        return new ReservationResponse(
                r.getId(),
                r.getMember().getId(),
                r.getSeat().getId(),
                r.getSeat().getSeatNo(),
                r.getReservedAt()
        );
    }

    // 같은 event + seat은 1번만 예약되게
        @Transactional
        public void cancel(Long reservationId) {
            Reservation r = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

            r.getSeat().cancelReserve();
            reservationRepository.delete(r);

    }
}

