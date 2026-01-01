package com.example.ticket.presentation.seat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSeatRequest {
    @NotBlank(message="좌석 번호는 필숩입니다.")
    public String seatNo;
}