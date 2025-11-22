package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.OrderDto;
import com.example.easython.dto.OrderRequestDto;
import com.example.easython.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 장바구니에서 주문 생성
     */
    @PostMapping("/user/{userId}/table/{tableId}/from-cart")
    public ResponseEntity<ApiResponse<OrderDto>> createOrderFromCart(
            @PathVariable String userId,
            @PathVariable String tableId) {
        try {
            OrderDto order = orderService.createOrderFromCart(userId, tableId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("주문 생성 성공", order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 주문 생성
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @PathVariable String userId,
            @Valid @RequestBody OrderRequestDto request) {
        try {
            OrderDto order = orderService.createOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("주문 생성 성공", order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 주문 완료 처리
     */
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse<OrderDto>> completeOrder(@PathVariable String orderId) {
        try {
            OrderDto order = orderService.completeOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 완료 처리 성공", order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 주문 준비 완료 처리 (메뉴 나오면)
     */
    @PutMapping("/{orderId}/ready")
    public ResponseEntity<ApiResponse<OrderDto>> readyOrder(@PathVariable String orderId) {
        try {
            OrderDto order = orderService.readyOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 준비 완료 처리 성공", order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자별 주문 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByUserId(@PathVariable String userId) {
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("주문 조회 성공", orders));
    }

    /**
     * 주문 ID로 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable String orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 조회 성공", order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

