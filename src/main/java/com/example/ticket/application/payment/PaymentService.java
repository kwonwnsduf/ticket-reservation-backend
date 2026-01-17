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
        Payment payment = paymentRepository.save(Payment.pending(amount));
        String txId = null;

        try {
            // 1. 결제
            txId = paymentGateway.charge(memberId, amount);
            payment.markCharged(txId);
            return confirmInDb(eventId, seatId, member, payment);
        }catch (ObjectOptimisticLockingFailureException e) {
            if (txId != null) safeCancel(txId, payment, "OPTIMISTIC_LOCK");
            payment.fail(ErrorCode.CONCURRENT_SEAT_UPDATE.name());
            throw new ApiException(ErrorCode.CONCURRENT_SEAT_UPDATE);}
        catch (DataIntegrityViolationException e) {
            if (txId != null) safeCancel(txId, payment, "DATA_INTEGRITY");
            payment.fail(ErrorCode.DUPLICATE_PAYMENT.name());
            throw new ApiException(ErrorCode.DUPLICATE_PAYMENT);}
        catch (ApiException e) {
            if (txId != null) safeCancel(txId, payment, e.getErrorCode().name());
            payment.fail(e.getErrorCode().name());
            throw e;}
        catch (Exception e) {
            if (txId != null) safeCancel(txId, payment, e.getClass().getSimpleName());
            payment.fail(ErrorCode.PAYMENT_FAILED.name());
            throw new ApiException(ErrorCode.PAYMENT_FAILED);

        }finally {
            holdStore.release(eventId, seatId);
        }


    }

    @Transactional
    protected Long confirmInDb(Long eventId, Long seatId, Member member, Payment payment) {

        Seat seat = seatRepository.findByIdAndEventId(eventId, seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        if (seat.isOccupied()) throw new ApiException(ErrorCode.ALREADY_RESERVED);

        seat.occupy(); // 낙관락 충돌 가능

        Reservation reservation = reservationRepository.save(
                Reservation.createConfirmed(member, seat)
        );

        payment.succeed(reservation);

        return reservation.getId();
    }
    private void safeCancel(String txId, Payment payment, String reason) {
        try {
            paymentGateway.cancel(txId);
            payment.cancel();
        } catch (Exception cancelEx) {
            payment.cancelFailed("CANCEL_FAIL:" + reason + ":" + cancelEx.getClass().getSimpleName());
        }
    }
}

