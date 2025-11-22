package com.example.easython.service;

import com.example.easython.domain.Menu;
import com.example.easython.domain.Order;
import com.example.easython.domain.OrderHistory;
import com.example.easython.domain.Notification;
import com.example.easython.dto.CartDto;
import com.example.easython.dto.OrderDto;
import com.example.easython.dto.OrderRequestDto;
import com.example.easython.repository.MenuRepository;
import com.example.easython.repository.OrderRepository;
import com.example.easython.repository.OrderHistoryRepository;
import com.example.easython.repository.NotificationRepository;
import com.example.easython.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final TableRepository tableRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final CartService cartService;

    /**
     * 장바구니에서 주문 생성
     */
    @Transactional
    public OrderDto createOrderFromCart(String userId, String tableId) {
        // 장바구니 조회
        CartDto cartDto = cartService.getCart(userId, tableId);
        
        if (cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        // 테이블 조회
        var table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

        // 주문 항목 생성
        List<Order.OrderItem> orderItems = cartDto.getItems().stream()
                .map(cartItem -> Order.OrderItem.builder()
                        .menuId(cartItem.getMenuId())
                        .menuName(cartItem.getMenuName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .spicinessLevel(cartItem.getSpicinessLevel())
                        .riceAmount(cartItem.getRiceAmount())
                        .additionalOptions(cartItem.getAdditionalOptions())
                        .comment(cartItem.getComment())
                        .build())
                .collect(Collectors.toList());

        // 주문 생성
        Order order = Order.builder()
                .userId(userId)
                .tableId(table.getId())
                .tableNumber(table.getTableNumber())
                .items(orderItems)
                .totalPrice(cartDto.getTotalPrice())
                .status(Order.OrderStatus.IN_CART) // 장바구니에서 생성된 주문
                .orderedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 주문 이력 업데이트
        updateOrderHistory(userId, orderItems);

        return convertToDto(savedOrder);
    }

    /**
     * 주문 생성
     */
    @Transactional
    public OrderDto createOrder(String userId, OrderRequestDto request) {
        // 테이블 조회
        var table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

        // 주문 항목 생성 및 총 가격 계산
        List<Order.OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> {
                    Menu menu = menuRepository.findById(itemRequest.getMenuId())
                            .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + itemRequest.getMenuId()));

                    return Order.OrderItem.builder()
                            .menuId(menu.getId())
                            .menuName(menu.getName())
                            .quantity(itemRequest.getQuantity())
                            .price(menu.getPrice())
                            .spicinessLevel(itemRequest.getSpicinessLevel())
                            .riceAmount(itemRequest.getRiceAmount())
                            .additionalOptions(itemRequest.getAdditionalOptions())
                            .comment(itemRequest.getComment())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 주문 생성
        Order order = Order.builder()
                .userId(userId)
                .tableId(table.getId())
                .tableNumber(table.getTableNumber())
                .items(orderItems)
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 주문 이력 업데이트
        updateOrderHistory(userId, orderItems);

        return convertToDto(savedOrder);
    }

    /**
     * 주문 완료 처리
     */
    @Transactional
    public OrderDto completeOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 알림 생성
        Notification notification = Notification.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .title("주문 완료")
                .message("주문이 완료되었습니다.")
                .type(Notification.NotificationType.ORDER_COMPLETED)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        return convertToDto(savedOrder);
    }

    /**
     * 주문 준비 완료 처리
     */
    @Transactional
    public OrderDto readyOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        order.setStatus(Order.OrderStatus.READY);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 알림 생성
        Notification notification = Notification.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .title("주문 준비 완료")
                .message("주문이 준비되었습니다. 받으러 와주세요.")
                .type(Notification.NotificationType.ORDER_READY)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        return convertToDto(savedOrder);
    }

    /**
     * 사용자별 주문 조회
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 주문 ID로 조회
     */
    @Transactional(readOnly = true)
    public OrderDto getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return convertToDto(order);
    }

    /**
     * 주문 이력 업데이트
     */
    private void updateOrderHistory(String userId, List<Order.OrderItem> orderItems) {
        for (Order.OrderItem item : orderItems) {
            orderHistoryRepository.findByUserIdAndMenuId(userId, item.getMenuId())
                    .ifPresentOrElse(
                            history -> {
                                history.setOrderCount(history.getOrderCount() + item.getQuantity());
                                history.setLastOrderedAt(LocalDateTime.now());
                                history.setUpdatedAt(LocalDateTime.now());
                                orderHistoryRepository.save(history);
                            },
                            () -> {
                                OrderHistory newHistory = OrderHistory.builder()
                                        .userId(userId)
                                        .menuId(item.getMenuId())
                                        .menuName(item.getMenuName())
                                        .orderCount(item.getQuantity())
                                        .lastOrderedAt(LocalDateTime.now())
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .build();
                                orderHistoryRepository.save(newHistory);
                            }
                    );
        }
    }

    /**
     * Order 엔티티를 OrderDto로 변환
     */
    private OrderDto convertToDto(Order order) {
        List<OrderDto.OrderItemDto> items = order.getItems().stream()
                .map(item -> OrderDto.OrderItemDto.builder()
                        .menuId(item.getMenuId())
                        .menuName(item.getMenuName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .spicinessLevel(item.getSpicinessLevel())
                        .riceAmount(item.getRiceAmount())
                        .additionalOptions(item.getAdditionalOptions())
                        .comment(item.getComment())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .tableId(order.getTableId())
                .tableNumber(order.getTableNumber())
                .items(items)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }
}

