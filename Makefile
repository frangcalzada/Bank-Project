SHELL := /bin/bash
BACKEND := backend

.PHONY: auth core wait-auth up up-bg down

auth:
	@echo ">> Iniciando AUTH..."
	cd $(BACKEND)/auth && ./mvnw spring-boot:run

core:
	@echo ">> Iniciando CORE..."
	cd $(BACKEND)/core && ./mvnw spring-boot:run

wait-auth:
	@echo ">> Esperando a AUTH en http://localhost:9000 ..."
	@until curl -fsS http://localhost:9000/.well-known/openid-configuration >/dev/null 2>&1; do \
    	printf "."; sleep 1; \
	done; echo " OK"

up:
	( cd $(BACKEND)/auth && ./mvnw spring-boot:run ) & \
	$(MAKE) wait-auth && \
	( cd $(BACKEND)/core && ./mvnw spring-boot:run )

down:
	-@lsof -ti :9000 | xargs kill -9 2>/dev/null || true
	-@lsof -ti :8080 | xargs kill -9 2>/dev/null || true
	@echo ">> Servicios detenidos (9000/8080)"