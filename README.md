# Saga Conductor Demo

Приложение для демонстрации паттерна **Saga** (оркестровая модель) с использованием **Netflix Conductor OSS**, **Java 21** и **Spring Boot 4.0+**.

## Описание проекта
Проект реализует цепочку распределенных транзакций для системы заказов. Каждая стадия (Оплата, Склад, Доставка) является отдельным воркером. Оркестратор Conductor управляет логикой выполнения и компенсации (отката) в случае сбоев.

## Технологический стек
- **Java 21**.
- **Netflix Conductor OSS**: Оркестрация рабочих процессов.
- **Netflix Conductor OSS Java SDK**: несколько ключевых модулей, которые помогают разработчикам легко создавать и управлять рабочими процессами.
- **MapStruct**: Маппинг сущностей в DTO/Records на этапе компиляции.
- **JPA Specifications**: Динамический поиск по сложным фильтрам (включая поиск внутри JSONB).

## Быстрый старт

### 1. Инфраструктура
Инфраструктура описанная в *docker-compose.yaml* включает в себя:
- **Conductor-server**
- **Conductor UI**
- **PostgreSql**
- **Saga-conductor-demo app** само приложение

Сборка с помощью Docker Compose:
```bash
docker-compose build
```

**Conductor UI: http://localhost:5000**

**PostgreSQL: localhost:5432 (user: postgres, pass: postgres)**

### 2. Запуск приложения
```bash
docker compose up -d
```


## Тестирование API
1. Создание новой Саги
   Для старта процесса оформления заказа:
```bash
curl -X POST http://localhost:8090/api/orders/start-saga \
    -H "Content-Type: application/json" \
    -d '{
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "items": ["Laptop", "Mouse"],
      "amount": 1500.00
    }'
```
2. Поиск и агрегация (UserInfo)

Приложение предоставляет эндпоинт для сложного поиска по всем сущностям сразу.
Пример запроса (динамические фильтры):
```bash
curl "http://localhost:8090/api/orders/search?status=FAILED&item=Macbook&minAmount=1000&page=0&size=10&sort=createdAt,desc"
```

Доступные параметры фильтрации:
- userId: UUID пользователя.
- status: Статус шага (PENDING, COMPLETED, FAILED).
- minAmount / maxAmount: Диапазон суммы оплаты.
- item: Поиск по названию товара внутри JSONB-массива в таблице Inventory.
- address: Поиск по адресу доставки.
