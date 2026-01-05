package com.example.ticket.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r where r.status = 'HOLD' and r.expiredAt < :now")
    List<Reservation> findExpired(@Param("now") LocalDateTime now);
}
