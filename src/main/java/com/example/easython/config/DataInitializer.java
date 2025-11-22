package com.example.easython.config;

import com.example.easython.domain.Menu;
import com.example.easython.domain.Table;
import com.example.easython.domain.User;
import com.example.easython.repository.MenuRepository;
import com.example.easython.repository.TableRepository;
import com.example.easython.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 초기 데이터 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TableRepository tableRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (tableRepository.count() == 0) {
            log.info("초기 테이블 데이터 생성 중...");
            initializeTables();
        }

        if (menuRepository.count() == 0) {
            log.info("초기 메뉴 데이터 생성 중...");
            initializeMenus();
        }

        if (userRepository.count() == 0) {
            log.info("초기 사용자 데이터 생성 중...");
            initializeUsers();
        }
    }

    private void initializeTables() {
        // 가게 A 테이블
        for (int i = 1; i <= 5; i++) {
            Table table = Table.builder()
                    .tableNumber("A-" + i)
                    .capacity(4)
                    .restaurantName("가게 A")
                    .isAvailable(true)
                    .qrCode("QR_A_" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            tableRepository.save(table);
        }

        // 가게 B 테이블
        for (int i = 1; i <= 5; i++) {
            Table table = Table.builder()
                    .tableNumber("B-" + i)
                    .capacity(4)
                    .restaurantName("가게 B")
                    .isAvailable(true)
                    .qrCode("QR_B_" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            tableRepository.save(table);
        }

        // 가게 C 테이블
        for (int i = 1; i <= 5; i++) {
            Table table = Table.builder()
                    .tableNumber("C-" + i)
                    .capacity(4)
                    .restaurantName("가게 C")
                    .isAvailable(true)
                    .qrCode("QR_C_" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            tableRepository.save(table);
        }

        // 가게 D 테이블
        for (int i = 1; i <= 5; i++) {
            Table table = Table.builder()
                    .tableNumber("D-" + i)
                    .capacity(4)
                    .restaurantName("가게 D")
                    .isAvailable(true)
                    .qrCode("QR_D_" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            tableRepository.save(table);
        }

        log.info("초기 테이블 데이터 생성 완료 (총 {}개)", tableRepository.count());
    }

    private void initializeMenus() {
        // 가게 A 메뉴
        Menu menuA1 = Menu.builder()
                .name("김치찌개")
                .description("얼큰한 김치찌개")
                .price(BigDecimal.valueOf(5000))
                .restaurantName("가게 A")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(320)
                        .protein(BigDecimal.valueOf(18.5))
                        .carbs(BigDecimal.valueOf(25.0))
                        .fat(BigDecimal.valueOf(15.2))
                        .sugar(BigDecimal.valueOf(8.5))
                        .sodium(1200)
                        .fiber(BigDecimal.valueOf(3.2))
                        .cholesterol(45)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("돼지고기", "밀가루"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuA1);

        Menu menuA2 = Menu.builder()
                .name("된장찌개")
                .description("구수한 된장찌개")
                .price(BigDecimal.valueOf(4500))
                .restaurantName("가게 A")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(280)
                        .protein(BigDecimal.valueOf(15.0))
                        .carbs(BigDecimal.valueOf(22.5))
                        .fat(BigDecimal.valueOf(12.8))
                        .sugar(BigDecimal.valueOf(6.2))
                        .sodium(980)
                        .fiber(BigDecimal.valueOf(4.5))
                        .cholesterol(35)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("대두", "밀가루"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuA2);

        // 가게 B 메뉴
        Menu menuB1 = Menu.builder()
                .name("짜장면")
                .description("달콤한 짜장면")
                .price(BigDecimal.valueOf(4500))
                .restaurantName("가게 B")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(580)
                        .protein(BigDecimal.valueOf(22.5))
                        .carbs(BigDecimal.valueOf(85.0))
                        .fat(BigDecimal.valueOf(18.5))
                        .sugar(BigDecimal.valueOf(12.5))
                        .sodium(1450)
                        .fiber(BigDecimal.valueOf(2.8))
                        .cholesterol(55)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("밀가루", "대두", "돼지고기"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuB1);

        Menu menuB2 = Menu.builder()
                .name("짬뽕")
                .description("시원한 짬뽕")
                .price(BigDecimal.valueOf(5500))
                .restaurantName("가게 B")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(420)
                        .protein(BigDecimal.valueOf(28.5))
                        .carbs(BigDecimal.valueOf(45.0))
                        .fat(BigDecimal.valueOf(12.5))
                        .sugar(BigDecimal.valueOf(8.5))
                        .sodium(1850)
                        .fiber(BigDecimal.valueOf(3.5))
                        .cholesterol(85)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("밀가루", "새우", "게", "조개류", "돼지고기"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuB2);

        // 가게 C 메뉴
        Menu menuC1 = Menu.builder()
                .name("치킨버거")
                .description("바삭한 치킨버거")
                .price(BigDecimal.valueOf(6000))
                .restaurantName("가게 C")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(650)
                        .protein(BigDecimal.valueOf(35.5))
                        .carbs(BigDecimal.valueOf(52.0))
                        .fat(BigDecimal.valueOf(32.5))
                        .sugar(BigDecimal.valueOf(8.5))
                        .sodium(1280)
                        .fiber(BigDecimal.valueOf(2.5))
                        .cholesterol(75)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("밀가루", "계란", "우유", "유제품"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuC1);

        Menu menuC2 = Menu.builder()
                .name("햄버거")
                .description("클래식 햄버거")
                .price(BigDecimal.valueOf(5000))
                .restaurantName("가게 C")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(520)
                        .protein(BigDecimal.valueOf(28.0))
                        .carbs(BigDecimal.valueOf(48.5))
                        .fat(BigDecimal.valueOf(22.5))
                        .sugar(BigDecimal.valueOf(10.5))
                        .sodium(1150)
                        .fiber(BigDecimal.valueOf(2.2))
                        .cholesterol(65)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("밀가루", "우유", "유제품", "계란"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuC2);

        // 가게 D 메뉴
        Menu menuD1 = Menu.builder()
                .name("라면")
                .description("맛있는 라면")
                .price(BigDecimal.valueOf(3000))
                .restaurantName("가게 D")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(380)
                        .protein(BigDecimal.valueOf(12.5))
                        .carbs(BigDecimal.valueOf(62.0))
                        .fat(BigDecimal.valueOf(14.5))
                        .sugar(BigDecimal.valueOf(5.5))
                        .sodium(2100)
                        .fiber(BigDecimal.valueOf(1.8))
                        .cholesterol(0)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("밀가루", "대두"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuD1);

        Menu menuD2 = Menu.builder()
                .name("비빔밥")
                .description("건강한 비빔밥")
                .price(BigDecimal.valueOf(5500))
                .restaurantName("가게 D")
                .isAvailable(true)
                .availableDate(LocalDate.now())
                .nutritionInfo(Menu.NutritionInfo.builder()
                        .calories(450)
                        .protein(BigDecimal.valueOf(20.5))
                        .carbs(BigDecimal.valueOf(68.0))
                        .fat(BigDecimal.valueOf(12.5))
                        .sugar(BigDecimal.valueOf(15.5))
                        .sodium(950)
                        .fiber(BigDecimal.valueOf(8.5))
                        .cholesterol(25)
                        .build())
                .allergyIngredients(java.util.Arrays.asList("계란", "대두"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menuD2);

        log.info("초기 메뉴 데이터 생성 완료 (총 {}개)", menuRepository.count());
    }

    private void initializeUsers() {
        // 샘플 사용자 1: 다이어트 모드
        User user1 = User.builder()
                .id("user001")
                .name("홍길동")
                .allergyIngredients(Arrays.asList("돼지고기", "계란"))
                .nutritionGoal(User.NutritionGoal.builder()
                        .dailyCalorieGoal(2000)
                        .minProteinGoal(BigDecimal.valueOf(50))
                        .maxCalorieGoal(BigDecimal.valueOf(2500))
                        .isDietMode(true)
                        .isHighProteinMode(false)
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user1);

        // 샘플 사용자 2: 고단백 모드
        User user2 = User.builder()
                .id("user002")
                .name("김철수")
                .allergyIngredients(Arrays.asList("우유", "유제품", "견과류"))
                .nutritionGoal(User.NutritionGoal.builder()
                        .dailyCalorieGoal(3000)
                        .minProteinGoal(BigDecimal.valueOf(100))
                        .maxCalorieGoal(BigDecimal.valueOf(3500))
                        .isDietMode(false)
                        .isHighProteinMode(true)
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user2);

        // 샘플 사용자 3: 알레르기만 있는 사용자
        User user3 = User.builder()
                .id("user003")
                .name("이영희")
                .allergyIngredients(Arrays.asList("밀가루", "대두", "새우", "게", "조개류"))
                .nutritionGoal(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user3);

        // 샘플 사용자 4: 정보 없는 사용자
        User user4 = User.builder()
                .id("user004")
                .name("박민수")
                .allergyIngredients(null)
                .nutritionGoal(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user4);

        log.info("초기 사용자 데이터 생성 완료 (총 {}개)", userRepository.count());
    }
}

