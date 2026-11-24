package com.ticketcompany.payment_service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ticketcompany.payment_service.bank.FakeBank;
import com.ticketcompany.payment_service.messaging.PaymentCompletedEvent;
import com.ticketcompany.payment_service.messaging.PaymentRequestedEvent;
import com.ticketcompany.payment_service.messaging.PaymentResultPublisher;

@Service
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    private final PaymentRepository paymentRepository;
    private final FakeBank fakeBank;
    private final PaymentResultPublisher resultPublisher;

    public PaymentProcessor(PaymentRepository paymentRepository, FakeBank fakeBank,
                            PaymentResultPublisher resultPublisher) {
        this.paymentRepository = paymentRepository;
        this.fakeBank = fakeBank;
        this.resultPublisher = resultPublisher;
    }

    public void process(PaymentRequestedEvent request) {
        Payment payment;
        try {
            payment = paymentRepository.save(
                    new Payment(request.bookingId(), request.amount(), request.paymentToken()));
        } catch (DataIntegrityViolationException e) {
            handleDuplicate(request.bookingId());
            return;
        }
        completeCharge(payment);
    }

    private void handleDuplicate(Long bookingId) {
        Payment existing = paymentRepository.findByBookingId(bookingId).orElseThrow();

        if (existing.getStatus() == PaymentStatus.PROCESSING) {
            log.warn("Payment for booking {} was left PROCESSING; asking the bank again with the same key", bookingId);
            completeCharge(existing);
            return;
        }

        log.info("Duplicate payment request for booking {}; re-sending result {}", bookingId, existing.getStatus());
        resultPublisher.publish(new PaymentCompletedEvent(bookingId, existing.getStatus().name()));
    }

    private void completeCharge(Payment payment) {
        String outcome = fakeBank.charge(payment.getBookingId(), payment.getAmount(), payment.getPaymentToken());

        if ("APPROVED".equals(outcome)) {
            payment.approve();
        } else {
            payment.decline();
        }
        paymentRepository.save(payment);

        resultPublisher.publish(new PaymentCompletedEvent(payment.getBookingId(), payment.getStatus().name()));
        log.info("Completed payment for booking {}: {}", payment.getBookingId(), payment.getStatus());
    }
}