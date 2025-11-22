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
public class MenuListResponseDto {
    private List<MenuDto> allMenus; // 전체 메뉴
    private List<MenuDto> popularMenus; // 인기 메뉴 3개 (사용자 이력 기반)
}

