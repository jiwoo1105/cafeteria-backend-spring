package com.example.easython.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    @NotBlank(message = "메시지는 필수입니다")
    private String message;
    private String sessionId; // 세션 ID (선택사항, 없으면 자동 생성)
}

