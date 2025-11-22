package com.example.easython.repository;

import com.example.easython.domain.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
    List<Menu> findByIsAvailableTrue();
    List<Menu> findByAvailableDate(LocalDate date);
    List<Menu> findByRestaurantName(String restaurantName);
    List<Menu> findByRestaurantNameAndIsAvailableTrue(String restaurantName);
    Optional<Menu> findByName(String name);
}

