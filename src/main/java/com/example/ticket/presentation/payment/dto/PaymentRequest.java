package com.example.ticket.presentation.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @NotNull
    private Long memberId;
    @NotNull
    private Long amount;
}
