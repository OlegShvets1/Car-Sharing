# 🚗🚕🚙 Hello! The online Сar Sharing app welcomes you! 🚗🚕🚙

![1GIF](CarSharingMainPicture.jpg)

####   Car Sharing is an application that simplifies the process of renting a car as much as possible and helps the user choose exactly what he needs.

## The API supports the following operations:

### Car control functionality(available only for the ADMINISTRATOR role):
  - adding new cars.
  - updating outdated car information.
  - deleting cars that are no longer available.
### Car rental functionality:
  - obtaining information about available cars (model, type and availability).
  - car reservation for the required period.
  - check rental history for a specific user.
### Functionality of sending notifications:
  - ability to subscribe to notifications using the Telegram bot.
### Payment functionality:
  - the possibility of closing the lease, by making a payment through Stripe.

## ⚙️ Used Technologies ⚙️ 

### Java 17
### Maven
### Spring Framework:
- Spring Boot 
- Spring Boot Web
- Spring Data JPA
- Spring Boot Security
- Spring Boot Validation

### Database:
- MySQL 8
- Hibernate
- Liquibase
- H2 for testing

### Testing:
- Spring Boot Starter Test
- Mockito
- Docker Test Containers

### Additionaly libraries and tools:
- Docker
- Lombok
- MapStruct
- Swagger
- JWT
- Thrird party API:
  - Telegram API
  - Stripe API

## Project structure
![1GIF](architecture.png)

  This Spring Boot application has the most common structure with the following **main layers**:
- repository (for working with the database).
- service (for implementing business logic).
- controller (for receiving customer requests and receiving answers to them).
  
## 🏔️ Endpoints
### 🔑 AuthenticationController: Handles registration and login requests, supporting both Basic and JWT authentication.
- `POST: /auth/registration` - The endpoint for registration.
- `POST: /auth/login` - The endpoint for login.

### 🚗 CarsController: Handles requests for car CRUD operations.
- `GET: /cars` - The endpoint for retrieving all avaliable cars.
- `GET: /cars/{id}` - The endpoint for searching a specific car by ID.
- `POST: /cars` - The endpoint for creating a new  car. (Available Administrator Only)
- `PUT: /cars/{id}` - The endpoint for updating car information. (Available Administrator Only)
- `DELETE: /cars/{id}` - The endpoint for deleting car. (Available Administrator Only)

### 👦👧 UsersController: Handles requests for user operations.
- `GET: /users/me` - The endpoint for retrieving user`s information.
- `PATCH: /users/me` - The endpoint for updating user`s information.
- `PUT: /users/{id}/role?role=` - The endpoint for updating user's role. (Available Administrator Only)

### 🛒 RentalsController: Handles requests for rental operations.
- `GET: /rentals/my` - The endpoint for retrieving all rentals by its owner.
- `GET: /rentals?userId=&is_active=` - The endpoint for retrieving all rentals by user and activity. (Available Administrator Only)
- `GET: /rentals/{id}` - The endpoint for retrieving a specific rental by ID. (Available Administrator Only)
- `POST: /rentals` - The endpoint for creating a new rental, it will send notification on creation.
- `POST: /rentals/{id}/return` - The endpoint for returning rental.

###  💸 PaymentsController: Handles requests for payment operations.
- `GET: /payments?user_id=` - The endpoint for retrieving all payments by user ID. (Available Administrator Only)
- `GET: /payments/success` - Success endpoint for payment, it will send notification on success.
- `GET: /payments/cancel` - Cancel endpoint for payment.
- `POST: /payments` - The endpoint for creating payment session using Stripe API, it will send notification on creation.

## Roles 

 Only 2 user roles are available in this application: 'USER role' and 'MANAGER role'.
 The user is given the opportunity to perform the following actions: search for a car, display a car by ID, rent a car, etc.
 However, a user with the USER role cannot delete cars from the database or update them. (Only a user with the role of MANAGER can perform these manipulations)


## How to run a project on your mashine?
1. Download [☕Java](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and [🐋Docker](https://www.docker.com/products/docker-desktop/) and install them on your PC.
2. Clone the repository:
   - Open a terminal and paste the following link: "https://github.com/OlegShvets1/Car-Sharing"
3. Create an .env file with the appropriate variables(The .env.sample file with all the necessary variables will be available to you when you complete the previous point).
4. Create a project:
    - Open the project in the IDE.
    - Enter all required variables in application.properties.
    - Open a terminal and execute the command: "mvn clean package".
5. Use Docker Compose:
     Open a terminal and execute the commands:
    - "docker compose build" 
    - "docker compose up"
