package com.example.easython.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDto {
    private String tableId;
    private List<CartItemRequestDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemRequestDto {
        private String menuId;
        private Integer quantity;
        private Integer spicinessLevel; // 매움 단계 (0-5)
        private String riceAmount; // 밥 양 (보통, 적게, 많이)
        private List<String> additionalOptions; // 추가 옵션 목록
        private String comment; // 코멘트
    }
}

