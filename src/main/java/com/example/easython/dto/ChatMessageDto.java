package com.example.easython.dto;

import com.example.easython.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String id;
    private String userId;
    private String sessionId;
    private String message;
    private String response;
    private ChatMessage.MessageRole role;
    private LocalDateTime createdAt;
}

