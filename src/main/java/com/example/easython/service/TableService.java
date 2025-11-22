package com.example.easython.service;

import com.example.easython.domain.Table;
import com.example.easython.dto.TableDto;
import com.example.easython.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    /**
     * 사용 가능한 모든 테이블 조회
     */
    @Transactional(readOnly = true)
    public List<TableDto> getAvailableTables() {
        List<Table> tables = tableRepository.findByIsAvailableTrue();
        return tables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * QR 코드로 테이블 조회
     */
    @Transactional(readOnly = true)
    public TableDto getTableByQrCode(String qrCode) {
        Table table = tableRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR 코드에 해당하는 테이블을 찾을 수 없습니다."));
        return convertToDto(table);
    }

    /**
     * 테이블 ID로 조회
     */
    @Transactional(readOnly = true)
    public TableDto getTableById(String tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));
        return convertToDto(table);
    }

    /**
     * 모든 테이블 조회 (점유 현황 포함)
     */
    @Transactional(readOnly = true)
    public List<TableDto> getAllTables() {
        List<Table> tables = tableRepository.findAll();
        return tables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 테이블 해제 (퇴식 처리)
     */
    @Transactional
    public TableDto releaseTable(String tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

        table.setIsAvailable(true);
        table.setUpdatedAt(LocalDateTime.now());

        Table savedTable = tableRepository.save(table);
        return convertToDto(savedTable);
    }

    /**
     * QR 코드로 테이블 해제 (퇴식 처리)
     */
    @Transactional
    public TableDto releaseTableByQrCode(String qrCode) {
        Table table = tableRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR 코드에 해당하는 테이블을 찾을 수 없습니다."));

        table.setIsAvailable(true);
        table.setUpdatedAt(LocalDateTime.now());

        Table savedTable = tableRepository.save(table);
        return convertToDto(savedTable);
    }

    /**
     * Table 엔티티를 TableDto로 변환
     */
    private TableDto convertToDto(Table table) {
        return TableDto.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .restaurantName(table.getRestaurantName())
                .isAvailable(table.getIsAvailable())
                .qrCode(table.getQrCode())
                .build();
    }
}

