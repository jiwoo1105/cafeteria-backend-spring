package com.example.easython.repository;

import com.example.easython.domain.Table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends MongoRepository<Table, String> {
    List<Table> findByIsAvailableTrue();
    Optional<Table> findByTableNumber(String tableNumber);
    Optional<Table> findByQrCode(String qrCode);
}

