package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.UserDto;
import com.example.easython.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable String userId) {
        try {
            UserDto user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자 생성 또는 업데이트
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createOrUpdateUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto savedUser = userService.createOrUpdateUser(userDto);
            return ResponseEntity.ok(ApiResponse.success("사용자 저장 성공", savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자 알레르기 정보 업데이트
     */
    @PutMapping("/{userId}/allergies")
    public ResponseEntity<ApiResponse<UserDto>> updateUserAllergies(
            @PathVariable String userId,
            @RequestBody List<String> allergyIngredients) {
        try {
            UserDto updatedUser = userService.updateUserAllergies(userId, allergyIngredients);
            return ResponseEntity.ok(ApiResponse.success("알레르기 정보 업데이트 성공", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

