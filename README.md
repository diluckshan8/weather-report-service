# Weather Report Service

A Spring Boot service that provides weather data for cities from multiple providers with failover capabilities.

## Features

- Fetches weather data from WeatherStack (primary) and OpenWeatherMap (failover)
- Implements circuit breaker pattern for resilience
- Caches weather data for 3 seconds
- Serves stale data if all providers are down
- RESTful API with JSON response
- Comprehensive error handling

## Technology Stack

- Java 21
- Spring Boot 3.3.1
- Spring WebFlux for reactive programming
- Resilience4j for circuit breaking
- Caffeine for caching
- Lombok for reducing boilerplate code

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle gradle-8.11.1
- Podman or Docker installed
- API keys for WeatherStack and OpenWeatherMap

### Configuration

1. Update `application.properties` with your API keys.

## Running the Application in a Container

This application can be containerized and run using either **Podman** or **Docker**.

### Build the Container Image

#### Using Podman
```bash
podman build -t weather-service:latest .
```

#### Using Docker
```bash
docker build -t weather-service:latest .
```

### Run the Container

#### Using Podman
```bash
podman run --name weather-service -p 8080:8080 weather-service:latest
```

#### Using Docker
```bash
docker run --name weather-service -p 8080:8080 weather-service:latest
```

## Design Decisions and Trade-offs

### Architecture

- **Reactive Programming**: Used WebFlux for non-blocking I/O, allowing the service to handle more concurrent requests.
- **Circuit Breaker Pattern**: Implemented with Resilience4j to handle failures gracefully and prevent cascading failures.
- **Layered Design**: The service is modular, with separate layers for API, service, and data access.
- **Caching**: Caffeine is used to cache weather data for 3 seconds, reducing API calls to external providers.

### Trade-offs

1. **Caching Implementation**
    - Benefits:
        - Reduced external API calls through @Cacheable annotation
        - Better response times for repeated requests
    - Costs:
        - Possibility of serving stale data (cache expiry is configurable via weather.cache.expiry.seconds)
        - Memory overhead for storing cached responses
        - Added complexity in managing cache invalidation

2. **Data Units and Language Support**
    - Currently uses fixed units and language settings
    - WeatherStack supports multiple options:
        - Units: Metric (m), Scientific (s), Fahrenheit (f)
        - Multiple language options available
    - Making these configurable would add complexity but increase flexibility

3. **Error Handling Granularity**
    - WeatherStack provides specific error codes that could be handled more precisely
    - Trade-off between implementation complexity and detailed error responses

### Future Enhancements

1. **Testing Improvements**
    - Add integration tests using Testcontainers or WireMock to simulate provider outages
    - Build a Docker Compose setup to test against mock APIs
    - Validate full service behavior under different scenarios

2. **Documentation**
    - Add OpenAPI (Swagger) documentation for easy developer onboarding
    - Improve API documentation with examples and use cases

3. **Security Enhancements**
    - Implement API key authentication for service access
    - Add request signing for enhanced security
    - Rate limiting implementation

4. **Operational Improvements**
    - Add detailed logging and tracing capabilities
    - Implement proper metrics collection
    - Enhanced monitoring and alerting setup

5. **Error Handling**
    - Implement specific error handling for WeatherStack error codes
    - Add detailed error messages and suggestions
    - Improve error reporting and monitoring


## API Usage

### Get Weather Data

The service provides weather information through a RESTful API interface. Below are detailed examples of various API requests and their responses.

### 1. Successful Weather Request

```http
GET /v1/weather?city=Melbourne
Accept: */*
Content-Type: application/json
```

#### Successful Response
```json
{
    "wind_speed": 12.0,
    "temperature_degrees": 22.0
}
```

### 2. Failed Provider Failover

When the primary provider (WeatherStack) fails, the service attempts to use the secondary provider (OpenWeatherMap). If both fail:

```http
GET /v1/weather?city=unKnowncity
Accept: */*
Content-Type: application/json
```

#### Error Response
```json
{
    "timestamp": "2025-05-11T13:22:50.8088399",
    "status": 503,
    "error": "Service Unavailable",
    "message": "Weather service error: OpenWeatherMap service is currently unavailable"
}
```

### 3. Missing City Parameter

```http
GET /v1/weather
Accept: */*
Content-Type: application/json
```

#### Error Response
```json
{
    "timestamp": "2025-05-11T01:09:23.0522143",
    "status": 400,
    "error": "Bad Request",
    "message": "Missing required parameter: city"
}
```

### 4. Empty City Value

```http
GET /v1/weather?city=
Accept: */*
Content-Type: application/json
```

#### Error Response
```json
{
    "timestamp": "2025-05-11T01:09:36.4872042",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation error: getWeather.city: must not be blank"
}
```

### 5. Invalid API Access Key

```http
GET /v1/weather?city=melbourne
Accept: */*
Content-Type: application/json
```

#### Error Response
```json
{
    "timestamp": "2025-05-11T01:09:50.6079702",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid API access key provided"
}
```

### 6. Invalid API Version

```http
GET /v2/weather?city=melbourne
Accept: */*
Content-Type: application/json
```

#### Error Response
```json
{
    "timestamp": "2025-05-11T01:10:05.6418949",
    "status": 404,
    "error": "Not Found",
    "message": "No static resource v2/weather."
}
```

### Error Handling

The API implements comprehensive error handling with appropriate HTTP status codes:

- `200 OK`: Successful request
- `400 Bad Request`: Invalid input parameters
- `401 Unauthorized`: Invalid API key
- `404 Not Found`: Invalid endpoint
- `503 Service Unavailable`: External weather services unavailable
