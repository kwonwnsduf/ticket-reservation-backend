package com.example.ticket.application.reservation;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.reservation.ReservationStatus;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.presentation.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    // 인터페이스 추천 (없으면 Fake로)
    private static final int HOLD_MINUTES=5;
    // Day6: "예매 생성" = 좌석 HOLD + Reservation PENDING 생성
    public ReservationResponse create(Long eventId, Long seatId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        Seat seat = seatRepository.findByIdWithLock(eventId, seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 좌석이 이미 HOLD/SOLD 등 점유 상태면 막기
        if (seat.isOccupied()) { // 네 기존 메서드 재사용
            throw new ApiException(ErrorCode.ALREADY_RESERVED);
        }

        // Day5 로직: 좌석 선점(HOLD)
        seat.reserve();


        Reservation saved = reservationRepository.save(
                Reservation.createHold(member,seat,HOLD_MINUTES)
        );

        return new ReservationResponse(
                saved.getId(),
                member.getId(),
                seat.getId(),
                seat.getSeatNo(),
                saved.getReservedAt()
        );
    }

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

    // Day6: 결제 성공 확정


    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!reservation.isHold()) {
            // 이미 canceled면 기존 에러 유지
            if (reservation.isCanceled()) {
                throw new ApiException(ErrorCode.ALREADY_CANCELED);
            }
            throw new ApiException(ErrorCode.INVALID_REQUEST); // 또는 INVALID_RESERVATION_STATUS 추가 추천
        }
        Seat seat = seatRepository.findByIdWithLock(
                reservation.getSeat().getEvent().getId(),
                reservation.getSeat().getId()
        ).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        reservation.cancel();
        seat.makeAvailable(); // HOLD 해제
    }
}


