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
     @Column(nullable = false)
     private LocalDateTime expiredAt;

     private LocalDateTime confirmedAt;

    @Builder
    private Reservation (Member member, Seat seat,LocalDateTime expiredAt) {
        this.member = member;
        this.seat = seat;
    this.status = ReservationStatus.HOLD;
        this.reservedAt = LocalDateTime.now();
        this.expiredAt=expiredAt;
    }
    public static Reservation createHold(Member member, Seat seat,int holdMinutes) {
        return Reservation.builder().member(member).seat(seat).expiredAt(LocalDateTime.now().plusMinutes(holdMinutes)).build();}
    public boolean isCanceled() {
        return this.status == ReservationStatus.CANCELED;
    }
    public boolean isHold() {
        return this.status == ReservationStatus.HOLD;
    }


    public void cancel() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new ApiException(ErrorCode.ALREADY_CANCELED);
        }
        if (this.status != ReservationStatus.HOLD) {
            throw new ApiException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        this.status = ReservationStatus.CANCELED;
    }
    public void confirm() {
        if (this.status != ReservationStatus.HOLD) {
            throw new ApiException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        if (LocalDateTime.now().isAfter(this.expiredAt)) {
            throw new ApiException(ErrorCode.RESERVATION_EXPIRED);
        }
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
    public void expire() {
        if (this.status != ReservationStatus.HOLD) {
            return; // 이미 다른 상태면 조용히 무시(스케줄러에서 편함)
        }
        this.status = ReservationStatus.EXPIRED;
    }


}


