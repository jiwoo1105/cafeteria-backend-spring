package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.PaymentDto;
import com.example.easython.dto.PaymentRequestDto;
import com.example.easython.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 처리
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @PathVariable String userId,
            @Valid @RequestBody PaymentRequestDto request) {
        try {
            PaymentDto payment = paymentService.processPayment(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("결제 완료", payment));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 결제 조회 (주문 ID로)
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrderId(@PathVariable String orderId) {
        try {
            PaymentDto payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(ApiResponse.success("결제 조회 성공", payment));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자별 결제 내역 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByUserId(@PathVariable String userId) {
        List<PaymentDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("결제 내역 조회 성공", payments));
    }
}

