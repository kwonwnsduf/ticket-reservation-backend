package com.example.ticket.presentation.reservation.dto;



public record HoldResponse(Long seatId,
                           String seatNo,
                           long ttlMinutes) {
}
