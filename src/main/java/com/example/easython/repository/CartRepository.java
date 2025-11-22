package com.example.easython.repository;

import com.example.easython.domain.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserIdAndTableId(String userId, String tableId);
    List<Cart> findByUserId(String userId);
    void deleteByUserIdAndTableId(String userId, String tableId);
}

