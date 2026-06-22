package com.example.lap_ecommerce.Payment.service;

import com.example.lap_ecommerce.Order.entity.Order;
import com.example.lap_ecommerce.Order.repository.OrderRepository;
import com.example.lap_ecommerce.Payment.dto.request.CreateCodPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.request.CreateVnPayPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.response.PaymentResponse;
import com.example.lap_ecommerce.Payment.entity.Payment;
import com.example.lap_ecommerce.Payment.entity.PaymentMethod;
import com.example.lap_ecommerce.Payment.entity.PaymentStatus;
import com.example.lap_ecommerce.Payment.repository.PaymentRepository;

import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentResponse createCodPayment(CreateCodPaymentRequest request) {

        if (paymentRepository.findByOrder_Id(request.orderId()).isPresent()) {
            throw new IllegalStateException("Order already has a payment");
        }

        Order order = findOrder(request.orderId());

        Payment payment = Payment.builder()
                .order(order)
                .method(PaymentMethod.COD)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);

        return toResponse(saved, null);
    }

    @Override
    public PaymentResponse createVnPayPayment(CreateVnPayPaymentRequest request) {

        if (paymentRepository.findByOrder_Id(request.orderId()).isPresent()) {
            throw new IllegalStateException("Order already has a payment");
        }

        Order order = findOrder(request.orderId());

        String transactionId = "VN_" +
                UUID.randomUUID().toString().replace("-", "");

        Payment payment = Payment.builder()
                .order(order)
                .method(PaymentMethod.VNPAY)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .transactionId(transactionId)
                .build();

        Payment saved = paymentRepository.save(payment);

        String paymentUrl =
                "http://localhost:8080/payments/callback?txnRef="
                        + transactionId
                        + "&status=success";

        return toResponse(saved, paymentUrl);
    }

    @Override
    public PaymentResponse handleCallback(String txnRef, String status) {

        Payment payment = paymentRepository.findByTransactionId(txnRef)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with transaction id: " + txnRef));

        if ("success".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment updated = paymentRepository.save(payment);

        return toResponse(updated, null);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId));
    }

    private PaymentResponse toResponse(
            Payment payment,
            String paymentUrl
    ) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getOrder().getId(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionId(),
                paymentUrl,
                payment.getPaidAt()
        );
    }
}