package com.example.ticket.application.payment;

public interface PaymentGateway {
    void charge(Long memberId, Long amount);
}
