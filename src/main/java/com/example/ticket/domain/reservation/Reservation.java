package com.example.ticket.domain.reservation;

import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
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




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id",nullable=false)
    private Seat seat;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // Day1/2에서 member 있으면 FK로 바꿔도 됨

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Builder
    private Reservation (Member member, Seat seat) {
        this.member = member;
        this.seat = seat;
    this.status = ReservationStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
    }
    public static Reservation create(Member member, Seat seat) {
        return new Reservation(member, seat);
    }
    public boolean isCanceled() {
        return this.status == ReservationStatus.CANCELED;
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new ApiException(ErrorCode.ALREADY_CANCELED);
        }
        this.status = ReservationStatus.CANCELED;
    }


}


