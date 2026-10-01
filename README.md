# Inventory Management API

A Spring Boot REST API for managing products and recording inventory movements. The project explores Java backend development with Spring MVC, Spring Data JPA, JWT authentication, role-based authorization, MySQL, and Kafka.

## Features

- Create, view, update, and delete products.
- Record inventory receipts, sales, returns, damage, and manual adjustments.
- Apply inventory movements asynchronously through Kafka and persist transaction history with JPA.
- Register users, authenticate with JWTs, and protect endpoints by role.
- Run the application and its MySQL and Kafka services with Docker Compose.

## Technology

- Java 17 and Spring Boot
- Spring Web MVC, Spring Security, Spring Data JPA
- JWT bearer authentication and BCrypt password hashing
- MySQL and Apache Kafka
- Maven, Docker, and Docker Compose
- H2 available as a runtime dependency for local or test configurations

## Run locally

### Prerequisites

- Java 17
- Docker with the Docker Compose plugin (for the full stack)

The application expects MySQL and Kafka. The simplest way to start all three services is from the application directory:

```bash
cd "demo 2"
docker compose up --build
```

The API listens on `http://localhost:8080`. MySQL is exposed on port `3306` and Kafka on port `9092`. Stop the services with `Ctrl+C`; remove the containers and the persisted database volume with `docker compose down -v`.

### Configuration

The defaults in `src/main/resources/application.yaml` and `docker-compose.yml` are for local development. Configure these environment variables when running outside that setup:

| Variable | Purpose | Local default |
| --- | --- | --- |
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `inventorydb` |
| `DB_USERNAME` | Database user | `java_user` |
| `DB_PASSWORD` | Database password | `temp_pass` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `localhost:9092` |
| `JWT_SECRET` | HMAC secret used to sign JWTs | Development-only fallback in YAML |

Set a private, sufficiently long `JWT_SECRET` and strong database credentials outside local development. The application currently uses Hibernate schema auto-update (`ddl-auto: update`); use a migration tool and an intentional schema strategy before treating this as a production deployment.

To run through Maven instead, start MySQL and Kafka separately, then run:

```bash
cd "demo 2"
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

## Authentication and roles

Register a user and log in to receive a JWT:

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"change-this-password"}'

curl -X POST http://localhost:8080/api/users/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"change-this-password"}'
```

Login returns the token as a response string. Send it on protected requests as a bearer token:

```bash
curl http://localhost:8080/products \
  -H 'Authorization: Bearer YOUR_JWT'
```

New accounts receive the `ASSOCIATE` role. Product creation, update, deletion, direct on-hand adjustment, and manual inventory adjustment require `MANAGER` or `ADMIN`. Role updates require `ADMIN`. Registration is open in the current configuration, so this example is intended for local exploration.

## API overview

All request and response bodies use JSON. Protected endpoints require a JWT.

### Products

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/products` | Authenticated | List products |
| `GET` | `/products/{sku}` | Authenticated | Get a product |
| `POST` | `/products` | Manager or admin | Create a product |
| `PUT` | `/products/{sku}` | Manager or admin | Update a product |
| `DELETE` | `/products/{sku}` | Manager or admin | Delete a product |
| `PUT` | `/products/update-on-hand/{sku}?newOnHand={quantity}` | Manager or admin | Set the on-hand quantity directly |

Product example:

```json
{
  "sku": 1001,
  "name": "Desk lamp",
  "price": 29.99,
  "onHand": 12
}
```

### Inventory transactions

The transaction routes take a SKU in the path and a quantity in the JSON body. The route determines the transaction type and whether the quantity is added or subtracted. `timestamp` is optional; when omitted, the current date is used. The date format is `MM/dd/yyyy`.

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/transactions/inventory/{sku}/receive` | Authenticated | Receive stock |
| `POST` | `/transactions/inventory/{sku}/sell` | Authenticated | Sell stock |
| `POST` | `/transactions/inventory/{sku}/return` | Authenticated | Return stock |
| `POST` | `/transactions/inventory/{sku}/damage` | Authenticated | Record damaged stock |
| `POST` | `/transactions/inventory/{sku}/adjust` | Manager or admin | Apply a manual adjustment |
| `GET` | `/transactions/history` | Authenticated | Transactions from the last seven days |
| `GET` | `/transactions/history/{sku}` | Authenticated | Look up transaction history by SKU |
| `GET` | `/transactions/negative-history` | Authenticated | Transactions with a negative quantity |

Example request to receive five units:

```bash
curl -X POST http://localhost:8080/transactions/inventory/1001/receive \
  -H 'Authorization: Bearer YOUR_JWT' \
  -H 'Content-Type: application/json' \
  -d '{"quantity":5}'
```

## Current development notes

This is a learning and portfolio project, and some behavior is still being developed. Kafka applies inventory movements asynchronously. Idempotent processing and additional inventory validation (such as rejecting sales that exceed stock) are planned improvements; avoid relying on the current transaction endpoints for production inventory accounting. The transaction repository query now uses the entity's `productSku` field for SKU lookups.

## Project layout

```text
demo 2/
  src/main/java/inventorymanagement/demo/
    Products/       Product API, service, repository, and entity
    Transactions/   Transaction API, Kafka producer/consumer, and persistence
    Users/          Registration, login, and user persistence
  src/main/resources/application.yaml
  src/test/java/    Unit and MVC/security tests
```
