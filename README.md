# Spring Cloud Demo

This project is a minimal Spring Cloud demo built with Spring Boot 3 and Spring Cloud 2023. It contains:

- `common-core` – shared utilities and common result wrappers
- `common-api` – Feign interfaces and shared DTOs
- `eureka-server` – service registry
- `gateway` – API gateway
- `user-service` – sample user API
- `order-service` – sample order API

## Run steps

```bash
mvn clean package
java -jar eureka-server/target/eureka-server-1.0.0.jar
java -jar gateway/target/gateway-1.0.0.jar
java -jar user-service/target/user-service-1.0.0.jar
java -jar order-service/target/order-service-1.0.0.jar
```

## Test endpoints

- Eureka: http://localhost:8761
- User service: http://localhost:8001/users
- Order service: http://localhost:8002/orders
- Order service calls user service: http://localhost:8002/orders/users
- Gateway: http://localhost:9900/api/user/users and http://localhost:9900/api/order/orders
- Gateway compatibility endpoints: http://localhost:9900/api/users and http://localhost:9900/api/orders

## Module responsibilities

- `common-core`: shared tool classes and generic Result wrapper
- `common-api`: Feign client interfaces and shared DTOs
- `eureka-server`: registry center and service discovery
- `gateway`: unified gateway, routing `/user/**` and `/order/**`
- `user-service`: provides user data
- `order-service`: provides order data and calls `user-service` through Feign
