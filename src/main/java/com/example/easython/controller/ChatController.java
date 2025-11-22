package com.example.easython.controller;

import com.example.easython.dto.ApiResponse;
import com.example.easython.dto.ChatMessageDto;
import com.example.easython.dto.ChatRequestDto;
import com.example.easython.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅 메시지 전송
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ChatMessageDto>> sendMessage(
            @PathVariable String userId,
            @Valid @RequestBody ChatRequestDto request) {
        try {
            ChatMessageDto response = chatService.sendMessage(userId, request);
            return ResponseEntity.ok(ApiResponse.success("메시지 전송 성공", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 채팅 이력 조회 (세션별)
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessageDto> messages = chatService.getChatHistory(sessionId);
        return ResponseEntity.ok(ApiResponse.success("채팅 이력 조회 성공", messages));
    }

    /**
     * 사용자별 채팅 이력 조회
     */
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getUserChatHistory(@PathVariable String userId) {
        List<ChatMessageDto> messages = chatService.getUserChatHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 채팅 이력 조회 성공", messages));
    }
}

