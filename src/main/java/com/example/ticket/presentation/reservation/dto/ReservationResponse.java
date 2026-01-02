package com.example.ticket.presentation.reservation.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private Long reservationId;
    private Long memberId;
    private Long seatId;
    private String seatNo;
    private LocalDateTime reservedAt;
}