package com.ciberaccion.stayly.paymentservice.service.impl;

import com.ciberaccion.stayly.paymentservice.dto.response.PaymentResponse;
import com.ciberaccion.stayly.paymentservice.exception.ResourceNotFoundException;
import com.ciberaccion.stayly.paymentservice.model.Payment;
import com.ciberaccion.stayly.paymentservice.repository.PaymentRepository;
import com.ciberaccion.stayly.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findByBookingId(UUID bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for bookingId: " + bookingId));

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ---- Mapper ----

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .hotelId(payment.getHotelId())
                .roomId(payment.getRoomId())
                .paymentType(payment.getPaymentType())
                .status(payment.getStatus())
                .totalAmount(payment.getTotalAmount())
                .currency(payment.getCurrency())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}