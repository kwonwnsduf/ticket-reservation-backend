package com.example.ticket.domain.payment;

public enum PaymentStatus {
    PENDING,        // 결제 기록만 생성됨
    CHARGED,        // PG 승인 성공 (txId 확보)
    SUCCESS,        // DB까지 최종 확정(예약/좌석 확정 포함)
    FAILED,         // 결제 실패(승인 실패 or 내부 처리 실패)
    CANCELED,       // 내부 실패로 보상 취소 성공
    CANCEL_FAILED // 취소 시도했는데 실패(운영 대응 필요)
}
