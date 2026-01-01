package com.example.ticket.presentation.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationCreateRequest {
    @NotNull(message = "memberId는 필수입니다.")
    private Long memberId;
}
