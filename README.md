# MovieNight

Веб-приложение для организации совместных киновечеров: создание мероприятий, номинирование фильмов, голосование и отзывы.

## Запуск

### Вариант 1 — Docker Compose (рекомендуется)

```bash
docker-compose up --build
```

Приложение поднимет PostgreSQL, Redis и само приложение. После старта открыть: `http://localhost:8080`

### Вариант 2 — локально

Требования: JDK 17+, PostgreSQL на порту **5440** (БД: `movienight`, user: `movienight`, password: `movienight`), Redis на порту **6380**.

```bash
./mvnw spring-boot:run
```

## Учётные данные

| Роль  | Логин | Пароль |
|-------|-------|--------|
| Admin | admin | admin1 |

Обычные пользователи регистрируются через `/register`.

## Документация API

| Интерфейс | URL |
|-----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |