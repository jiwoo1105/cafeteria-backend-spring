package com.example.easython.service;

import com.example.easython.domain.User;
import com.example.easython.dto.UserDto;
import com.example.easython.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return convertToDto(user);
    }

    /**
     * 사용자 생성 또는 업데이트
     */
    @Transactional
    public UserDto createOrUpdateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .map(existingUser -> {
                    // 기존 사용자 업데이트
                    existingUser.setName(userDto.getName());
                    existingUser.setAllergyIngredients(userDto.getAllergyIngredients());
                    existingUser.setNutritionGoal(userDto.getNutritionGoal());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 새 사용자 생성
                    return User.builder()
                            .id(userDto.getId())
                            .name(userDto.getName())
                            .allergyIngredients(userDto.getAllergyIngredients())
                            .nutritionGoal(userDto.getNutritionGoal())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                });

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 사용자 알레르기 정보 업데이트
     */
    @Transactional
    public UserDto updateUserAllergies(String userId, java.util.List<String> allergyIngredients) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        user.setAllergyIngredients(allergyIngredients);
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 사용자 영양 목표 업데이트
     */
    @Transactional
    public UserDto updateUserNutritionGoal(String userId, User.NutritionGoal nutritionGoal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        user.setNutritionGoal(nutritionGoal);
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * User 엔티티를 UserDto로 변환
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .allergyIngredients(user.getAllergyIngredients())
                .nutritionGoal(user.getNutritionGoal())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

