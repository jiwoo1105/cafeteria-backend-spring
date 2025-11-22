package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.MenuRatingDto;
import com.example.easython.dto.RatingRequestDto;
import com.example.easython.service.MenuRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final MenuRatingService menuRatingService;

    /**
     * 메뉴 평가 생성 또는 업데이트
     */
    @PostMapping("/menu/{menuId}/user/{userId}")
    public ResponseEntity<ApiResponse<MenuRatingDto>> createOrUpdateRating(
            @PathVariable String menuId,
            @PathVariable String userId,
            @Valid @RequestBody RatingRequestDto request) {
        try {
            MenuRatingDto rating = menuRatingService.createOrUpdateRating(userId, menuId, request);
            return ResponseEntity.ok(ApiResponse.success("메뉴 평가 저장 성공", rating));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 메뉴별 평가 조회
     */
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponse<List<MenuRatingDto>>> getRatingsByMenuId(@PathVariable String menuId) {
        List<MenuRatingDto> ratings = menuRatingService.getRatingsByMenuId(menuId);
        return ResponseEntity.ok(ApiResponse.success("메뉴 평가 조회 성공", ratings));
    }

    /**
     * 사용자별 평가 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<MenuRatingDto>>> getRatingsByUserId(@PathVariable String userId) {
        List<MenuRatingDto> ratings = menuRatingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 평가 조회 성공", ratings));
    }

    /**
     * 메뉴 평가 삭제
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> deleteRating(@PathVariable String ratingId) {
        try {
            menuRatingService.deleteRating(ratingId);
            return ResponseEntity.ok(ApiResponse.success("메뉴 평가 삭제 성공", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

