package com.example.easython.repository;

import com.example.easython.domain.OrderHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHistoryRepository extends MongoRepository<OrderHistory, String> {
    List<OrderHistory> findByUserIdOrderByOrderCountDesc(String userId);
    Optional<OrderHistory> findByUserIdAndMenuId(String userId, String menuId);
    List<OrderHistory> findTop3ByUserIdOrderByOrderCountDesc(String userId);
}

