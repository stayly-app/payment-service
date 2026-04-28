package com.ciberaccion.stayly.paymentservice.listener;

import com.ciberaccion.stayly.paymentservice.model.Payment;
import com.ciberaccion.stayly.paymentservice.model.enums.PaymentStatus;
import com.ciberaccion.stayly.paymentservice.model.enums.PaymentType;
import com.ciberaccion.stayly.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @SqsListener("${booking.events.queue.url}")
    @Transactional
    public void onBookingConfirmed(String message) {
        try {
            // Extraer mensaje del envelope SNS
            JsonNode envelope = objectMapper.readTree(message);
            String actualMessage = envelope.has("Message")
                    ? envelope.path("Message").asText()
                    : message;

            JsonNode root = objectMapper.readTree(actualMessage);
            JsonNode payload = root.path("payload");

            UUID bookingId = UUID.fromString(payload.path("bookingId").asText());

            // Idempotencia
            if (paymentRepository.existsByBookingId(bookingId)) {
                log.info("Payment already processed for bookingId: {}, skipping", bookingId);
                return;
            }

            UUID userId = UUID.fromString(payload.path("userId").asText());
            UUID hotelId = UUID.fromString(payload.path("hotelId").asText());
            UUID roomId = UUID.fromString(payload.path("roomId").asText());
            BigDecimal totalAmount = new BigDecimal(payload.path("totalAmount").asText());

            log.info("Processing payment for bookingId: {}", bookingId);
            boolean paymentSuccess = processPayment(totalAmount);

            Payment payment = Payment.builder()
                    .bookingId(bookingId)
                    .userId(userId)
                    .hotelId(hotelId)
                    .roomId(roomId)
                    .paymentType(PaymentType.CREDIT_CARD)
                    .status(paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                    .totalAmount(totalAmount)
                    .currency("USD")
                    .build();

            paymentRepository.save(payment);
            log.info("Payment {} for bookingId: {}", payment.getStatus(), bookingId);

        } catch (Exception e) {
            log.error("Failed to process BookingConfirmed event", e);
        }
    }

    private boolean processPayment(BigDecimal amount) {
        log.info("Simulating payment processing for amount: {}", amount);
        return true;
    }
}