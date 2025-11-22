# EC2 MongoDB 설정 가이드

## 방법 1: MongoDB Atlas 사용 (가장 추천 ⭐)

### 장점
- 무료 티어 제공 (512MB 스토리지)
- 관리형 서비스 (백업, 모니터링 자동)
- 자동 스케일링
- EC2와 분리되어 안정적
- 설정이 간단

### 설정 방법

1. **MongoDB Atlas 가입**
   - https://www.mongodb.com/cloud/atlas 접속
   - 무료 계정 생성

2. **클러스터 생성**
   - Free 티어 선택 (M0)
   - AWS 리전 선택 (ap-northeast-2 - 서울)
   - 클러스터 이름 설정

3. **네트워크 접근 설정**
   - Network Access → Add IP Address
   - EC2 인스턴스 IP 추가 (또는 0.0.0.0/0으로 모든 IP 허용 - 개발용)

4. **데이터베이스 사용자 생성**
   - Database Access → Add New Database User
   - Username, Password 설정

5. **Connection String 확인**
   - Clusters → Connect → Connect your application
   - Connection String 복사
   ```
   mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```

6. **application.properties 수정**
   ```properties
   # MongoDB Atlas 설정
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/easython?retryWrites=true&w=majority
   ```

---

## 방법 2: EC2에 MongoDB 직접 설치

### 장점
- 완전한 제어
- 비용 절감 (EC2 비용만)

### 단점
- 설치 및 설정 복잡
- 백업/모니터링 직접 관리
- EC2 리소스 사용

### 설치 방법 (Ubuntu/Debian)

```bash
# 1. MongoDB 공개 키 가져오기
wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | sudo apt-key add -

# 2. MongoDB 리포지토리 추가
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# 3. 패키지 목록 업데이트
sudo apt-get update

# 4. MongoDB 설치
sudo apt-get install -y mongodb-org

# 5. MongoDB 서비스 시작
sudo systemctl start mongod
sudo systemctl enable mongod

# 6. MongoDB 상태 확인
sudo systemctl status mongod
```

### EC2 보안 그룹 설정
- 포트 27017 인바운드 규칙 추가 (EC2 내부 통신용)
- 또는 SSH만 열어두고 EC2 내부에서만 접근

### application.properties
```properties
# EC2 내부 MongoDB (같은 인스턴스)
spring.data.mongodb.uri=mongodb://localhost:27017/easython

# EC2 내부 MongoDB (다른 인스턴스)
spring.data.mongodb.uri=mongodb://<private-ip>:27017/easython
```

---

## 방법 3: Docker로 MongoDB 실행 (EC2)

### 장점
- 설치 간편
- 버전 관리 쉬움
- 컨테이너화로 격리

### 설정 방법

```bash
# 1. Docker 설치
sudo apt-get update
sudo apt-get install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker

# 2. MongoDB 컨테이너 실행
docker run -d \
  --name mongodb \
  --restart=always \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=your_password \
  mongo:latest

# 3. MongoDB 연결 확인
docker exec -it mongodb mongosh
```

### application.properties
```properties
# Docker MongoDB
spring.data.mongodb.uri=mongodb://admin:your_password@localhost:27017/easython?authSource=admin
```

---

## 배포 시 주의사항

### 1. 환경 변수 사용 (보안)
```properties
# application.properties
spring.data.mongodb.uri=${MONGODB_URI}
```

EC2에서 환경 변수 설정:
```bash
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/easython"
```

### 2. Profile 분리
- `application-dev.properties` (로컬 개발)
- `application-prod.properties` (EC2 프로덕션)

### 3. 자동 백업 (MongoDB Atlas 자동, 직접 설치 시)
```bash
# 크론탭으로 주기적 백업
0 2 * * * mongodump --out=/backup/mongodb/$(date +\%Y\%m\%d)
```

---

## 추천 방식

**개발/프로덕션: MongoDB Atlas 사용**
- 설정이 간단
- 무료 티어로 시작 가능
- 자동 백업 및 모니터링
- EC2와 분리되어 안정적

**로컬 개발: Docker 또는 로컬 MongoDB**
- 빠른 개발 환경 구축

