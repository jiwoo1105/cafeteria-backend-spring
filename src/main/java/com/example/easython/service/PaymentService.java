package com.example.easython.service;

import com.example.easython.domain.Order;
import com.example.easython.domain.Payment;
import com.example.easython.domain.Table;
import com.example.easython.dto.OrderDto;
import com.example.easython.dto.PaymentDto;
import com.example.easython.dto.PaymentRequestDto;
import com.example.easython.repository.OrderRepository;
import com.example.easython.repository.PaymentRepository;
import com.example.easython.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final CartService cartService;
    private final OrderService orderService;

    /**
     * 결제 생성 및 처리 (장바구니에서 주문 자동 생성 후 결제)
     */
    @Transactional
    public PaymentDto processPayment(String userId, PaymentRequestDto request) {
        // 테이블 조회
        Table table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

        // 장바구니 조회
        var cartDto = cartService.getCart(userId, request.getTableId());
        
        if (cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        // 장바구니 금액 검증
        if (cartDto.getTotalPrice().compareTo(request.getAmount()) != 0) {
            throw new RuntimeException("결제 금액이 장바구니 총 금액과 일치하지 않습니다.");
        }

        // 주문 자동 생성 (장바구니에서)
        var orderDto = orderService.createOrderFromCart(userId, request.getTableId());
        
        // 주문 조회 (생성된 주문)
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new RuntimeException("주문 생성 실패"));

        // 결제 생성
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(userId)
                .tableId(request.getTableId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(Payment.PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // 주문 상태를 결제 완료로 변경
        order.setStatus(Order.OrderStatus.PAYED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // 테이블 상태를 이용 중으로 변경 (결제 완료 시 점유)
        table.setIsAvailable(false);
        table.setUpdatedAt(LocalDateTime.now());
        tableRepository.save(table);

        // 장바구니 비우기 (이미 OrderService에서 처리되었을 수도 있지만 확실히)
        cartService.clearCart(userId, request.getTableId());

        return convertToDto(savedPayment);
    }

    /**
     * 결제 조회 (주문 ID로)
     */
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다."));
        return convertToDto(payment);
    }

    /**
     * 사용자별 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByUserId(String userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Payment 엔티티를 PaymentDto로 변환
     */
    private PaymentDto convertToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .tableId(payment.getTableId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

