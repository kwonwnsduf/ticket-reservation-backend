package com.example.ticket.domain.reservation;

import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.seat.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "seat_id"}))
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Column(nullable = false)
    private Long memberId; // Day1/2에서 member 있으면 FK로 바꿔도 됨

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Builder
    private Reservation(Event event, Seat seat, Long memberId, LocalDateTime reservedAt) {
        this.event = event;
        this.seat = seat;
        this.memberId = memberId;
        this.reservedAt = reservedAt;
    }
}
