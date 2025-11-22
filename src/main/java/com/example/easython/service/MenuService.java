package com.example.easython.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.easython.domain.Menu;
import com.example.easython.domain.MenuRating;
import com.example.easython.domain.OrderHistory;
import com.example.easython.dto.MenuDto;
import com.example.easython.dto.MenuListResponseDto;
import com.example.easython.repository.MenuRatingRepository;
import com.example.easython.repository.MenuRepository;
import com.example.easython.repository.OrderHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuRatingRepository menuRatingRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    /**
     * 전체 메뉴 조회 (오늘 날짜 기준 사용 가능한 메뉴)
     */
    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus() {
        List<Menu> menus = menuRepository.findByIsAvailableTrue();
        return menus.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 식당별 메뉴 조회
     */
    @Transactional(readOnly = true)
    public List<MenuDto> getMenusByRestaurant(String restaurantName) {
        List<Menu> menus = menuRepository.findByRestaurantNameAndIsAvailableTrue(restaurantName);
        return menus.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 페이지 정보 조회 (전체 메뉴 + 인기 메뉴 3개)
     */
    @Transactional(readOnly = true)
    public MenuListResponseDto getMenuPage(String userId) {
        // 전체 메뉴
        List<MenuDto> allMenus = getAllMenus();

        // 사용자별 인기 메뉴 3개 (주문 횟수 기반)
        List<MenuDto> popularMenus = getPopularMenus(userId, 3);

        return MenuListResponseDto.builder()
                .allMenus(allMenus)
                .popularMenus(popularMenus)
                .build();
    }

    /**
     * 사용자별 인기 메뉴 조회 (주문 횟수 기반)
     */
    @Transactional(readOnly = true)
    public List<MenuDto> getPopularMenus(String userId, int limit) {
        List<OrderHistory> orderHistories = orderHistoryRepository.findTop3ByUserIdOrderByOrderCountDesc(userId);
        return orderHistories.stream()
                .limit(limit)
                .map(history -> {
                    Menu menu = menuRepository.findById(history.getMenuId())
                            .orElse(null);
                    if (menu != null) {
                        MenuDto dto = convertToDto(menu);
                        dto.setOrderCount(history.getOrderCount());
                        return dto;
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 ID로 조회
     */
    @Transactional(readOnly = true)
    public MenuDto getMenuById(String menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다."));
        return convertToDto(menu);
    }

    /**
     * Menu 엔티티를 MenuDto로 변환
     */
    private MenuDto convertToDto(Menu menu) {
        // 평균 평점 계산
        List<MenuRating> ratings = menuRatingRepository.findByMenuId(menu.getId());
        double averageRating = ratings.stream()
                .mapToInt(MenuRating::getRating)
                .average()
                .orElse(0.0);

        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .restaurantName(menu.getRestaurantName())
                .imageUrl(menu.getImageUrl())
                .isAvailable(menu.getIsAvailable())
                .availableDate(menu.getAvailableDate())
                .nutritionInfo(menu.getNutritionInfo())
                .allergyIngredients(menu.getAllergyIngredients())
                .averageRating(averageRating)
                .build();
    }
}

