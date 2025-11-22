package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.TableDto;
import com.example.easython.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    /**
     * 사용 가능한 테이블 조회
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<TableDto>>> getAvailableTables() {
        List<TableDto> tables = tableService.getAvailableTables();
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 테이블 조회 성공", tables));
    }

    /**
     * QR 코드로 테이블 조회
     */
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ApiResponse<TableDto>> getTableByQrCode(@PathVariable String qrCode) {
        try {
            TableDto table = tableService.getTableByQrCode(qrCode);
            return ResponseEntity.ok(ApiResponse.success("테이블 조회 성공", table));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 테이블 ID로 조회
     */
    @GetMapping("/{tableId}")
    public ResponseEntity<ApiResponse<TableDto>> getTableById(@PathVariable String tableId) {
        try {
            TableDto table = tableService.getTableById(tableId);
            return ResponseEntity.ok(ApiResponse.success("테이블 조회 성공", table));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 모든 테이블 조회 (점유 현황 확인용)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TableDto>>> getAllTables() {
        List<TableDto> tables = tableService.getAllTables();
        return ResponseEntity.ok(ApiResponse.success("전체 테이블 조회 성공", tables));
    }

    /**
     * 테이블 해제 (퇴식 처리)
     */
    @PutMapping("/{tableId}/release")
    public ResponseEntity<ApiResponse<TableDto>> releaseTable(@PathVariable String tableId) {
        try {
            TableDto table = tableService.releaseTable(tableId);
            return ResponseEntity.ok(ApiResponse.success("테이블 해제 성공", table));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * QR 코드로 테이블 해제 (퇴식 처리)
     */
    @PutMapping("/qr/{qrCode}/release")
    public ResponseEntity<ApiResponse<TableDto>> releaseTableByQrCode(@PathVariable String qrCode) {
        try {
            TableDto table = tableService.releaseTableByQrCode(qrCode);
            return ResponseEntity.ok(ApiResponse.success("테이블 해제 성공", table));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

