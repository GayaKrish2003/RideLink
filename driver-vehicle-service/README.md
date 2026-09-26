# RideLink Driver and Vehicle Service

The Driver and Vehicle Service is a Spring Boot microservice in the RideLink platform. It manages driver profiles, vehicle details, driver availability, service areas, and eligible-driver retrieval.

## Main Features

- Create, retrieve, update, and delete drivers
- Update driver availability
- Find available drivers
- Filter eligible drivers by service area
- Create, retrieve, update, and delete vehicles
- Find a vehicle using its driver ID
- Validate incoming request data
- Return clear error responses
- Provide Swagger/OpenAPI documentation
- Provide unit tests and a Postman collection

## Technologies

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- PostgreSQL
- H2 Database for tests
- Maven
- JUnit 5
- Mockito
- Springdoc OpenAPI

## Service Configuration

The service runs on port `8082`.

```properties
server.port=8082
```

The PostgreSQL database name is:

```text
driverdb
```

Before running the application, create the PostgreSQL database:

```sql
CREATE DATABASE driverdb;
```

Set the PostgreSQL password as an environment variable in PowerShell:

```powershell
$env:DB_PASSWORD="your_postgresql_password"
```

## Run the Service

From the repository root:

```powershell
.\driver-vehicle-service\mvnw.cmd -f driver-vehicle-service\pom.xml spring-boot:run
```

The service will be available at:

```text
http://localhost:8082
```

## Run the Tests

From the repository root:

```powershell
.\driver-vehicle-service\mvnw.cmd -f driver-vehicle-service\pom.xml test
```

H2 is used as an in-memory database during testing. PostgreSQL is used when running the actual application.

## Driver API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/drivers` | Create a driver |
| GET | `/api/drivers` | Get all drivers |
| GET | `/api/drivers/{id}` | Get a driver by ID |
| PUT | `/api/drivers/{id}` | Update a driver |
| PATCH | `/api/drivers/{id}/availability?available=true` | Update availability |
| DELETE | `/api/drivers/{id}` | Delete a driver |
| GET | `/api/drivers/eligible` | Get all eligible drivers |
| GET | `/api/drivers/eligible?serviceArea=Colombo` | Filter eligible drivers by area |

## Vehicle API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/vehicles` | Create a vehicle |
| GET | `/api/vehicles` | Get all vehicles |
| GET | `/api/vehicles/{id}` | Get a vehicle by ID |
| GET | `/api/vehicles/driver/{driverId}` | Get a vehicle by driver ID |
| PUT | `/api/vehicles/{id}` | Update a vehicle |
| DELETE | `/api/vehicles/{id}` | Delete a vehicle |

## Example Driver Request

```json
{
  "accountId": 3001,
  "licenseNumber": "D1234567",
  "serviceArea": "Colombo",
  "currentLocation": "Colombo Fort",
  "available": true
}
```

## Example Vehicle Request

```json
{
  "registrationNumber": "WP-CAB-3001",
  "make": "Toyota",
  "model": "Aqua",
  "colour": "White",
  "vehicleType": "Car",
  "manufactureYear": 2020,
  "driverId": 1
}
```

## API Documentation

After starting the service, Swagger UI is available at:

```text
http://localhost:8082/swagger-ui.html
```

The OpenAPI specification is available at:

```text
http://localhost:8082/v3/api-docs
```

## Postman

Import the following files from the `postman` directory into Postman:

- `RideLink-Driver-Vehicle-Service.postman_collection.json`
- `RideLink-Local.postman_environment.json`

Select the `RideLink Local` environment before sending requests.

## Error Responses

The service returns appropriate HTTP status codes:

- `200 OK` for successful retrieval and updates
- `201 Created` for successful creation
- `204 No Content` for successful deletion
- `400 Bad Request` for invalid or duplicate data
- `404 Not Found` when a driver or vehicle does not exist