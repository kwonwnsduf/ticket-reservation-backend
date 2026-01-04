package com.example.ticket.presentation.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {
    private Long seatId;
    private String seatNo;
    private boolean occupied;
}

