# DonaTrack — Donation Platform

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=flat-square&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

[English](README.md) | [Español](README.es.md)

> Academic capstone project for **Diseño de Sistemas de Información**, UTN FRBA, 2026.
> Built with **5 colleagues**.

## What is DonaTrack?

DonaTrack connects **donors**, **beneficiary organizations** and **logistics** in one platform. Donations are registered, segmented by category and subcategory, matched with needs, assigned to delivery routes, and tracked through notifications and incentives.

## Architecture

```mermaid
flowchart TB
    FE[React frontend] --> AUTH[Authentication service]
    FE --> DON[Donations service]
    FE --> INC[Incentives service]
    FE --> LOG[Logistics service]

    AUTH --> DB[(MySQL)]
    DON --> DB
    INC --> DB
    LOG --> DB

    DON --> MQ[(RabbitMQ)]
    INC --> MQ
    LOG --> MQ
    MQ --> NOT[Notification service]
    NOT --> EMAIL[Email / n8n]
```

### Services

| Module | Responsibility |
|---|---|
| `autenticacion-service` | Users, roles and authentication |
| `donaciones-service` | Donors, donations, beneficiary entities and needs |
| `incentivos-service` | Missions, badges, rankings and impact metrics |
| `logistica-service` | Routes, deliveries and proof of delivery |
| `notificacion-service` | Email and event-driven notifications |
| `common-lib` | Shared domain, DTOs and infrastructure |
| `front-end-app` | Thymeleaf/React-facing web app |

## Key flows

- **Donation intake:** one load is segmented into independent donations by subcategory, expiration and condition.
- **Needs matching:** beneficiary organizations publish recurring and extraordinary needs.
- **Monthly ranking:** a scheduled job builds the donor ranking at 03:00 on the first day of each month.
- **Impact panel:** donors see their history, helped organizations and ranking position.

![Donor use cases](docs/casosDeUso/casosUsoPersonaDonante.png)
![Beneficiary entity use cases](docs/casosDeUso/casosUsoEntidadBeneficiaria.png)
![Monthly ranking](openspec/changes/ranking-mensual-por-misiones/diagrams/ranking-programado.png)
![Impact panel](openspec/changes/panel-impacto-donante/diagrams/impacto-donante.png)

## My role

Full-stack developer in a 6-person team. I worked across the incentives and donations services and the frontend:

- Monthly **incentive ranking** and on-demand ranking endpoints.
- Beneficiary entity views with filters and monthly impact counters.
- DTO mapping between domain entities and API responses.
- JPA/Hibernate persistence and repository migration.
- Docker Compose setup and zero-intervention local startup.
- Frontend flows for donations, entities and incentives.

## Testing

- **43 tests** across the services.
- Domain, service and integration coverage for donations, incentives, logistics and notifications.

## Spec-driven development

- **12 OpenSpec changes** documenting proposals, design and tasks.
- Specs and acceptance criteria are written before implementation.

## Stack

`Java 21` · `Spring Boot` · `Spring Cloud` · `Spring Data JPA` · `Hibernate` · `MySQL` · `RabbitMQ` · `Docker Compose` · `Maven` · `JUnit`

## Running locally

```bash
cp .env.example .env
docker compose up --build
```

The main services are exposed on:

| Service | Port |
|---|---:|
| Donations API | 8080 |
| Incentives API | 8081 |
| Logistics API | 8083 |
| RabbitMQ management | 15672 |
| MySQL | 3307 |

## Project structure

```text
├── autenticacion-service/
├── common-lib/
├── donaciones-service/
├── incentivos-service/
├── logistica-service/
├── notificacion-service/
├── front-end-app/
├── docker/
├── docs/
├── openspec/
└── docker-compose.yml
```

## Notes

- Code published for portfolio purposes.
- No credentials are committed; configure them through `.env`.
- Full academic documentation is available under `docs/` and `openspec/`.
