package com.ciberaccion.stayly.paymentservice.dto.response;

import com.ciberaccion.stayly.paymentservice.model.enums.PaymentStatus;
import com.ciberaccion.stayly.paymentservice.model.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private UUID bookingId;
    private UUID userId;
    private UUID hotelId;
    private UUID roomId;
    private PaymentType paymentType;
    private PaymentStatus status;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}