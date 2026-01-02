package com.example.ticket.presentation.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationHoldCreateRequest {
    @NotNull(message = "seatId는 필수입니다.")
    private Long seatId;
}
