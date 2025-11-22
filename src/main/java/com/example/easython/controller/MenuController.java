package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.MenuDto;
import com.example.easython.dto.MenuListResponseDto;
import com.example.easython.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 전체 메뉴 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuDto>>> getAllMenus() {
        List<MenuDto> menus = menuService.getAllMenus();
        return ResponseEntity.ok(ApiResponse.success("전체 메뉴 조회 성공", menus));
    }

    /**
     * 메뉴 페이지 정보 조회 (전체 메뉴 + 인기 메뉴 3개)
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<MenuListResponseDto>> getMenuPage(
            @RequestParam(required = false) String userId) {
        MenuListResponseDto menuPage = menuService.getMenuPage(userId != null ? userId : "");
        return ResponseEntity.ok(ApiResponse.success("메뉴 페이지 조회 성공", menuPage));
    }

    /**
     * 사용자별 인기 메뉴 조회
     */
    @GetMapping("/popular/{userId}")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getPopularMenus(
            @PathVariable String userId,
            @RequestParam(defaultValue = "3") int limit) {
        List<MenuDto> menus = menuService.getPopularMenus(userId, limit);
        return ResponseEntity.ok(ApiResponse.success("인기 메뉴 조회 성공", menus));
    }

    /**
     * 식당별 메뉴 조회
     */
    @GetMapping("/restaurant/{restaurantName}")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getMenusByRestaurant(@PathVariable String restaurantName) {
        List<MenuDto> menus = menuService.getMenusByRestaurant(restaurantName);
        return ResponseEntity.ok(ApiResponse.success("식당별 메뉴 조회 성공", menus));
    }

    /**
     * 메뉴 ID로 조회
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto>> getMenuById(@PathVariable String menuId) {
        try {
            MenuDto menu = menuService.getMenuById(menuId);
            return ResponseEntity.ok(ApiResponse.success("메뉴 조회 성공", menu));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

