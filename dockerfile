# =========================
# Stage 1: Build ứng dụng
# =========================
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# Copy toàn bộ source code
COPY . .

# Cấp quyền cho gradlew
RUN chmod +x gradlew

# Build project (bỏ test cho nhanh)
RUN ./gradlew build -x test

# =========================
# Stage 2: Chạy ứng dụng
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/build/libs/*.jar app.jar

# Port mặc định Spring Boot
EXPOSE 8080

# Chạy app + bind đúng PORT của Render
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]