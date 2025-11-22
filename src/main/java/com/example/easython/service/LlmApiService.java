package com.example.easython.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.easython.domain.ChatMessage;
import com.example.easython.domain.User;
import com.example.easython.dto.MenuDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * LLM API 서비스
 * OpenAI API, Claude API, 또는 다른 LLM API를 호출합니다.
 * 실제 사용할 LLM API에 맞게 구현하세요.
 */
@Service
@Slf4j
public class LlmApiService {

    @Value("${llm.api.url:}")
    private String llmApiUrl;

    @Value("${llm.api.key:}")
    private String llmApiKey;

    private final RestTemplate restTemplate;

    public LlmApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * LLM API 호출하여 응답 생성
     * 
     * 실제 사용할 LLM API에 맞게 수정하세요.
     * 예: OpenAI GPT, Anthropic Claude, Google Gemini 등
     * 
     * @param userMessage 사용자 메시지
     * @param previousMessages 이전 대화 이력
     * @param userAllergies 사용자 알레르기 유발성분 목록
     * @param userNutritionGoal 사용자 영양 목표/선호도
     * @param allMenus 전체 메뉴 정보 (영양성분, 알레르기 유발성분 포함)
     */
    public String getChatResponse(String userMessage, List<ChatMessage> previousMessages, 
                                   List<String> userAllergies, User.NutritionGoal userNutritionGoal,
                                   List<MenuDto> allMenus) {
        try {
            // LLM 팀 API URL 설정이 없으면 모의 응답 반환
            if (llmApiUrl == null || llmApiUrl.isEmpty()) {
                log.warn("LLM API URL이 설정되지 않아 모의 응답을 반환합니다. application.properties에 llm.api.url을 설정하세요.");
                return generateMockResponse(userMessage, previousMessages, userAllergies, userNutritionGoal, allMenus);
            }

            // LLM 팀 API로 전송할 쿼리 생성
            Map<String, Object> query = buildLlmQuery(userMessage, previousMessages, userAllergies, userNutritionGoal, allMenus);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 요청 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(query, headers);
            
            // 요청 쿼리를 JSON 형식으로 로깅
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String queryJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(query);
                log.info("LLM API 요청 쿼리:\n{}", queryJson);
            } catch (Exception e) {
                log.warn("쿼리 JSON 변환 실패", e);
                log.debug("쿼리: {}", query);
            }
            
            // LLM 팀 API 호출
            String apiUrl = llmApiUrl.endsWith("/") ? llmApiUrl + "chat" : llmApiUrl + "/chat";
            log.debug("LLM 팀 API 호출: {}", apiUrl);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            // 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            
            // 응답 본문을 JSON 형식으로 로깅
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String responseJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseBody);
                log.info("LLM API 응답:\n{}", responseJson);
            } catch (Exception e) {
                log.debug("응답 JSON 변환 실패", e);
                log.debug("응답 본문: {}", responseBody);
            }

            // LLM 팀에서 "message" 필드로 응답을 내려줌
            if (responseBody != null && responseBody.containsKey("message")) {
                String llmResponse = (String) responseBody.get("message");
                log.debug("LLM 팀 API 응답 성공");
                return llmResponse;
            }

            // 기존 형식도 지원 (하위 호환성)
            if (responseBody != null && responseBody.containsKey("success") && Boolean.TRUE.equals(responseBody.get("success"))) {
                if (responseBody.containsKey("data")) {
                    String llmResponse = (String) responseBody.get("data");
                    log.debug("LLM 팀 API 응답 성공 (기존 형식)");
                    return llmResponse;
                }
            }

            // 응답 형식이 예상과 다르면 모의 응답 반환
            log.warn("LLM 팀 API 응답 형식이 예상과 다릅니다. 모의 응답을 반환합니다. 응답 본문: {}", responseBody);
            return generateMockResponse(userMessage, previousMessages, userAllergies, userNutritionGoal, allMenus);

        } catch (Exception e) {
            log.error("LLM 팀 API 호출 실패", e);
            // API 호출 실패 시 모의 응답 반환
            return generateMockResponse(userMessage, previousMessages, userAllergies, userNutritionGoal, allMenus);
        }
    }

    /**
     * LLM 팀 API로 전송할 쿼리 생성
     * 사용자 메시지, 이전 대화 이력, 사용자 정보, 메뉴 정보를 포함
     */
    private Map<String, Object> buildLlmQuery(String userMessage, List<ChatMessage> previousMessages,
                                              List<String> userAllergies, User.NutritionGoal userNutritionGoal,
                                              List<MenuDto> allMenus) {
        Map<String, Object> query = new HashMap<>();
        
        // 사용자 메시지 (FastAPI 서버가 "message" 필드를 기대함)
        query.put("message", userMessage);

        // 이전 대화 이력
        if (previousMessages != null && !previousMessages.isEmpty()) {
            List<Map<String, String>> previousMessagesList = new ArrayList<>();
            for (ChatMessage msg : previousMessages) {
                Map<String, String> message = new HashMap<>();
                message.put("role", msg.getRole() == ChatMessage.MessageRole.USER ? "USER" : "ASSISTANT");
                message.put("content", msg.getRole() == ChatMessage.MessageRole.USER ? 
                    msg.getMessage() : (msg.getResponse() != null ? msg.getResponse() : ""));
                previousMessagesList.add(message);
            }
            query.put("previousMessages", previousMessagesList);
        }

        // 사용자 정보
        Map<String, Object> userInfo = new HashMap<>();
        if (userAllergies != null && !userAllergies.isEmpty()) {
            userInfo.put("allergies", userAllergies);
        }
        if (userNutritionGoal != null) {
            Map<String, Object> nutritionGoal = new HashMap<>();
            if (userNutritionGoal.getDailyCalorieGoal() != null) {
                nutritionGoal.put("dailyCalorieGoal", userNutritionGoal.getDailyCalorieGoal());
            }
            if (userNutritionGoal.getMinProteinGoal() != null) {
                nutritionGoal.put("minProteinGoal", userNutritionGoal.getMinProteinGoal());
            }
            if (userNutritionGoal.getMaxCalorieGoal() != null) {
                nutritionGoal.put("maxCalorieGoal", userNutritionGoal.getMaxCalorieGoal());
            }
            if (userNutritionGoal.getIsDietMode() != null) {
                nutritionGoal.put("isDietMode", userNutritionGoal.getIsDietMode());
            }
            if (userNutritionGoal.getIsHighProteinMode() != null) {
                nutritionGoal.put("isHighProteinMode", userNutritionGoal.getIsHighProteinMode());
            }
            if (!nutritionGoal.isEmpty()) {
                userInfo.put("nutritionGoal", nutritionGoal);
            }
        }
        if (!userInfo.isEmpty()) {
            query.put("userInfo", userInfo);
        }

        // 메뉴 정보
        if (allMenus != null && !allMenus.isEmpty()) {
            List<Map<String, Object>> menusList = new ArrayList<>();
            for (MenuDto menu : allMenus) {
                if (menu != null) {
                    Map<String, Object> menuMap = new HashMap<>();
                    menuMap.put("id", menu.getId());
                    menuMap.put("name", menu.getName());
                    menuMap.put("restaurantName", menu.getRestaurantName());
                    menuMap.put("price", menu.getPrice());
                    
                    // 영양성분 정보
                    if (menu.getNutritionInfo() != null) {
                        Map<String, Object> nutritionInfo = new HashMap<>();
                        if (menu.getNutritionInfo().getCalories() != null) {
                            nutritionInfo.put("calories", menu.getNutritionInfo().getCalories());
                        }
                        if (menu.getNutritionInfo().getProtein() != null) {
                            nutritionInfo.put("protein", menu.getNutritionInfo().getProtein());
                        }
                        if (menu.getNutritionInfo().getCarbs() != null) {
                            nutritionInfo.put("carbs", menu.getNutritionInfo().getCarbs());
                        }
                        if (menu.getNutritionInfo().getFat() != null) {
                            nutritionInfo.put("fat", menu.getNutritionInfo().getFat());
                        }
                        if (menu.getNutritionInfo().getSugar() != null) {
                            nutritionInfo.put("sugar", menu.getNutritionInfo().getSugar());
                        }
                        if (menu.getNutritionInfo().getSodium() != null) {
                            nutritionInfo.put("sodium", menu.getNutritionInfo().getSodium());
                        }
                        if (menu.getNutritionInfo().getFiber() != null) {
                            nutritionInfo.put("fiber", menu.getNutritionInfo().getFiber());
                        }
                        if (menu.getNutritionInfo().getCholesterol() != null) {
                            nutritionInfo.put("cholesterol", menu.getNutritionInfo().getCholesterol());
                        }
                        if (!nutritionInfo.isEmpty()) {
                            menuMap.put("nutritionInfo", nutritionInfo);
                        }
                    }
                    
                    // 알레르기 유발성분
                    if (menu.getAllergyIngredients() != null && !menu.getAllergyIngredients().isEmpty()) {
                        menuMap.put("allergyIngredients", menu.getAllergyIngredients());
                    }
                    
                    menusList.add(menuMap);
                }
            }
            if (!menusList.isEmpty()) {
                query.put("menus", menusList);
            }
        }

        return query;
    }

    /**
     * 시스템 프롬프트 생성
     * 사용자 정보, 메뉴 정보를 포함하여 LLM이 맥락을 이해할 수 있도록 함
     */
    private String buildSystemPrompt(List<String> userAllergies, User.NutritionGoal userNutritionGoal, 
                                     List<MenuDto> allMenus) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("당신은 대학 학생식당 주문 도우미 AI입니다. 사용자에게 친절하고 도움이 되는 답변을 제공해주세요.\n\n");

        // 사용자 알레르기 정보
        if (userAllergies != null && !userAllergies.isEmpty()) {
            prompt.append("## 사용자 알레르기 정보\n");
            prompt.append("사용자는 다음 알레르기 유발성분에 알레르기가 있습니다: ");
            prompt.append(String.join(", ", userAllergies));
            prompt.append("\n메뉴를 추천할 때는 반드시 이 알레르기 성분을 확인하고, 포함된 메뉴는 추천하지 말거나 주의하도록 안내해주세요.\n\n");
        }

        // 사용자 영양 목표
        if (userNutritionGoal != null) {
            prompt.append("## 사용자 영양 목표\n");
            if (userNutritionGoal.getDailyCalorieGoal() != null) {
                prompt.append(String.format("- 일일 칼로리 목표: %dkcal 이하\n", userNutritionGoal.getDailyCalorieGoal()));
            }
            if (userNutritionGoal.getMaxCalorieGoal() != null) {
                prompt.append(String.format("- 최대 칼로리 제한: %dkcal 이하\n", userNutritionGoal.getMaxCalorieGoal().intValue()));
            }
            if (userNutritionGoal.getMinProteinGoal() != null) {
                prompt.append(String.format("- 최소 단백질 목표: %.1fg 이상\n", userNutritionGoal.getMinProteinGoal()));
            }
            if (Boolean.TRUE.equals(userNutritionGoal.getIsDietMode())) {
                prompt.append("- 다이어트 모드 활성화: 칼로리가 낮은 메뉴를 우선 추천해주세요.\n");
            }
            if (Boolean.TRUE.equals(userNutritionGoal.getIsHighProteinMode())) {
                prompt.append("- 고단백 모드 활성화: 단백질이 많은 메뉴를 우선 추천해주세요.\n");
            }
            prompt.append("\n");
        }

        // 메뉴 정보
        if (allMenus != null && !allMenus.isEmpty()) {
            prompt.append("## 사용 가능한 메뉴 정보\n");
            for (MenuDto menu : allMenus) {
                if (menu != null && menu.getName() != null) {
                    prompt.append(String.format("- %s (%s): %d원", menu.getName(), menu.getRestaurantName(), menu.getPrice().intValue()));
                    
                    // 영양성분 정보
                    if (menu.getNutritionInfo() != null) {
                        prompt.append(String.format(", 칼로리: %dkcal, 단백질: %.1fg", 
                                menu.getNutritionInfo().getCalories() != null ? menu.getNutritionInfo().getCalories() : 0,
                                menu.getNutritionInfo().getProtein() != null ? menu.getNutritionInfo().getProtein() : 0));
                    }
                    
                    // 알레르기 정보
                    if (menu.getAllergyIngredients() != null && !menu.getAllergyIngredients().isEmpty()) {
                        prompt.append(String.format(", 알레르기 유발성분: %s", String.join(", ", menu.getAllergyIngredients())));
                    }
                    
                    prompt.append("\n");
                }
            }
            prompt.append("\n");
        }

        prompt.append("## 주의사항\n");
        prompt.append("- 사용자의 알레르기 정보와 영양 목표를 항상 고려하여 답변해주세요.\n");
        prompt.append("- 메뉴를 추천할 때는 구체적인 정보(칼로리, 단백질, 알레르기 성분 등)를 포함해주세요.\n");
        prompt.append("- 친절하고 자연스러운 한국어로 답변해주세요.\n");
        prompt.append("- 사용자가 질문한 내용에 직접적으로 답변하고, 필요시 메뉴 추천도 해주세요.\n");

        return prompt.toString();
    }

    /**
     * 임시 모의 응답 생성 (실제 LLM API 연동 전까지 사용)
     */
    private String generateMockResponse(String userMessage, List<ChatMessage> previousMessages,
                                         List<String> userAllergies, User.NutritionGoal userNutritionGoal,
                                         List<MenuDto> allMenus) {
        // 간단한 키워드 기반 응답 (실제 LLM API 연동 전까지)
        String lowerMessage = userMessage.toLowerCase();
        
        // 알레르기 관련 질문 처리
        if (lowerMessage.contains("알레르기") || lowerMessage.contains("알러지")) {
            if (userAllergies != null && !userAllergies.isEmpty()) {
                // 특정 메뉴에 대한 알레르기 확인
                if (lowerMessage.contains("메뉴") || lowerMessage.contains("이거") || lowerMessage.contains("이것")) {
                    // 메뉴 이름 추출 시도 (간단한 예시)
                    if (allMenus != null) {
                        for (MenuDto menu : allMenus) {
                            if (menu != null && menu.getName() != null && lowerMessage.contains(menu.getName().toLowerCase())) {
                                List<String> menuAllergies = menu.getAllergyIngredients();
                                if (menuAllergies != null) {
                                    List<String> matchingAllergies = new ArrayList<>();
                                    for (String allergy : userAllergies) {
                                        if (menuAllergies.contains(allergy)) {
                                            matchingAllergies.add(allergy);
                                        }
                                    }
                                    if (!matchingAllergies.isEmpty()) {
                                        return String.format("네, %s 메뉴에는 당신의 알레르기 유발성분인 '%s'이(가) 포함되어 있습니다. 주문 시 주의하시기 바랍니다.", 
                                                menu.getName(), String.join(", ", matchingAllergies));
                                    } else {
                                        return String.format("네, %s 메뉴에는 당신의 알레르기 유발성분이 포함되어 있지 않습니다. 안전하게 주문하실 수 있습니다.", menu.getName());
                                    }
                                }
                            }
                        }
                    }
                    return "메뉴 이름을 정확히 알려주시면 알레르기 유발성분을 확인해드릴 수 있습니다.";
                } else {
                    return String.format("당신의 알레르기 유발성분은 다음과 같습니다: %s. 메뉴 선택 시 이 성분들을 확인해드릴 수 있습니다.", 
                            String.join(", ", userAllergies));
                }
            } else {
                return "등록된 알레르기 유발성분이 없습니다. 알레르기 정보를 등록하시면 메뉴 선택 시 안내를 받으실 수 있습니다.";
            }
        }
        
        // 영양성분 관련 질문 처리 (다이어트, 단백질, 칼로리 등)
        if (lowerMessage.contains("다이어트") || lowerMessage.contains("칼로리") || lowerMessage.contains("단백질") || 
            lowerMessage.contains("영양성분") || lowerMessage.contains("영양")) {
            StringBuilder response = new StringBuilder();
            
            // 사용자 영양 목표 확인
            boolean hasCalorieGoal = userNutritionGoal != null && userNutritionGoal.getDailyCalorieGoal() != null;
            boolean hasProteinGoal = userNutritionGoal != null && userNutritionGoal.getMinProteinGoal() != null;
            boolean isDietMode = userNutritionGoal != null && Boolean.TRUE.equals(userNutritionGoal.getIsDietMode());
            boolean isHighProteinMode = userNutritionGoal != null && Boolean.TRUE.equals(userNutritionGoal.getIsHighProteinMode());
            
            if (lowerMessage.contains("칼로리") || lowerMessage.contains("다이어트") || isDietMode || hasCalorieGoal) {
                Integer calorieLimit = hasCalorieGoal ? userNutritionGoal.getDailyCalorieGoal() : null;
                
                if (allMenus != null && !allMenus.isEmpty()) {
                    List<MenuDto> filteredMenus = allMenus.stream()
                            .filter(m -> m.getNutritionInfo() != null && m.getNutritionInfo().getCalories() != null)
                            .filter(m -> calorieLimit == null || m.getNutritionInfo().getCalories() <= calorieLimit)
                            .sorted((m1, m2) -> Integer.compare(
                                    m1.getNutritionInfo().getCalories(), 
                                    m2.getNutritionInfo().getCalories()))
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                    
                    if (!filteredMenus.isEmpty()) {
                        MenuDto lowCalorieMenu = filteredMenus.get(0);
                        if (calorieLimit != null) {
                            response.append(String.format("당신의 일일 칼로리 목표(%dkcal 이하)를 고려하면, %s (%dkcal)을 추천드립니다. ", 
                                    calorieLimit, lowCalorieMenu.getName(), lowCalorieMenu.getNutritionInfo().getCalories()));
                        } else {
                            response.append(String.format("칼로리가 가장 낮은 메뉴는 %s (%dkcal)입니다. ", 
                                    lowCalorieMenu.getName(), lowCalorieMenu.getNutritionInfo().getCalories()));
                        }
                        
                        if (filteredMenus.size() > 1 && filteredMenus.size() <= 3) {
                            response.append("다른 추천 메뉴: ");
                            for (int i = 1; i < filteredMenus.size(); i++) {
                                MenuDto menu = filteredMenus.get(i);
                                response.append(String.format("%s (%dkcal)", menu.getName(), menu.getNutritionInfo().getCalories()));
                                if (i < filteredMenus.size() - 1) response.append(", ");
                            }
                            response.append(". ");
                        }
                    } else if (calorieLimit != null) {
                        response.append(String.format("죄송합니다. %dkcal 이하인 메뉴를 찾을 수 없습니다. ", calorieLimit));
                    }
                }
            }
            
            if (lowerMessage.contains("단백질") || isHighProteinMode || hasProteinGoal) {
                java.math.BigDecimal proteinLimit = hasProteinGoal ? userNutritionGoal.getMinProteinGoal() : null;
                
                if (allMenus != null && !allMenus.isEmpty()) {
                    List<MenuDto> filteredMenus = allMenus.stream()
                            .filter(m -> m.getNutritionInfo() != null && m.getNutritionInfo().getProtein() != null)
                            .filter(m -> proteinLimit == null || m.getNutritionInfo().getProtein().compareTo(proteinLimit) >= 0)
                            .sorted((m1, m2) -> m2.getNutritionInfo().getProtein().compareTo(m1.getNutritionInfo().getProtein()))
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                    
                    if (!filteredMenus.isEmpty()) {
                        MenuDto highProteinMenu = filteredMenus.get(0);
                        if (proteinLimit != null) {
                            response.append(String.format("당신의 단백질 목표(%.1fg 이상)를 고려하면, %s (%.1fg)을 추천드립니다. ", 
                                    proteinLimit, highProteinMenu.getName(), highProteinMenu.getNutritionInfo().getProtein()));
                        } else {
                            response.append(String.format("단백질이 가장 많은 메뉴는 %s (%.1fg)입니다. ", 
                                    highProteinMenu.getName(), highProteinMenu.getNutritionInfo().getProtein()));
                        }
                        
                        if (filteredMenus.size() > 1 && filteredMenus.size() <= 3) {
                            response.append("다른 추천 메뉴: ");
                            for (int i = 1; i < filteredMenus.size(); i++) {
                                MenuDto menu = filteredMenus.get(i);
                                response.append(String.format("%s (%.1fg)", menu.getName(), menu.getNutritionInfo().getProtein()));
                                if (i < filteredMenus.size() - 1) response.append(", ");
                            }
                            response.append(". ");
                        }
                    } else if (proteinLimit != null) {
                        response.append(String.format("죄송합니다. %.1fg 이상의 단백질을 포함한 메뉴를 찾을 수 없습니다. ", proteinLimit));
                    }
                }
            }
            
            if (response.length() == 0) {
                response.append("영양성분 정보를 기반으로 메뉴를 추천해드릴 수 있습니다. 칼로리 목표나 단백질 목표를 설정하시면 더 정확한 추천을 받으실 수 있습니다.");
            }
            
            return response.toString();
        }
        
        // 일반적인 질문 처리
        if (lowerMessage.contains("안녕") || lowerMessage.contains("hello")) {
            return "안녕하세요! 학식 주문 도우미입니다. 무엇을 도와드릴까요?";
        } else if (lowerMessage.contains("메뉴") || lowerMessage.contains("뭐")) {
            return "오늘의 메뉴를 확인하고 싶으시면 메뉴 화면을 확인해주세요. 특정 메뉴에 대한 정보나 추천이 필요하시면 말씀해주세요!";
        } else if (lowerMessage.contains("추천")) {
            return "인기 메뉴나 과거 주문 이력을 기반으로 메뉴를 추천해드릴 수 있습니다. 어떤 종류의 음식을 좋아하시나요?";
        } else if (lowerMessage.contains("주문") || lowerMessage.contains("시켜")) {
            return "주문하시려면 메뉴를 선택하고 장바구니에 담은 후 결제하시면 됩니다. 주문 과정에서 도움이 필요하시면 말씀해주세요!";
        } else if (lowerMessage.contains("비용") || lowerMessage.contains("가격") || lowerMessage.contains("얼마")) {
            return "각 메뉴의 가격은 메뉴 화면에서 확인하실 수 있습니다. 장바구니에 담으시면 총 금액도 확인하실 수 있어요!";
        } else {
            return "학식 주문과 관련된 질문을 도와드릴 수 있습니다. 메뉴 추천, 주문 방법, 가격 정보, 알레르기 확인, 영양성분 정보 등 무엇이든 물어보세요!";
        }
    }
}

