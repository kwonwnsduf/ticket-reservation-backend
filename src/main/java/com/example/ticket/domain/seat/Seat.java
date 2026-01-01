package com.example.ticket.domain.seat;

import com.example.ticket.domain.event.Event;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "seat_no"}))
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "seat_no", nullable = false)
    private String seatNo; // ex) A1, A2...
    @Column(nullable = false)
    private boolean reserved;

    @Builder
    private Seat(Event event, String seatNo) {
        this.event = event;
        this.seatNo = seatNo;
        this.reserved=false;
    }
    public void reserve(){
        this.reserved=true;
    }
    public void cancelReserve(){
        this.reserved=false;
    }
}
