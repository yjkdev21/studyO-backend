FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 재료를 저장할 위치
COPY build/libs/tjspring-0.0.1-SNAPSHOT.jar /app/app.jar

# 재료를 통해 요리를 할 레시피 (JSON 배열 형식으로 변경)
ENTRYPOINT ["java", "-jar", "app.jar"]
# /app/app.jar 대신 상대 경로 app.jar를 사용해도 WORKDIR 때문에 정상 동작합니다.