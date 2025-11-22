package com.example.easython.repository;

import com.example.easython.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByTableId(String tableId);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByOrderedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<Order> findByTableIdAndStatus(String tableId, Order.OrderStatus status);
}

