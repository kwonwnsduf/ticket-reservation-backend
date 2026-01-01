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
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id",nullable=false, unique = true)
    private Seat seat;

    // Day1/2에서 member 있으면 FK로 바꿔도 됨

    @Column(nullable = false)
    private LocalDateTime reservedAt;


    @Builder
    public Reservation(Member member, Seat seat) {
        this.member=member;
        this.seat=seat;
        this.reservedAt=LocalDateTime.now();
    }
}


