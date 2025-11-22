# Docker를 사용한 EC2 배포 가이드

## 목차
1. [GitHub에 코드 푸시](#1-github에-코드-푸시)
2. [EC2 서버 접속 및 환경 설정](#2-ec2-서버-접속-및-환경-설정)
3. [Docker 설치](#3-docker-설치)
4. [애플리케이션 배포](#4-애플리케이션-배포)
5. [MongoDB 연결 설정](#5-mongodb-연결-설정)
6. [컨테이너 관리](#6-컨테이너-관리)

---

## 1. GitHub에 코드 푸시

### 1.1 로컬에서 변경사항 커밋 및 푸시

```bash
# 현재 상태 확인
git status

# Dockerfile과 .dockerignore 추가
git add Dockerfile .dockerignore

# 커밋
git commit -m "Add Docker configuration for EC2 deployment"

# GitHub에 푸시
git push origin main
```

> **참고**: 브랜치 이름이 `main`이 아닌 `master`일 수 있습니다. `git branch` 명령어로 확인하세요.

---

## 2. EC2 서버 접속 및 환경 설정

### 2.1 EC2 서버 접속

```bash
# SSH로 EC2 접속 (로컬 터미널에서 실행)
ssh -i "your-key.pem" ubuntu@ec2-52-79-242-205.ap-northeast-2.compute.amazonaws.com
```

### 2.2 시스템 업데이트

```bash
# 패키지 목록 업데이트
sudo apt update

# 설치된 패키지 업그레이드
sudo apt upgrade -y
```

---

## 3. Docker 설치

### 3.1 Docker 설치

```bash
# 필요한 패키지 설치
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Docker GPG 키 추가
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Docker 저장소 추가
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Docker 설치
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Docker 서비스 시작 및 활성화
sudo systemctl start docker
sudo systemctl enable docker

# 현재 사용자를 docker 그룹에 추가 (sudo 없이 docker 명령 사용)
sudo usermod -aG docker $USER

# 변경사항 적용 (재로그인 또는 아래 명령 실행)
newgrp docker

# Docker 설치 확인
docker --version
```

### 3.2 Docker Compose 설치 (선택사항)

```bash
# Docker Compose 설치
sudo apt install -y docker-compose-plugin

# 설치 확인
docker compose version
```

---

## 4. 애플리케이션 배포

### 4.1 Git 설치 및 저장소 클론

```bash
# Git 설치
sudo apt install -y git

# 프로젝트 디렉토리로 이동
cd ~

# GitHub에서 프로젝트 클론
git clone https://github.com/your-username/easython.git

# 프로젝트 디렉토리로 이동
cd easython
```

> **중요**: `https://github.com/your-username/easython.git`을 실제 GitHub 저장소 URL로 변경하세요.

### 4.2 Docker 이미지 빌드

```bash
# Docker 이미지 빌드
docker build -t easython-app .

# 빌드된 이미지 확인
docker images
```

### 4.3 Docker 컨테이너 실행

#### 옵션 1: 간단한 실행 (기본 설정)

```bash
# 컨테이너 실행
docker run -d \
  --name easython \
  -p 8080:8080 \
  easython-app

# 컨테이너 상태 확인
docker ps
```

#### 옵션 2: 환경 변수를 사용한 실행 (권장)

```bash
# MongoDB URI와 함께 실행
docker run -d \
  --name easython \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/easython \
  -e SERVER_PORT=8080 \
  easython-app
```

#### 옵션 3: 외부 MongoDB 사용 (EC2에 MongoDB가 이미 설치된 경우)

```bash
# 호스트 네트워크 모드로 실행 (localhost 접근 가능)
docker run -d \
  --name easython \
  --network host \
  -e SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/easython \
  easython-app
```

### 4.4 애플리케이션 로그 확인

```bash
# 실시간 로그 확인
docker logs -f easython

# 최근 100줄 로그 확인
docker logs --tail 100 easython
```

---

## 5. MongoDB 연결 설정

### 5.1 EC2에 MongoDB가 설치되어 있는 경우

MongoDB가 이미 EC2에 설치되어 있다면, 위의 **옵션 3**을 사용하여 `--network host` 옵션으로 컨테이너를 실행하세요.

### 5.2 Docker로 MongoDB 실행

MongoDB를 Docker 컨테이너로 실행하려면:

```bash
# MongoDB 컨테이너 실행
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  mongo:7.0

# 네트워크 생성
docker network create easython-network

# MongoDB를 네트워크에 연결
docker network connect easython-network mongodb

# Spring Boot 앱 컨테이너 중지 및 삭제
docker stop easython
docker rm easython

# Spring Boot 앱을 같은 네트워크에서 실행
docker run -d \
  --name easython \
  --network easython-network \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/easython \
  easython-app
```

---

## 6. 컨테이너 관리

### 6.1 기본 Docker 명령어

```bash
# 실행 중인 컨테이너 확인
docker ps

# 모든 컨테이너 확인 (중지된 것 포함)
docker ps -a

# 컨테이너 중지
docker stop easython

# 컨테이너 시작
docker start easython

# 컨테이너 재시작
docker restart easython

# 컨테이너 삭제
docker rm easython

# 강제 삭제 (실행 중인 컨테이너)
docker rm -f easython

# 이미지 삭제
docker rmi easython-app
```

### 6.2 애플리케이션 업데이트

```bash
# 프로젝트 디렉토리로 이동
cd ~/easython

# 최신 코드 가져오기
git pull origin main

# 기존 컨테이너 중지 및 삭제
docker stop easython
docker rm easython

# 새 이미지 빌드
docker build -t easython-app .

# 새 컨테이너 실행
docker run -d \
  --name easython \
  -p 8080:8080 \
  easython-app

# 로그 확인
docker logs -f easython
```

### 6.3 디스크 공간 정리

```bash
# 사용하지 않는 이미지, 컨테이너, 볼륨 삭제
docker system prune -a

# 특정 이미지 삭제
docker rmi $(docker images -f "dangling=true" -q)
```

---

## 7. 보안 그룹 설정

EC2 인스턴스의 보안 그룹에서 다음 포트를 열어야 합니다:

1. AWS 콘솔에서 EC2 인스턴스 선택
2. 보안 그룹 클릭
3. 인바운드 규칙 편집
4. 규칙 추가:
   - **포트 8080**: Spring Boot 애플리케이션
   - **포트 22**: SSH 접속 (이미 열려있어야 함)

---

## 8. 애플리케이션 접속 테스트

```bash
# EC2 내부에서 테스트
curl http://localhost:8080

# 외부에서 테스트 (로컬 터미널)
curl http://ec2-52-79-242-205.ap-northeast-2.compute.amazonaws.com:8080
```

---

## 9. 트러블슈팅

### 9.1 컨테이너가 실행되지 않는 경우

```bash
# 컨테이너 로그 확인
docker logs easython

# 컨테이너 상세 정보 확인
docker inspect easython
```

### 9.2 MongoDB 연결 오류

```bash
# MongoDB 컨테이너가 실행 중인지 확인
docker ps | grep mongodb

# MongoDB 로그 확인
docker logs mongodb

# 네트워크 연결 확인
docker network inspect easython-network
```

### 9.3 포트가 이미 사용 중인 경우

```bash
# 8080 포트를 사용하는 프로세스 확인
sudo lsof -i :8080

# 프로세스 종료
sudo kill -9 <PID>
```

---

## 10. 자동 재시작 설정

서버 재부팅 시 자동으로 컨테이너가 시작되도록 설정:

```bash
# 컨테이너를 재시작 정책과 함께 실행
docker run -d \
  --name easython \
  --restart unless-stopped \
  -p 8080:8080 \
  easython-app

# 또는 기존 컨테이너에 재시작 정책 업데이트
docker update --restart unless-stopped easython
```

---

## 요약

1. **로컬**: Dockerfile 생성 → GitHub 푸시
2. **EC2**: Docker 설치 → Git 클론 → 이미지 빌드 → 컨테이너 실행
3. **확인**: 로그 및 접속 테스트
4. **업데이트**: Git pull → 이미지 재빌드 → 컨테이너 재시작

**EC2 주소**: `ec2-52-79-242-205.ap-northeast-2.compute.amazonaws.com:8080`
