package com.example.easython.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId; // 사용자 ID
    private String orderId; // 주문 ID
    private String title; // 알림 제목
    private String message; // 알림 메시지
    private NotificationType type; // 알림 타입
    private Boolean isRead; // 읽음 여부
    private LocalDateTime createdAt;

    public enum NotificationType {
        ORDER_READY,    // 주문 준비 완료
        ORDER_COMPLETED, // 주문 완료
        MENU_AVAILABLE  // 메뉴 제공 시작
    }
}

