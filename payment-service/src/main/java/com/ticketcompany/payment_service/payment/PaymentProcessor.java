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

        String outcome = fakeBank.charge(request.amount(), request.paymentToken());

        if ("APPROVED".equals(outcome)) {
            payment.approve();
        } else {
            payment.decline();
        }
        paymentRepository.save(payment);

        resultPublisher.publish(new PaymentCompletedEvent(request.bookingId(), payment.getStatus().name()));
        log.info("Charged booking {}: {}", request.bookingId(), payment.getStatus());
    }

    private void handleDuplicate(Long bookingId) {
        Payment existing = paymentRepository.findByBookingId(bookingId).orElseThrow();

        if (existing.getStatus() == PaymentStatus.PROCESSING) {
            log.warn("Payment for booking {} is still PROCESSING; not charging again", bookingId);
            return;
        }

        log.info("Duplicate payment request for booking {}; re-sending result {}", bookingId, existing.getStatus());
        resultPublisher.publish(new PaymentCompletedEvent(bookingId, existing.getStatus().name()));
    }
}