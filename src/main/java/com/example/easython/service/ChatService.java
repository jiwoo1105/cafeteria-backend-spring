package com.example.easython.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.easython.domain.ChatMessage;
import com.example.easython.domain.User;
import com.example.easython.dto.ChatMessageDto;
import com.example.easython.dto.ChatRequestDto;
import com.example.easython.dto.MenuDto;
import com.example.easython.repository.ChatMessageRepository;
import com.example.easython.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final LlmApiService llmApiService;
    private final UserRepository userRepository;
    private final MenuService menuService;

    /**
     * 채팅 메시지 전송 및 LLM 응답 받기
     */
    @Transactional
    public ChatMessageDto sendMessage(String userId, ChatRequestDto request) {
        // 세션 ID 생성 또는 기존 세션 사용
        String sessionId = request.getSessionId() != null && !request.getSessionId().isEmpty()
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        // 사용자 메시지 저장
        ChatMessage userMessage = ChatMessage.builder()
                .userId(userId)
                .sessionId(sessionId)
                .message(request.getMessage())
                .role(ChatMessage.MessageRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(userMessage);

        // 이전 대화 이력 조회 (컨텍스트용)
        List<ChatMessage> previousMessages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        // 사용자 정보 조회 (알레르기 정보, 영양 목표 포함)
        User user = userRepository.findById(userId).orElse(null);
        List<String> userAllergies = user != null ? user.getAllergyIngredients() : null;
        User.NutritionGoal userNutritionGoal = user != null ? user.getNutritionGoal() : null;

        // 전체 메뉴 정보 조회 (LLM이 메뉴 관련 질문에 답변할 수 있도록)
        List<MenuDto> allMenus = menuService.getAllMenus();

        // LLM API 호출하여 응답 생성
        String llmResponse = llmApiService.getChatResponse(
                request.getMessage(), 
                previousMessages, 
                userAllergies, 
                userNutritionGoal,
                allMenus
        );

        System.out.println("llmResponse: " + llmResponse);
        
        // LLM 응답 메시지 저장
        ChatMessage assistantMessage = ChatMessage.builder()
                .userId(userId)
                .sessionId(sessionId)
                .message(request.getMessage())
                .response(llmResponse)
                .role(ChatMessage.MessageRole.ASSISTANT)
                .createdAt(LocalDateTime.now())
                .build();
        ChatMessage savedMessage = chatMessageRepository.save(assistantMessage);

        return convertToDto(savedMessage);
    }

    /**
     * 채팅 이력 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getChatHistory(String sessionId) {
        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 채팅 이력 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getUserChatHistory(String userId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByCreatedAtAsc(userId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ChatMessage 엔티티를 ChatMessageDto로 변환
     */
    private ChatMessageDto convertToDto(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .userId(message.getUserId())
                .sessionId(message.getSessionId())
                .message(message.getMessage())
                .response(message.getResponse())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

