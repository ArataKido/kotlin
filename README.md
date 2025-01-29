# Kotlin Delivery API

This repository implements a Kotlin-based delivery order pricing API using Ktor. The application is designed to calculate delivery order prices dynamically based on user input and predefined business rules.

## Features
- **REST API**:
  - Provides endpoints for delivery pricing.
  - Validates user input with detailed error messages.
- **Integration with APIs**:
  - Fetches venue-specific data dynamically from a remote API.
- **Custom Middleware**:
  - Centralized exception handling.
  - Logging middleware for tracking requests and responses.
- **Modular Architecture**:
  - Uses Koin for dependency injection.
  - Separation of concerns via services, clients, and utilities.

## Installation
1. Build the project:
  ```bash
    ./gradlew build
  ```
2. Run the application:
  ```bash
    ./gradlew run
  ```
3. Run test:
  ```bash
    ./gradlew test
  ```

## API Endpoints
### `GET /api/v1/delivery-order-price`
- Description: Calculates the delivery order price based on user input.

- Parameters:

- - venue_slug (string): The venue identifier.
- - cart_value (integer): Total value of the items in the shopping cart.
- - user_lat, user_lon (float): User's latitude and longitude coordinates.
- - Response:
```json
{
  "total_price": 1500,
  "small_order_surcharge": 200,
  "cart_value": 1000,
  "delivery": {
    "fee": 300,
    "distance": 5000
  }
}
```
- Error Handling:
- -Returns appropriate HTTP status codes and error messages for missing or invalid parameters.
## Code Highlights
### Core Components
- `Application.kt`: Main entry point. Configures routes, middleware, and services.
- `Config.kt`: Handles application settings and HTTP client configuration.
- `Frameworks.kt`: Manages dependency injection using Koin.
### Business Logic
- `DeliveryOrderPriceServiceImpl`: Implements the logic for calculating delivery prices, leveraging utility functions and external API data.
### Utilities
- `DeliveryFeeCalculator`: Calculates delivery fees based on distance and predefined ranges.
- `DistanceCalculator`: Computes distances using Haversine or other strategies.
### Testing
The `src/test` directory includes unit tests for:

- API clients
- Delivery fee calculation logic
- Delivery price service implementation


