package com.example.ticket.application.reservation;

import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationExpireScheduler {
    private final ReservationService reservationService;
    @Transactional
    @Scheduled(fixedDelay=60000)
    public void expireReservations(){
       reservationService.expireHolds();
    }
}
