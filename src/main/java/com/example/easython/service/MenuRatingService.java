package com.example.easython.service;

import com.example.easython.domain.MenuRating;
import com.example.easython.dto.MenuRatingDto;
import com.example.easython.dto.RatingRequestDto;
import com.example.easython.repository.MenuRatingRepository;
import com.example.easython.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuRatingService {

    private final MenuRatingRepository menuRatingRepository;
    private final MenuRepository menuRepository;

    /**
     * 메뉴 평가 생성 또는 업데이트
     */
    @Transactional
    public MenuRatingDto createOrUpdateRating(String userId, String menuId, RatingRequestDto request) {
        // 메뉴 존재 확인
        menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다."));

        // 기존 평가가 있으면 업데이트, 없으면 생성
        MenuRating rating = menuRatingRepository.findByMenuIdAndUserId(menuId, userId)
                .map(existing -> {
                    existing.setRating(request.getRating());
                    existing.setComment(request.getComment());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElse(MenuRating.builder()
                        .menuId(menuId)
                        .userId(userId)
                        .rating(request.getRating())
                        .comment(request.getComment())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        MenuRating savedRating = menuRatingRepository.save(rating);
        return convertToDto(savedRating);
    }

    /**
     * 메뉴별 평가 조회
     */
    @Transactional(readOnly = true)
    public List<MenuRatingDto> getRatingsByMenuId(String menuId) {
        List<MenuRating> ratings = menuRatingRepository.findByMenuId(menuId);
        return ratings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 평가 조회
     */
    @Transactional(readOnly = true)
    public List<MenuRatingDto> getRatingsByUserId(String userId) {
        List<MenuRating> ratings = menuRatingRepository.findByUserId(userId);
        return ratings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 평가 삭제
     */
    @Transactional
    public void deleteRating(String ratingId) {
        menuRatingRepository.deleteById(ratingId);
    }

    /**
     * MenuRating 엔티티를 MenuRatingDto로 변환
     */
    private MenuRatingDto convertToDto(MenuRating rating) {
        String menuName = menuRepository.findById(rating.getMenuId())
                .map(menu -> menu.getName())
                .orElse("알 수 없음");

        return MenuRatingDto.builder()
                .id(rating.getId())
                .menuId(rating.getMenuId())
                .menuName(menuName)
                .userId(rating.getUserId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}

