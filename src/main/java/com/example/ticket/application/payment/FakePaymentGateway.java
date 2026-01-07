package com.example.ticket.application.payment;

import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class FakePaymentGateway implements PaymentGateway{
    @Override
    public void charge(Long memberId, Long amount){
        if(amount==null||amount<=0){

            throw new ApiException(ErrorCode.PAYMENT_FAILED);
        }
    }
}
