package com.example.ticket.application.payment;

import com.example.ticket.domain.payment.Payment;
import com.example.ticket.domain.payment.PaymentRepository;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final PaymentGateway paymentGateway;
    public void pay(Long reservationId, Long memberId, Long amount){
        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ApiException(ErrorCode.NOT_FOUND));
        if(!reservation.isHold()){
            throw new ApiException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        Long eventId=reservation.getSeat().getEvent().getId();
        Long seatId=reservation.getSeat().getId();
        Seat seat=seatRepository.findByIdWithLock(eventId,seatId).orElseThrow(()->new ApiException(ErrorCode.NOT_FOUND));
        Payment payment;
        try {
            payment = paymentRepository.save(Payment.request(reservation, amount));
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.DUPLICATE_PAYMENT);
        }
        try {
            paymentGateway.charge(memberId, amount);

            payment.succeed();
            seat.occupy();                 // HELD -> OCCUPIED
            reservation.confirmAfterPayment();

        } catch (Exception ex) {
            payment.fail(ex.getMessage());
            reservation.cancelByPaymentFailure();
            seat.release();                // HELD -> AVAILABLE
        }

    }

}
