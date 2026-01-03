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
    private Long seatId;
    private Long memberId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id",nullable=false, unique = true)
    private Seat seat;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // Day1/2에서 member 있으면 FK로 바꿔도 됨

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Builder
    private Reservation(Member member, Seat seat) {
        this.member = member;
        this.seat = seat;
        this.status = ReservationStatus.PENDING;
        this.reservedAt = LocalDateTime.now();
    }
    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태만 확정 가능");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예매는 취소 정책에 따라 처리해야 합니다.");
        }
        this.status = ReservationStatus.CANCELED;
    }


}


