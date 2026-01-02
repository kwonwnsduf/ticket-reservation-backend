package com.example.ticket.domain.reservation;

import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.seat.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = true)
    private Member member;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id",nullable=false, unique = true)
    private Seat seat;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status= ReservationStatus.HOLD;

    // Day1/2에서 member 있으면 FK로 바꿔도 됨

    @Column(nullable = false)
    private LocalDateTime reservedAt;


    @Builder
    public Reservation(Member member, Seat seat) {
        this.member=member;
        this.seat=seat;
        this.reservedAt=LocalDateTime.now();
    }
    public Reservation(Seat seat){
        this.seat= seat;
        this.reservedAt = LocalDateTime.now();
        this.status = ReservationStatus.HOLD;
    }
    public void confirm(){
        if(status != ReservationStatus.HOLD){
            throw new IllegalStateException("HOLD 상태만 확정 가능");
        }
        status=ReservationStatus.CONFIRMED;
    }
    public void cancel(){
        status=ReservationStatus.CANCELED;
    }
}


