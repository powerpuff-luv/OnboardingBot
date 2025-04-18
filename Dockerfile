# --- СТАДИЯ СБОРКИ ---
FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Предзагрузка зависимостей
RUN ./gradlew dependencies --no-daemon || true

COPY ./src ./src

# Сборка jar-файла
RUN ./gradlew clean bootJar --no-daemon


# --- СТАДИЯ ЗАПУСКА ---
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
