Vue + Spring Boot 기반의 프론트/백엔드 프로젝트  
Docker Compose로 실행 가능



## 📦 프로젝트 개요

이 프로젝트는 Docker를 활용하여 백엔드(Spring Boot)와 프론트엔드(Vue.js with Vite)를 컨테이너 환경에서 실행할 수 있도록 구성되었습니다.



## ✅ 주요 기술 스택

|역영|기술|
|:---|:---|
|프론트엔드|Node.js 18, Vue 3, Vite, TypeScript|
|백엔드|Spring Boot, Java 17, Gradle|
|인프라|Docker, Docker Compose|



## 📁 프로젝트 구조

```
root/
├── backend/ # Spring Boot 프로젝트
│ ├── Dockerfile
│ └── ...
├── frontend/ # Vue + Vite 프로젝트
│ ├── Dockerfile
│ └── ...
├── docker-compose.yml # 전체 서비스 구성
└── README.md
```



## 🐳 실행 방법 (Docker)

1. 로컬 개발 환경에서 실행
```bash
docker-compose up --build
```

* 백엔드: http://localhost:8080
* 프론트엔드: http://localhost:5173



## ⚙️ Docker 구성 설명

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew build
CMD ["java", "-jar", "build/libs/your-backend.jar"]
```

🧪 frontend/Dockerfile
```dockerfile
FROM node:18
WORKDIR /app
COPY . .
RUN npm install && npm run build
RUN npm install -g serve
CMD ["serve", "-s", "dist"]
```

🧩 docker-compose.yml
```yaml
version: "3.8"
services:
backend:
build: ./backend
ports:
- "8080:8080"

frontend:
build: ./frontend
ports:
- "5173:3000"
depends_on:
- backend
```
