package com.ticketcompany.payment_service.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_seq", allocationSize = 50)
    private Long id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_token", nullable = false)
    private String paymentToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Payment() {
    }

    public Payment(Long bookingId, BigDecimal amount, String paymentToken) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentToken = paymentToken;
        this.status = PaymentStatus.PROCESSING;
        this.createdAt = Instant.now();
    }

    public void approve() {
        requireProcessing();
        this.status = PaymentStatus.APPROVED;
    }

    public void decline() {
        requireProcessing();
        this.status = PaymentStatus.DECLINED;
    }

    private void requireProcessing() {
        if (status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Payment " + id + " is already " + status);
        }
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentToken() { return paymentToken; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}