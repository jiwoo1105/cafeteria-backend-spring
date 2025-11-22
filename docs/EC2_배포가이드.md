# EC2 배포 가이드

## 1. EC2 인스턴스 준비

### 인스턴스 생성
- Amazon Linux 2023 또는 Ubuntu 22.04 LTS
- t2.micro (무료 티어) 또는 t3.small
- 보안 그룹: SSH(22), HTTP(80), HTTPS(443), 애플리케이션 포트(8080)

---

## 2. MongoDB 설정 (3가지 옵션)

### 옵션 A: MongoDB Atlas (추천 ⭐)

**장점:**
- 설정 간단
- 무료 티어 제공
- 자동 백업
- EC2와 분리되어 안정적

**설정:**
1. MongoDB Atlas 가입: https://www.mongodb.com/cloud/atlas
2. Free 클러스터 생성 (M0 - 512MB)
3. Network Access에서 EC2 IP 추가 (또는 0.0.0.0/0 - 개발용)
4. Database User 생성
5. Connection String 복사

**환경 변수 설정:**
```bash
# EC2에서 실행
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/easython?retryWrites=true&w=majority"
```

---

### 옵션 B: EC2에 MongoDB 직접 설치

**설치 (Ubuntu):**
```bash
# 1. MongoDB 설치
wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org

# 2. MongoDB 시작
sudo systemctl start mongod
sudo systemctl enable mongod

# 3. 상태 확인
sudo systemctl status mongod
```

**보안 그룹:**
- 포트 27017은 EC2 내부 통신용으로만 사용 (외부 노출 불필요)

**환경 변수:**
```bash
export MONGODB_URI="mongodb://localhost:27017/easython"
```

---

### 옵션 C: Docker로 MongoDB 실행

**설치:**
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
  mongo:latest
```

**환경 변수:**
```bash
export MONGODB_URI="mongodb://localhost:27017/easython"
```

---

## 3. Spring Boot 애플리케이션 배포

### 방법 1: JAR 파일 배포 (간단)

**로컬에서 빌드:**
```bash
./gradlew clean build
# build/libs/easython-0.0.1-SNAPSHOT.jar 생성
```

**EC2로 전송:**
```bash
# SCP로 전송
scp -i your-key.pem build/libs/easython-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/home/ec2-user/
```

**EC2에서 실행:**
```bash
# Java 설치 확인 (없으면 설치)
java -version

# 환경 변수 설정
export MONGODB_URI="your-mongodb-connection-string"
export SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
java -jar easython-0.0.1-SNAPSHOT.jar

# 백그라운드 실행 (nohup)
nohup java -jar easython-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

---

### 방법 2: systemd 서비스로 등록 (권장)

**서비스 파일 생성:**
```bash
sudo nano /etc/systemd/system/easython.service
```

**내용:**
```ini
[Unit]
Description=Easython Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
Environment="MONGODB_URI=mongodb://localhost:27017/easython"
Environment="SPRING_PROFILES_ACTIVE=prod"
ExecStart=/usr/bin/java -jar /home/ec2-user/easython-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**서비스 시작:**
```bash
sudo systemctl daemon-reload
sudo systemctl start easython
sudo systemctl enable easython
sudo systemctl status easython
```

---

## 4. Nginx 리버스 프록시 (선택)

**Nginx 설치:**
```bash
sudo apt-get install -y nginx
```

**설정 파일:**
```bash
sudo nano /etc/nginx/sites-available/easython
```

**내용:**
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**Nginx 활성화:**
```bash
sudo ln -s /etc/nginx/sites-available/easython /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 5. 배포 체크리스트

- [ ] MongoDB 연결 확인
- [ ] EC2 보안 그룹 설정 확인
- [ ] 환경 변수 설정
- [ ] 애플리케이션 실행 확인
- [ ] API 테스트
- [ ] 로그 확인

---

## 6. 추천 구성

**프로덕션:**
- MongoDB: MongoDB Atlas (무료 티어)
- 애플리케이션: EC2에서 JAR 실행
- 프로세스 관리: systemd 서비스
- 리버스 프록시: Nginx (도메인 연결 시)

**장점:**
- MongoDB Atlas: 자동 백업, 모니터링, 확장성
- EC2: 애플리케이션 실행만 집중
- systemd: 자동 재시작, 로그 관리

