package com.ciberaccion.stayly.paymentservice.repository;

import com.ciberaccion.stayly.paymentservice.model.Payment;
import com.ciberaccion.stayly.paymentservice.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);

    List<Payment> findByUserId(UUID userId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByBookingId(UUID bookingId);
}