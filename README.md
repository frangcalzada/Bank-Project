# Bank Project

Home banking educativo/realista.

## Módulos
- **backend/auth**: Spring Authorization Server (OIDC/OAuth2, JWT).
- **backend/core**: API REST (customers, accounts), Resource Server (JWT).
- **Infra**: Docker Compose (MySQL host 3307, Mongo 27017).
- **docs/postman**: Collection y Environment para pruebas.

## Cómo correr

# 1) Levantar Auth y Core
make up

# 2) (Opcional) Infra
docker compose -f Infra/compose.yaml up -d
