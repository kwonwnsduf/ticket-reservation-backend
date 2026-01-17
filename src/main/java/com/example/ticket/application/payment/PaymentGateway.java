package com.example.ticket.application.payment;

public interface PaymentGateway {
    String charge(Long memberId, Long amount);
    void cancel(String txId);
}
