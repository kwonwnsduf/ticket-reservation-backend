package com.example.ticket.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
        ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예매된 좌석입니다."), DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다.");
    private final HttpStatus status;
    private final String message;
    ErrorCode(HttpStatus status, String message){
        this.status=status;
        this.message=message;
    }
    public HttpStatus getStatus(){return status;}

    public String getMessage() {
        return message;
    }
}
