package com.example.ticket.domain.payment;

public interface PaymentService {
    void pay(Long memberId,int amount);
}
