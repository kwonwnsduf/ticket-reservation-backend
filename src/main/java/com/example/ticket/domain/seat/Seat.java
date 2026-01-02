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
    @Enumerated(EnumType.STRING)
    private SeatStatus status=SeatStatus.AVAILABLE;
    @Version
    private Long version;

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
    public void hold(){
        if(status!=SeatStatus.AVAILABLE){
            throw new IllegalStateException("이미 예매된 좌석입니다.");
        }
        status=SeatStatus.HELD;
    }
    public void sold(){
        if(status != SeatStatus.HELD){
            throw new IllegalStateException("임시 예매 상태가 아닙니다.");
        }
        status=SeatStatus.SOLD;
    }
    public void release(){
        status=SeatStatus.AVAILABLE;
    }
}
