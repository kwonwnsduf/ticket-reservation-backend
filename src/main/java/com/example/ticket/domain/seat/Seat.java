package com.example.ticket.domain.seat;

import com.example.ticket.domain.event.Event;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
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



    @Builder
    private Seat(Event event, String seatNo) {
        this.event = event;
        this.seatNo = seatNo;
        this.status=SeatStatus.AVAILABLE;
    }
    public boolean isOccupied() {
        return this.status == SeatStatus.OCCUPIED;
    }
    public boolean isAvailable() {
        return this.status == SeatStatus.AVAILABLE;
    }


    /** 예매(좌석 점유) */
    public void occupy() {
        if (this.status == SeatStatus.OCCUPIED ){
            // AVAILABLE에서 바로 확정하면 흐름이 깨짐 (정책)
            throw new ApiException(ErrorCode.ALREADY_RESERVED);
        }
        this.status = SeatStatus.OCCUPIED;
    }
    /** 취소(좌석 해제) */
    public void release() {
        if (this.status != SeatStatus.OCCUPIED) {
            throw new ApiException(ErrorCode.INVALID_SEAT_STATUS);
        }
        this.status = SeatStatus.AVAILABLE;
    }
}
