package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.NotificationDto;
import com.example.easython.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자별 알림 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotificationsByUserId(@PathVariable String userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("알림 조회 성공", notifications));
    }

    /**
     * 읽지 않은 알림 조회
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(@PathVariable String userId) {
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("읽지 않은 알림 조회 성공", notifications));
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable String notificationId) {
        try {
            NotificationDto notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(ApiResponse.success("알림 읽음 처리 성공", notification));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 메뉴 제공 시작 알림 생성 (관리자용 - Firebase 연동은 프론트엔드에서 처리)
     */
    @PostMapping("/menu-available/{userId}")
    public ResponseEntity<ApiResponse<NotificationDto>> createMenuAvailableNotification(
            @PathVariable String userId,
            @RequestParam String menuId,
            @RequestParam String menuName) {
        try {
            NotificationDto notification = notificationService.createMenuAvailableNotification(userId, menuId, menuName);
            return ResponseEntity.ok(ApiResponse.success("알림 생성 성공", notification));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

