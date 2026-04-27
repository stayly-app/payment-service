package com.ciberaccion.stayly.paymentservice.service;

import com.ciberaccion.stayly.paymentservice.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse findById(UUID id);

    PaymentResponse findByBookingId(UUID bookingId);

    List<PaymentResponse> findByUserId(UUID userId);
}