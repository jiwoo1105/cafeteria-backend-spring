package com.example.easython.service;

import com.example.easython.domain.Notification;
import com.example.easython.dto.NotificationDto;
import com.example.easython.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 사용자별 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUserId(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public NotificationDto markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        notification.setIsRead(true);
        Notification savedNotification = notificationRepository.save(notification);

        return convertToDto(savedNotification);
    }

    /**
     * 메뉴 제공 시작 알림 생성 (관리자용)
     */
    @Transactional
    public NotificationDto createMenuAvailableNotification(String userId, String menuId, String menuName) {
        Notification notification = Notification.builder()
                .userId(userId)
                .orderId(null)
                .title("메뉴 제공 시작")
                .message(menuName + " 메뉴가 제공되었습니다.")
                .type(Notification.NotificationType.MENU_AVAILABLE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDto(savedNotification);
    }

    /**
     * Notification 엔티티를 NotificationDto로 변환
     */
    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .orderId(notification.getOrderId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

