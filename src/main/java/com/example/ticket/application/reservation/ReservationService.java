package com.example.ticket.application.reservation;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.payment.PaymentService;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService; // 인터페이스 추천 (없으면 Fake로)

    // Day6: "예매 생성" = 좌석 HOLD + Reservation PENDING 생성
    public ReservationResponse create(Long eventId, Long seatId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        Seat seat = seatRepository.findByIdWithLock(eventId, seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 좌석이 이미 HOLD/SOLD 등 점유 상태면 막기
        if (seat.isReserved()) { // 네 기존 메서드 재사용
            throw new ApiException(ErrorCode.ALREADY_RESERVED);
        }

        // Day5 로직: 좌석 선점(HOLD)
        seat.hold();

        // Day6 로직: Reservation은 무조건 PENDING으로 생성
        Reservation saved = reservationRepository.save(
                Reservation.builder()
                        .member(member)
                        .seat(seat)
                        .build()
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
    public void confirm(Long reservationId, int price) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 좌석을 다시 락 잡고 상태 확인 (경합 방지)
        Seat seat = seatRepository.findByIdWithLock(
                reservation.getSeat().getEvent().getId(), // event가 Seat에 있으면
                reservation.getSeat().getId()
        ).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // Reservation 상태 검증 (PENDING만 가능)
        reservation.confirm(); // 내부에서 PENDING 검증하도록 만드는 게 정석

        // 좌석 상태 검증 (HOLD 상태여야 sold 가능)
        // seat.ensureHold(); 같은 검증 메서드 있으면 여기서 체크

        // 결제 (실패 시 예외 → 트랜잭션 롤백)
        paymentService.pay(reservation.getMember().getId(), price);

        // 확정 처리
        seat.sold();
    }

    // Day6: 취소 (결제 전 취소)
    public void cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        Seat seat = seatRepository.findByIdWithLock(
                reservation.getSeat().getEvent().getId(),
                reservation.getSeat().getId()
        ).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        reservation.cancel();
        seat.release(); // HOLD 해제
    }
}


