package com.example.ticket.domain.payment;

import org.springframework.stereotype.Service;

@Service
public class FakePaymentService implements PaymentService{
    @Override
    public void pay(Long memberId, int amount){
        if(amount<=0){
            throw new IllegalArgumentException("결제금액오류");
        }
    }
}
