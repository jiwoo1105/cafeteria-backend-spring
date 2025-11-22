package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.CartDto;
import com.example.easython.dto.CartRequestDto;
import com.example.easython.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 조회 또는 생성
     */
    @GetMapping("/user/{userId}/table/{tableId}")
    public ResponseEntity<ApiResponse<CartDto>> getOrCreateCart(
            @PathVariable String userId,
            @PathVariable String tableId) {
        try {
            CartDto cart = cartService.getOrCreateCart(userId, tableId);
            return ResponseEntity.ok(ApiResponse.success("장바구니 조회 성공", cart));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 장바구니에 메뉴 추가
     */
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
            @PathVariable String userId,
            @Valid @RequestBody CartRequestDto request) {
        try {
            CartDto cart = cartService.addToCart(userId, request);
            return ResponseEntity.ok(ApiResponse.success("장바구니에 추가 성공", cart));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 장바구니 비우기
     */
    @DeleteMapping("/user/{userId}/table/{tableId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @PathVariable String userId,
            @PathVariable String tableId) {
        try {
            cartService.clearCart(userId, tableId);
            return ResponseEntity.ok(ApiResponse.success("장바구니 비우기 성공", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

