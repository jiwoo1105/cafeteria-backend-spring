# Windows MongoDB 설치 가이드 (Docker 없이)

## 방법 1: MongoDB Community Server 설치 (추천 ⭐)

### 1단계: 다운로드

1. **MongoDB Community Server 다운로드**
   - https://www.mongodb.com/try/download/community 접속
   - Version: 7.0 (또는 최신)
   - Platform: Windows
   - Package: MSI
   - 다운로드 시작

### 2단계: 설치

1. **MSI 파일 실행**
   - 다운로드한 `.msi` 파일 더블 클릭

2. **설치 옵션 선택**
   - **Complete** 설치 선택 (전체 설치)
   - **Install MongoDB as a Service** ✅ 체크 (중요!)
     - 이 옵션을 체크하면 Windows 서비스로 자동 실행
   - **Install MongoDB Compass** ✅ 체크 (GUI 도구)
   - Service Name: MongoDB (기본값)
   - Port: 27017 (기본값)

3. **설치 완료**
   - 설치 완료 후 MongoDB가 자동으로 시작됨

### 3단계: 확인

**Windows 서비스 확인:**
1. `Win + R` 키 누르기
2. `services.msc` 입력
3. "MongoDB" 서비스 찾기
4. 상태가 "실행 중"인지 확인

**또는 PowerShell에서:**
```powershell
Get-Service -Name MongoDB*
```

**MongoDB Compass로 확인:**
1. MongoDB Compass 실행
2. 연결 문자열: `mongodb://localhost:27017`
3. "Connect" 클릭
4. 연결 성공 시 설치 완료!

---

## 방법 2: MongoDB 수동 실행 (설치 후)

만약 서비스로 설치하지 않았다면:

```powershell
# MongoDB 설치 경로로 이동 (기본 경로)
cd "C:\Program Files\MongoDB\Server\7.0\bin"

# MongoDB 실행
.\mongod.exe --dbpath="C:\data\db"
```

**데이터 디렉토리 생성:**
```powershell
# 데이터 디렉토리 생성 (관리자 권한 필요)
mkdir C:\data\db
```

---

## 방법 3: MongoDB Compass로만 시작

**MongoDB Compass 실행:**
1. MongoDB Compass 실행
2. 연결 문자열: `mongodb://localhost:27017`
3. "Connect" 클릭

**참고:** 
- MongoDB Community Server가 설치되어 있어야 함
- Compass는 GUI 도구일 뿐, 서버 자체는 아님

---

## 설치 확인

**PowerShell에서:**
```powershell
# MongoDB 버전 확인
mongod --version

# MongoDB 쉘 접속
mongosh
```

**또는 브라우저에서:**
- http://localhost:27017 접속
- "It looks like you are trying to access MongoDB over HTTP" 메시지가 나오면 정상!

---

## 문제 해결

### 포트 27017이 이미 사용 중
```
Port 27017 is already in use
```

**해결:**
- 다른 MongoDB 인스턴스가 실행 중일 수 있음
- Windows 서비스에서 MongoDB 중지 후 재시작

### 서비스가 시작되지 않음
**해결:**
1. Windows 서비스에서 MongoDB 서비스 찾기
2. 우클릭 → 속성 → 시작 유형: 자동
3. 서비스 시작

### MongoDB Compass 연결 실패
**해결:**
1. MongoDB 서비스가 실행 중인지 확인
2. 포트 27017이 열려있는지 확인
3. 방화벽 설정 확인

---

## 다음 단계

MongoDB 설치 및 실행 확인 후:

1. ✅ MongoDB Compass로 연결 확인
2. ✅ Spring Boot 애플리케이션 실행
3. ✅ 초기 데이터 자동 생성 확인
4. ✅ API 테스트

---

## 빠른 시작

1. **MongoDB Community Server 다운로드 및 설치**
   - https://www.mongodb.com/try/download/community
   - "Install MongoDB as a Service" 체크

2. **MongoDB Compass 실행**
   - 연결 문자열: `mongodb://localhost:27017`
   - Connect 클릭

3. **Spring Boot 애플리케이션 실행**
   - IDE에서 `EasythonApplication.java` 실행
   - 또는 `.\gradlew bootRun`

4. **확인**
   - http://localhost:8080/api/tables 접속
   - MongoDB Compass에서 `easython` 데이터베이스 확인

