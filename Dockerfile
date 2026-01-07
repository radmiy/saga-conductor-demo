# СТАДИЯ 1: СБОРКА (BUILD STAGE)
# Этот образ используется только для компиляции и сборки проекта
FROM  eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Копируем файлы конфигурации Gradle (ускоряет кэширование слоев Docker)
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY gradle gradle

# Предварительная загрузка зависимостей
RUN ./gradlew dependencies || return 0

# Копируем исходный код
COPY src src

# Сборка проекта
RUN ./gradlew clean build -x test

# СТАДИЯ 2: ЗАПУСК (RUN STAGE)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Создание пользования с минимальными правами
RUN useradd --system --create-home --uid 1001 appuser
USER appuser

# Выполнение команд от пользования с минимальными правами
COPY --chown=appuser:appuser --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
