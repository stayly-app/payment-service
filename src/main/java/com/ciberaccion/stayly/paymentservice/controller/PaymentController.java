package com.ciberaccion.stayly.paymentservice.controller;

import com.ciberaccion.stayly.paymentservice.dto.response.PaymentResponse;
import com.ciberaccion.stayly.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> findByBookingId(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(paymentService.findByBookingId(bookingId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(paymentService.findByUserId(userId));
    }
}