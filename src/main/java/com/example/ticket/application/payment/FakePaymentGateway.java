package com.example.ticket.application.payment;

import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class FakePaymentGateway implements PaymentGateway{
    @Override
    public String charge(Long memberId, Long amount){
        if(amount==null||amount<=0){

            throw new ApiException(ErrorCode.PAYMENT_FAILED);
        }
        return "fake-"+ UUID.randomUUID();
    }
    @Override
    public void cancel(String txId){
        if(txId==null||txId.isBlank()){
            throw new ApiException(ErrorCode.PAYMENT_FAILED);
        }
    }
}
