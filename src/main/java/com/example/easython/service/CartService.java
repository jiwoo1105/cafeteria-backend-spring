package com.example.easython.service;

import com.example.easython.domain.Cart;
import com.example.easython.domain.Menu;
import com.example.easython.dto.CartDto;
import com.example.easython.dto.CartRequestDto;
import com.example.easython.repository.CartRepository;
import com.example.easython.repository.MenuRepository;
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
public class CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final TableRepository tableRepository;

    /**
     * 장바구니 조회 또는 생성
     */
    @Transactional
    public CartDto getOrCreateCart(String userId, String tableId) {
        Cart cart = cartRepository.findByUserIdAndTableId(userId, tableId)
                .orElseGet(() -> {
                    var table = tableRepository.findById(tableId)
                            .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

                    return Cart.builder()
                            .userId(userId)
                            .tableId(tableId)
                            .tableNumber(table.getTableNumber())
                            .items(List.of())
                            .totalPrice(BigDecimal.ZERO)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                });
        cart = cartRepository.save(cart);
        return convertToDto(cart);
    }

    /**
     * 장바구니에 메뉴 추가
     */
    @Transactional
    public CartDto addToCart(String userId, CartRequestDto request) {
        var table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUserIdAndTableId(userId, request.getTableId())
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .tableId(request.getTableId())
                        .tableNumber(table.getTableNumber())
                        .items(List.of())
                        .totalPrice(BigDecimal.ZERO)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        // 장바구니에 항목 추가
        List<Cart.CartItem> cartItems = request.getItems().stream()
                .map(itemRequest -> {
                    Menu menu = menuRepository.findById(itemRequest.getMenuId())
                            .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + itemRequest.getMenuId()));

                    return Cart.CartItem.builder()
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

        // 기존 항목에 새 항목 추가
        List<Cart.CartItem> updatedItems = cart.getItems() != null 
                ? cart.getItems() 
                : List.of();
        updatedItems = new java.util.ArrayList<>(updatedItems);
        updatedItems.addAll(cartItems);

        // 총 가격 계산
        BigDecimal totalPrice = updatedItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setItems(updatedItems);
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return convertToDto(savedCart);
    }

    /**
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public CartDto getCart(String userId, String tableId) {
        Cart cart = cartRepository.findByUserIdAndTableId(userId, tableId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
        return convertToDto(cart);
    }

    /**
     * 장바구니 비우기
     */
    @Transactional
    public void clearCart(String userId, String tableId) {
        cartRepository.deleteByUserIdAndTableId(userId, tableId);
    }

    /**
     * Cart 엔티티를 CartDto로 변환
     */
    private CartDto convertToDto(Cart cart) {
        List<CartDto.CartItemDto> items = cart.getItems() != null
                ? cart.getItems().stream()
                        .map(item -> CartDto.CartItemDto.builder()
                                .menuId(item.getMenuId())
                                .menuName(item.getMenuName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .spicinessLevel(item.getSpicinessLevel())
                                .riceAmount(item.getRiceAmount())
                                .additionalOptions(item.getAdditionalOptions())
                                .comment(item.getComment())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .tableId(cart.getTableId())
                .tableNumber(cart.getTableNumber())
                .items(items)
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}

