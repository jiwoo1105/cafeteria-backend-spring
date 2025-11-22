# EC2 서버 실행 가이드

## 1단계: EC2에 연결

```bash
# SSH로 EC2 접속
ssh -i your-key.pem ec2-user@your-ec2-ip

# 또는 Ubuntu인 경우
ssh -i your-key.pem ubuntu@your-ec2-ip
```

---

## 2단계: Java 설치 확인 및 설치

### Java 설치 확인
```bash
java -version
```

### Java 17 설치 (없는 경우)

**Amazon Linux 2023:**
```bash
sudo yum install -y java-17-amazon-corretto-headless
```

**Ubuntu:**
```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk
```

**설치 확인:**
```bash
java -version
# 출력: openjdk version "17" ...
```

---

## 3단계: 애플리케이션 JAR 파일 준비

### 방법 A: 로컬에서 빌드 후 전송 (추천)

**로컬에서 빌드:**
```bash
# Windows PowerShell
.\gradlew.bat clean build -x test

# Linux/Mac
./gradlew clean build -x test
```

**EC2로 JAR 파일 전송:**
```bash
# 로컬에서 실행 (PowerShell 또는 Git Bash)
scp -i your-key.pem build/libs/easython-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/home/ec2-user/
```

**EC2에서 디렉토리 생성:**
```bash
mkdir -p /home/ec2-user/easython
mv /home/ec2-user/easython-0.0.1-SNAPSHOT.jar /home/ec2-user/easython/
```

### 방법 B: EC2에서 직접 빌드

```bash
# Git 설치
sudo yum install -y git  # Amazon Linux
# 또는
sudo apt-get install -y git  # Ubuntu

# 프로젝트 클론
git clone your-repository-url
cd easython

# Gradle Wrapper 실행 권한 부여
chmod +x gradlew

# 빌드
./gradlew clean build -x test
```

---

## 4단계: MongoDB 설정

### 옵션 1: MongoDB Atlas 사용 (가장 추천 ⭐)

1. MongoDB Atlas 가입: https://www.mongodb.com/cloud/atlas
2. Free 클러스터 생성
3. Network Access에서 EC2 IP 추가 (또는 0.0.0.0/0)
4. Database User 생성
5. Connection String 복사

**환경 변수로 설정:**
```bash
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/easython?retryWrites=true&w=majority"
```

### 옵션 2: EC2에 MongoDB 설치

**Ubuntu:**
```bash
wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org
sudo systemctl start mongod
sudo systemctl enable mongod
```

**환경 변수:**
```bash
export MONGODB_URI="mongodb://localhost:27017/easython"
```

---

## 5단계: 환경 변수 설정

EC2에서 환경 변수 파일 생성:
```bash
nano /home/ec2-user/easython/.env
```

**내용:**
```bash
# MongoDB 연결 문자열
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/easython?retryWrites=true&w=majority"

# 또는 로컬 MongoDB
# export MONGODB_URI="mongodb://localhost:27017/easython"

# Spring Profile
export SPRING_PROFILES_ACTIVE=prod

# LLM API URL (이미 application.properties에 있지만 재정의 가능)
export LLM_API_URL="http://ec2-52-79-242-205.ap-northeast-2.compute.amazonaws.com:8000"
```

**환경 변수 적용:**
```bash
source /home/ec2-user/easython/.env
```

---

## 6단계: 서버 실행

### 방법 1: 직접 실행 (테스트용)

```bash
cd /home/ec2-user/easython
source .env
java -jar easython-0.0.1-SNAPSHOT.jar
```

**백그라운드 실행:**
```bash
nohup java -jar easython-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

**로그 확인:**
```bash
tail -f app.log
```

**프로세스 확인:**
```bash
ps aux | grep java
```

**프로세스 종료:**
```bash
# PID 찾기
ps aux | grep java

# 종료 (PID는 실제 값으로 변경)
kill <PID>
```

---

### 방법 2: systemd 서비스로 실행 (프로덕션 권장 ⭐)

**서비스 파일 생성:**
```bash
sudo nano /etc/systemd/system/easython.service
```

**내용:**
```ini
[Unit]
Description=Easython Spring Boot Application
After=network.target mongodb.service

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/easython
EnvironmentFile=/home/ec2-user/easython/.env
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /home/ec2-user/easython/easython-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=append:/var/log/easython/app.log
StandardError=append:/var/log/easython/error.log

[Install]
WantedBy=multi-user.target
```

**로그 디렉토리 생성:**
```bash
sudo mkdir -p /var/log/easython
sudo chown ec2-user:ec2-user /var/log/easython
```

**서비스 시작:**
```bash
# systemd 재로드
sudo systemctl daemon-reload

# 서비스 시작
sudo systemctl start easython

# 부팅 시 자동 시작 설정
sudo systemctl enable easython

# 상태 확인
sudo systemctl status easython
```

**서비스 관리 명령어:**
```bash
# 시작
sudo systemctl start easython

# 중지
sudo systemctl stop easython

# 재시작
sudo systemctl restart easython

# 상태 확인
sudo systemctl status easython

# 로그 확인
sudo journalctl -u easython -f
# 또는
tail -f /var/log/easython/app.log
```

---

## 7단계: EC2 보안 그룹 설정

AWS 콘솔에서 보안 그룹 설정:

1. **인바운드 규칙 추가:**
   - 타입: `Custom TCP`
   - 포트: `8080`
   - 소스: `0.0.0.0/0` (또는 특정 IP)

2. **SSH 접근:**
   - 타입: `SSH`
   - 포트: `22`
   - 소스: `내 IP`

---

## 8단계: 서버 테스트

**서버 상태 확인:**
```bash
# EC2에서 실행
curl http://localhost:8080/actuator/health

# 또는 외부에서
curl http://your-ec2-ip:8080/api/menus
```

**브라우저에서 접속:**
```
http://your-ec2-ip:8080/api/menus
```

---

## 빠른 실행 스크립트

**간단 실행 스크립트 생성:**
```bash
nano /home/ec2-user/easython/start.sh
```

**내용:**
```bash
#!/bin/bash

cd /home/ec2-user/easython

# 환경 변수 로드
if [ -f .env ]; then
    source .env
fi

# 서버 실행
java -jar easython-0.0.1-SNAPSHOT.jar
```

**실행 권한 부여:**
```bash
chmod +x /home/ec2-user/easython/start.sh
```

**실행:**
```bash
./start.sh
```

---

## 문제 해결

### 포트가 이미 사용 중인 경우
```bash
# 포트 사용 확인
sudo netstat -tlnp | grep 8080

# 프로세스 종료
sudo kill <PID>
```

### 로그 확인
```bash
# 애플리케이션 로그
tail -f app.log

# systemd 로그
sudo journalctl -u easython -f

# 에러 로그
tail -f /var/log/easython/error.log
```

### Java 메모리 부족
```bash
# 메모리 확인
free -h

# JVM 메모리 옵션 조정 (Xmx 값 줄이기)
java -Xms128m -Xmx256m -jar easython-0.0.1-SNAPSHOT.jar
```

---

## 체크리스트

- [ ] Java 17 설치 확인
- [ ] JAR 파일 업로드 확인
- [ ] MongoDB 연결 설정
- [ ] 환경 변수 설정
- [ ] EC2 보안 그룹 설정 (포트 8080)
- [ ] 서버 실행 확인
- [ ] API 테스트 성공

---

## 추천 구성

**프로덕션 환경:**
- MongoDB: MongoDB Atlas (무료 티어)
- 서버 실행: systemd 서비스
- 로그: `/var/log/easython/`
- 자동 재시작: systemd 설정

**장점:**
- 서버 재부팅 시 자동 시작
- 프로세스 실패 시 자동 재시작
- 로그 관리 용이
- 안정적인 운영

