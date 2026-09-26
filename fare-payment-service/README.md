\# Fare \& Payment Service (Member 4)



Owns fare estimation, final fare calculation, simulated payment processing, and receipts for RideLink.



\- \*\*Port:\*\* `8084`

\- \*\*Database:\*\* PostgreSQL, `faredb` (own schema, no cross-service DB access)

\- \*\*Auth:\*\* validates JWTs issued by Account Service (does not issue tokens itself)



\## Run locally



Prerequisites: Java 17, Maven (or use the bundled `mvnw`/`mvnw.cmd`), PostgreSQL 16+ running locally.



1\. Create the database:

```sql

&#x20;  CREATE DATABASE faredb;

```

2\. Set the required environment variables:

DB\_PASSWORD=<your postgres password>

JWT\_SECRET=<same secret agreed across all 4 services>



3\. Run:

mvn spring-boot:run

&#x20;  or `.\\mvnw.cmd spring-boot:run` on Windows.



Swagger UI: http://localhost:8084/swagger-ui.html

Raw OpenAPI spec: http://localhost:8084/v3/api-docs



\## Fare calculation rule

Estimate: baseFare (100) + distanceKm \* ratePerKm (50)

Final fare: baseFare (100) + distanceKm \* ratePerKm (50) + durationMin \* ratePerMinute (5)





\## Endpoints



| Method | Path                      | Auth required            | Description                                    |

|--------|---------------------------|---------------------------|------------------------------------------------|

| POST   | /api/fares/estimate       | No (public)                | Get a fare estimate for a pickup/destination   |

| POST   | /api/payments             | Yes — PASSENGER, DRIVER, ADMIN | Process a simulated payment for a completed ride |

| GET    | /api/payments/{id}        | Yes — PASSENGER, DRIVER, ADMIN | Retrieve a single payment by id                |

| GET    | /api/payments             | Yes — PASSENGER, DRIVER, ADMIN | List all payments                              |



`/api/fares/estimate` is intentionally public so a passenger can see a price before logging in or committing to a ride.



\## Simulated payment behaviour



\- `CASH` payments always succeed.

\- `CARD` payments have a genuine 10% random chance of failing (`status: FAILED`), to demonstrate the required negative payment scenario.

\- For a \*\*reliable, repeatable\*\* failure during demos, send `rideId: 999` with `paymentMethod: "CARD"` — this deterministically returns `status: FAILED` every time.



\## Negative scenarios demonstrated



\- Invalid fare estimate input (e.g. `distanceKm <= 0`) → `400 Bad Request` with field-level validation messages

\- Missing/invalid payment input → `400 Bad Request`

\- Payment not found → `404 Not Found`

\- Missing or invalid JWT on a protected endpoint → `403 Forbidden`

\- Simulated card payment failure → `201 Created` with `status: FAILED` in the response body



\## Interservice interaction



This service is called by \*\*Ride Management Service\*\* via synchronous REST:



\- `POST /api/payments`, triggered when a ride's status changes to `COMPLETED`. Ride Management sends `rideId`, `distanceKm`, `durationMin`, and `paymentMethod`, along with the passenger's JWT (forwarded from their original request) so this service can authenticate the call.



\## Tests

mvn test





Covers `FareService` (fare formula, zero/negative distance validation) and `PaymentService` (successful payment, deterministic card failure, missing/invalid input, payment-not-found), all using Mockito with no real database or Spring context — fast, isolated unit tests.



CI runs these automatically via GitHub Actions on every push/PR (`.github/workflows/ci.yml`), using an isolated H2 in-memory database so no real PostgreSQL instance is needed in the pipeline.



\## Tech stack



\- Java 17, Spring Boot 4.1.1

\- Spring Data JPA + PostgreSQL (runtime), H2 (tests only)

\- Spring Security + JWT (`jjwt` 0.12.5)

\- springdoc-openapi 3.1.1 (Swagger UI)

\- JUnit 5 + Mockito

