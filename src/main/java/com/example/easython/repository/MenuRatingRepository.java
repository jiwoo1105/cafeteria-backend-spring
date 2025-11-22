package com.example.easython.repository;

import com.example.easython.domain.MenuRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRatingRepository extends MongoRepository<MenuRating, String> {
    List<MenuRating> findByMenuId(String menuId);
    Optional<MenuRating> findByMenuIdAndUserId(String menuId, String userId);
    List<MenuRating> findByUserId(String userId);
}

