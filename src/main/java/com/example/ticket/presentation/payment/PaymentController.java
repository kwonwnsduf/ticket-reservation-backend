package com.example.ticket.presentation.payment;

import com.example.ticket.application.payment.PaymentService;
import com.example.ticket.presentation.payment.dto.PaymentRequest;
import com.example.ticket.presentation.payment.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private Long currentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
    @PostMapping("/events/{eventId}/seats/{seatId}/payments")
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid PaymentRequest req
    ){
        Long memberId = currentMemberId();
       Long reservationId= paymentService.pay(eventId,seatId,memberId,req.getAmount());
        return ResponseEntity.ok(new PaymentResponse(reservationId));
    }

}
