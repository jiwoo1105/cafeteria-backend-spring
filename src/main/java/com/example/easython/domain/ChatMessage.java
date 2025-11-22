package com.example.easython.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private String userId; // 사용자 ID
    private String sessionId; // 채팅 세션 ID (사용자별 또는 테이블별)
    private String message; // 사용자 메시지
    private String response; // LLM 응답
    private MessageRole role; // 메시지 역할
    private LocalDateTime createdAt;

    public enum MessageRole {
        USER,    // 사용자
        ASSISTANT // LLM 어시스턴트
    }
}

