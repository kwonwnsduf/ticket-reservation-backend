package com.example.ticket.application.reservation;
import com.example.ticket.domain.hold.HoldStore;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.reservation.ReservationStatus;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.presentation.reservation.dto.HoldResponse;
import com.example.ticket.presentation.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final HoldStore holdStore;
    private static final Duration HOLD_TTL=Duration.ofMinutes(5);
    // 인터페이스 추천 (없으면 Fake로)


    public HoldResponse create(Long eventId, Long seatId, Long memberId) {

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

            Seat seat = seatRepository.findByIdAndEventId(eventId, seatId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));


            // Day5 로직: 좌석 선점(HOLD)
           boolean ok=holdStore.tryHold(eventId,seatId,member.getId(),HOLD_TTL);
           if(!ok) throw new ApiException(ErrorCode.SEAT_ALREADY_HELD);

           return new HoldResponse(member.getId(),seat.getId(),seat.getSeatNo(),HOLD_TTL.toMinutes());
        }

        @Transactional(readOnly = true)
        public ReservationResponse get (Long reservationId){
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
//   public ReservationResponse confirm(Long eventId, Long seatId, Long memberId) {
//       try{ Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
//
//        Seat seat = seatRepository.findByIdAndEventId(eventId, seatId)
//                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
//
//        Long holder = holdStore.getHolder(eventId, seatId);
//        if (holder == null) throw new ApiException(ErrorCode.HOLD_EXPIRED);
//        if (!holder.equals(memberId)) throw new ApiException(ErrorCode.HOLD_NOT_OWNER);
//        seat.occupy();
//
//        Reservation saved = reservationRepository.save(
//                Reservation.createConfirmed(member, seat)
//        );
//
//        holdStore.release(eventId, seatId);
//
//        return new ReservationResponse(
//                saved.getId(),
//                member.getId(),
//                seat.getId(),
//                seat.getSeatNo(),
//                saved.getReservedAt()
//        );}catch (ObjectOptimisticLockingFailureException e){
//           holdStore.release(eventId, seatId);
//           throw new ApiException(ErrorCode.CONCURRENT_SEAT_UPDATE);
//       }
//    }
    public void cancelHold(Long eventId, Long seatId, Long memberId) {
        Long holder = holdStore.getHolder(eventId, seatId);
        if (holder == null) return; // 이미 만료/없음: 멱등 처리
        if (!holder.equals(memberId)) throw new ApiException(ErrorCode.HOLD_NOT_OWNER);
        holdStore.release(eventId, seatId);
    }

    /** (선택) 확정된 예약 취소를 DB에 남기고 싶으면 유지 */
    public void cancelConfirmed(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        reservation.cancelConfirmed();
        reservation.getSeat().release();
    }



}


