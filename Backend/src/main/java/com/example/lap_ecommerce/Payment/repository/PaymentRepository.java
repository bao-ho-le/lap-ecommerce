package com.example.lap_ecommerce.Payment.repository;


import com.example.lap_ecommerce.Payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrder_Id(Long orderId);

}