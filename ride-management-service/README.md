# Ride Management Service (Member 3)

Owns ride requests, driver assignment, ride lifecycle and cancellation for RideLink.

- Port: `8083`
- Database: PostgreSQL, `riddb` (own schema, no cross-service DB access)
- Auth: validates JWTs issued by Account Service (does not issue tokens itself)

## Run locally

```
export DB_PASSWORD=yourpassword
export JWT_SECRET=<same secret agreed across all 4 services>
mvn spring-boot:run
```

Swagger UI: http://localhost:8083/swagger-ui.html
Health check: http://localhost:8083/health

## Endpoints

| Method | Path                    | Role(s)                  | Description                                   |
|--------|-------------------------|---------------------------|------------------------------------------------|
| POST   | /api/rides              | PASSENGER                 | Create a ride request (auto-assigns a driver) |
| GET    | /api/rides/{id}         | PASSENGER, DRIVER, ADMIN  | Retrieve a ride by id                          |
| GET    | /api/rides?passengerId= | PASSENGER, DRIVER, ADMIN  | List rides for a passenger                     |
| GET    | /api/rides?driverId=    | PASSENGER, DRIVER, ADMIN  | List rides for a driver                        |
| PATCH  | /api/rides/{id}/status  | PASSENGER, DRIVER, ADMIN  | Transition ride status                         |

## Ride lifecycle

```
REQUESTED -> ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED
   \            \            \             \
    -------------------- CANCELLED ---------
```

Invalid transitions (e.g. `REQUESTED -> COMPLETED`) return `409 Conflict`.

## Interservice interactions (both synchronous REST, both called by this service)

1. **Ride -> Driver & Vehicle Service** — `GET /api/drivers/available`, called on ride creation to assign a driver.
   Negative case: no available driver -> `409 Conflict`, ride is not created.
2. **Ride -> Fare & Payment Service** — `POST /api/payments`, called when status changes to `COMPLETED`.

## Negative scenarios demonstrated

- No available driver on ride creation
- Invalid ride status transition
- Ride not found
- Validation errors on ride creation (missing/blank fields)

## Tests

```
mvn test
```

Covers the ride state machine (valid + invalid transitions) and `RideService`
(driver assignment, no-driver negative case, invalid transition, payment trigger
on completion, cancellation reason).
