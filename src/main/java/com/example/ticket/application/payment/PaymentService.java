package com.example.ticket.application.payment;

import com.example.ticket.domain.hold.HoldStore;
import com.example.ticket.domain.payment.Payment;
import com.example.ticket.domain.payment.PaymentRepository;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final PaymentGateway paymentGateway;
    private final HoldStore holdStore;

    /**
     * Day13 고급: 결제 = (Redis HOLD 검증) -> PG 결제 -> (DB 확정 저장)
     */
    public Long pay(Long eventId, Long seatId, Long memberId, Long amount) {

        Long holder = holdStore.getHolder(eventId, seatId);
        if (holder == null) throw new ApiException(ErrorCode.HOLD_EXPIRED);
        if (!holder.equals(memberId)) throw new ApiException(ErrorCode.HOLD_NOT_OWNER);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        Seat seat = seatRepository.findByIdAndEventId(eventId, seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        if (seat.isOccupied()) throw new ApiException(ErrorCode.ALREADY_RESERVED);

        try {
            // 1. 결제
            paymentGateway.charge(memberId, amount);

            // 2. 좌석 확정 (여기서 동시성 터질 수 있음)
            seat.occupy();

            // 3. 예약 확정 저장
            Reservation reservation = reservationRepository.save(
                    Reservation.createConfirmed(member, seat)
            );

            // 4. 결제 기록 저장
            Payment payment = paymentRepository.save(Payment.request(reservation, amount));
            payment.succeed();

            return reservation.getId();

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.CONCURRENT_SEAT_UPDATE);

        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.DUPLICATE_PAYMENT);

        } catch (Exception e) {
            throw new ApiException(ErrorCode.PAYMENT_FAILED);

        } finally {
            // 5. HOLD 정리 (성공/실패/예외 상관없이)
            holdStore.release(eventId, seatId);
        }
    }
}

