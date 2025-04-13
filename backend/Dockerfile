# 1. OpenJDK 이미지 가져오기
FROM openjdk:17-jdk-slim

# 2. 애플리케이션 JAR 파일을 컨테이너에 복사
COPY target/demo-0.0.1-SNAPSHOT.jar demo.jar

# 3. 애플리케이션 포트 설정 (기본적으로 8080 사용)
EXPOSE 8080

# 4. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/demo.jar"]
