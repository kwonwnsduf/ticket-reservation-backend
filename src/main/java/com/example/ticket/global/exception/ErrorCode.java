package com.example.ticket.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {
    // ===== 공통 =====
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    CONCURRENT_REQUEST(HttpStatus.CONFLICT, "동시 요청으로 처리에 실패했습니다. 다시 시도해주세요."),

    // ===== Member =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),

    // ===== Event / Seat =====
    EVENT_ENDED(HttpStatus.CONFLICT, "종료된 이벤트입니다."),
    SOLD_OUT(HttpStatus.CONFLICT, "매진되었습니다."),
    ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예매된 좌석입니다."),
    INVALID_SEAT_STATUS(HttpStatus.BAD_REQUEST, "좌석 상태가 올바르지 않습니다."),
    // ===== Reservation (Day8 핵심) =====
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예매가 존재하지 않습니다."),
    INVALID_RESERVATION_STATUS(HttpStatus.CONFLICT, "예매 상태가 올바르지 않습니다."),
    RESERVATION_EXPIRED(HttpStatus.CONFLICT, "예매가 만료되었습니다."),
    ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 예매입니다."),
    CONCURRENT_SEAT_UPDATE(HttpStatus.CONFLICT, "동시 요청으로 좌석 상태가 변경되었습니다. 다시 시도해주세요."),

    // ====payment====
    DUPLICATE_PAYMENT(HttpStatus.CONFLICT, "이미 해당 예약에 대한 결제가 존재합니다"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST,"결제에 실패했습니다."),
    //===redis hold====
    SEAT_ALREADY_HELD(HttpStatus.CONFLICT,"이미 다른 사용자가 선택한 좌석입니다."),
    HOLD_EXPIRED(HttpStatus.NOT_FOUND,"좌석 선점 시간이 만료되었습니다. 다시 선택해주세요."),
    HOLD_NOT_OWNER(HttpStatus.FORBIDDEN,"해당 좌석을 선점한 사용자가 아닙니다.");

    private final HttpStatus status;
    private final String message;
    ErrorCode(HttpStatus status, String message){
        this.status=status;
        this.message=message;
    }

}
