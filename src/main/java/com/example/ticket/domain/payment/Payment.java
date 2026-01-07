package com.example.ticket.domain.payment;

import com.example.ticket.domain.reservation.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="payments",uniqueConstraints={@UniqueConstraint(columnNames={"reservation_id"})})
public class Payment {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="reservation_id",nullable = false)
    private Reservation reservation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(nullable = false)
    private Long amount;

    private String failReason;

    @Column(nullable=false)
    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    @Builder
    private Payment(Reservation reservation, Long amount) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = PaymentStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    public static Payment request(Reservation reservation,Long amount){
        return Payment.builder().reservation(reservation).amount(amount).build();
    }
    public void succeed() {
        this.status = PaymentStatus.SUCCESS;
        this.completedAt = LocalDateTime.now();
        this.failReason = null;
    }
    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.failReason = reason;
    }
    public boolean isSuccess() { return status == PaymentStatus.SUCCESS; }
    public boolean isFailed() { return status == PaymentStatus.FAILED; }

}
