package com.example.ticket.presentation.payment;

import com.example.ticket.application.payment.PaymentService;
import com.example.ticket.presentation.payment.dto.PaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/reservations/{reservationId}/payments")
    public ResponseEntity<Void> pay(
            @PathVariable Long reservationId,
            @RequestBody @Valid PaymentRequest req
    ){
        paymentService.pay(reservationId,req.getMemberId(),req.getAmount());
        return ResponseEntity.ok().build();
    }

}
