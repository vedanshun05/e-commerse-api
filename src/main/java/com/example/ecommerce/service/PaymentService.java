package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String MOCK_PAYMENT_URL = "http://localhost:8081/payments/create";

    public Payment createPayment(String orderId, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("CREATED")) {
            throw new RuntimeException("Order already processed or cancelled");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPaymentId("pay_" + UUID.randomUUID().toString());
        paymentRepository.save(payment);

        // Call Mock Service
        try {
            // Manual JSON construction to ensure simplicity and control
            String jsonRequest = String.format("{\"orderId\":\"%s\",\"amount\":%.2f,\"paymentId\":\"%s\"}",
                    orderId, amount, payment.getPaymentId());

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonRequest,
                    headers);

            // Fire and forget
            new Thread(() -> {
                try {
                    restTemplate.postForObject(MOCK_PAYMENT_URL, entity, String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payment;
    }

    public void processWebhook(String paymentId, String status) {
        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentId().equals(paymentId)) // In real app, findByPaymentId
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(status)) {
            order.setStatus("PAID");
        } else {
            order.setStatus("FAILED");
        }
        orderRepository.save(order);
    }
}
