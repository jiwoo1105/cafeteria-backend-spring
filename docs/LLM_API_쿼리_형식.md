# LLM API 쿼리 형식 문서

## 개요
백엔드에서 LLM 팀의 API로 전송하는 쿼리 형식입니다.

---

## API 엔드포인트
```
POST {llm.api.url}/chat
Content-Type: application/json
```

---

## 요청 형식 (Request)

### Request Body
```json
{
  "userMessage": "사용자 메시지",
  "previousMessages": [
    {
      "role": "USER | ASSISTANT",
      "content": "메시지 내용"
    }
  ],
  "userInfo": {
    "userId": "user001",
    "allergies": ["돼지고기", "계란"],
    "nutritionGoal": {
      "dailyCalorieGoal": 2000,
      "minProteinGoal": 50.0,
      "maxCalorieGoal": 2500.0,
      "isDietMode": true,
      "isHighProteinMode": false
    }
  },
  "menus": [
    {
      "id": "menu1",
      "name": "김치찌개",
      "restaurantName": "가게 A",
      "price": 5000,
      "nutritionInfo": {
        "calories": 320,
        "protein": 18.5,
        "carbs": 25.0,
        "fat": 15.2,
        "sugar": 8.5,
        "sodium": 1200,
        "fiber": 3.2,
        "cholesterol": 45
      },
      "allergyIngredients": ["돼지고기", "밀가루"]
    }
  ]
}
```

### 필드 설명

#### 1. `userMessage` (필수)
- **타입**: String
- **설명**: 사용자가 입력한 메시지

#### 2. `previousMessages` (선택)
- **타입**: Array
- **설명**: 이전 대화 이력 (컨텍스트 유지용)
- **구조**:
  ```json
  {
    "role": "USER" | "ASSISTANT",
    "content": "메시지 내용"
  }
  ```

#### 3. `userInfo` (선택)
- **타입**: Object
- **설명**: 사용자 정보 (알레르기, 영양 목표 등)
- **구조**:
  ```json
  {
    "userId": "사용자 ID",
    "allergies": ["알레르기 유발성분 목록"],
    "nutritionGoal": {
      "dailyCalorieGoal": 일일 칼로리 목표 (Integer),
      "minProteinGoal": 최소 단백질 목표 (BigDecimal),
      "maxCalorieGoal": 최대 칼로리 목표 (BigDecimal),
      "isDietMode": 다이어트 모드 여부 (Boolean),
      "isHighProteinMode": 고단백 모드 여부 (Boolean)
    }
  }
  ```

#### 4. `menus` (선택)
- **타입**: Array
- **설명**: 사용 가능한 메뉴 정보 (영양성분, 알레르기 성분 포함)
- **구조**:
  ```json
  {
    "id": "메뉴 ID",
    "name": "메뉴 이름",
    "restaurantName": "가게 이름",
    "price": 가격 (BigDecimal),
    "nutritionInfo": {
      "calories": 칼로리 (Integer),
      "protein": 단백질 (BigDecimal),
      "carbs": 탄수화물 (BigDecimal),
      "fat": 지방 (BigDecimal),
      "sugar": 당 (BigDecimal),
      "sodium": 나트륨 (Integer),
      "fiber": 섬유질 (BigDecimal),
      "cholesterol": 콜레스테롤 (Integer)
    },
    "allergyIngredients": ["알레르기 유발성분 목록"]
  }
  ```

---

## 응답 형식 (Response)

### Success Response
```json
{
  "success": true,
  "message": "응답 메시지",
  "data": "LLM 응답 텍스트"
}
```

### Error Response
```json
{
  "success": false,
  "message": "에러 메시지",
  "error": "상세 에러 정보"
}
```

---

## 요청 예시

### 예시 1: 기본 쿼리
```json
{
  "userMessage": "오늘 다이어트인데 칼로리 낮은 메뉴 추천해줘",
  "userInfo": {
    "userId": "user001",
    "allergies": ["돼지고기", "계란"],
    "nutritionGoal": {
      "dailyCalorieGoal": 2000,
      "isDietMode": true
    }
  },
  "menus": [...]
}
```

### 예시 2: 대화 이력 포함
```json
{
  "userMessage": "그 메뉴에 알레르기 성분 있나요?",
  "previousMessages": [
    {
      "role": "USER",
      "content": "오늘 다이어트인데 칼로리 낮은 메뉴 추천해줘"
    },
    {
      "role": "ASSISTANT",
      "content": "칼로리가 가장 낮은 메뉴는 라면 (380kcal)입니다."
    }
  ],
  "userInfo": {...},
  "menus": [...]
}
```

---

## 구현 위치
- **서비스**: `com.example.easython.service.LlmApiService`
- **메서드**: `buildLlmQuery()`, `getChatResponse()`

---

## 참고사항
- LLM 팀의 API URL은 `application.properties`의 `llm.api.url`에 설정
- API 키는 LLM 팀이 관리하므로 백엔드에서는 전송하지 않음
- 모든 필드는 선택사항이며, 제공 가능한 정보만 포함

