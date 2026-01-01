package com.example.ticket.presentation.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateEventRequest {
    @NotBlank
    public String title;
    @NotNull
    public LocalDateTime startsAt;
    @NotNull public LocalDateTime endsAt;
}