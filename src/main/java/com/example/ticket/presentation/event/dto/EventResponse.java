package com.example.ticket.presentation.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventResponse {
    private Long eventId;
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}