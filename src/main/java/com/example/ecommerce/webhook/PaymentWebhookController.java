package com.example.ecommerce.webhook;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class PaymentWebhookController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<String> handleWebhook(@RequestBody PaymentWebhookRequest request) {
        paymentService.processWebhook(request.getPaymentId(), request.getStatus());
        return ResponseEntity.ok("Webhook processed");
    }
}
