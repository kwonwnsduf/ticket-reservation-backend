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
    @JoinColumn(name="reservation_id")
    private Reservation reservation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(nullable = false)
    private Long amount;
    @Column(length = 100)
    private String txId;

    private String failReason;

    @Column(nullable=false)
    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    private Payment(Long amount) {
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }
    public static Payment pending(Long amount) {
        return new Payment(amount);
    }
    public void markCharged(String txId) {
        this.status = PaymentStatus.CHARGED;
        this.txId = txId;
    }
//   @Builder
//    private Payment(Reservation reservation, Long amount) {
//        this.reservation = reservation;
//        this.amount = amount;
//        this.status = PaymentStatus.REQUESTED;
//        this.requestedAt = LocalDateTime.now();
//    }

//    public static Payment request(Reservation reservation,Long amount){
//        return Payment.builder().reservation(reservation).amount(amount).build();
//    }
  public void succeed(Reservation reservation) {
        this.status = PaymentStatus.SUCCESS;
        this.reservation=reservation;
        this.completedAt = LocalDateTime.now();
        this.failReason = null;
    }
    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.failReason = reason;
    }
    public void cancel() {
        this.status = PaymentStatus.CANCELED;
        this.completedAt = LocalDateTime.now();
    }
    public void cancelFailed(String reason) {
        this.status = PaymentStatus.CANCEL_FAILED;
        this.completedAt = LocalDateTime.now();
        this.failReason = reason;
    }


}
