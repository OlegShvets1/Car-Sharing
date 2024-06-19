# 🚗🚕🚙 Car sharing service 🚗🚕🚙

Car Sharing is an application that simplifies the process of renting a car as much as possible.
The API supports the following operations:
* Car control functionality(available only for the ADMINISTRATOR role):
  - adding new cars.
  - updating outdated car information.
  - deleting cars that are no longer available.
* Car rental functionality:
  - obtaining information about available cars (model, type and availability).
  - car reservation for the required period.
  - check rental history for a specific user.
* Functionality of sending notifications:
  Ability to subscribe to notifications using the Telegram bot.
* Payment functionality:
  The possibility of closing the lease, by making a payment through Stripe.

Used Technologies
Core Technologies:

Java 17
Maven
Spring Framework:

Spring Boot 3.2.0
Spring Boot Web
Spring Data JPA
Spring Boot Security
Spring Boot Validation
Database:

MySQL 8
Hibernate
Liquibase
H2 for testing
Testing:

Spring Boot Starter Test
JUnit 5
Mockito
Docker Test Containers
Auxiliary Libraries and tools:

Docker
Lombok
MapStruct
Swagger
JWT
Thrird party API:

Telegram API
Stripe API

  