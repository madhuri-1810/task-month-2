# E-commerce Backend System - Month 2 Task

**Internship:** Full Stack Java | The Developers Arena  
**Student:** Madhuri Anil Choudhari | EMP20260110-273  
**Batch:** January 2026 - 6 Months  

## Project Overview

Complete RESTful E-commerce Backend built with **Spring Boot 2.7**, **Spring Security + JWT**, **Spring Data JPA**, and **PostgreSQL**.

## Technology Stack

| Technology | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 2.7.0 |
| Spring Security | 5.7 |
| Spring Data JPA | 2.7 |
| PostgreSQL | 14 |
| JWT (jjwt) | 0.11.5 |
| Swagger/OpenAPI | 3.0 |
| Lombok | Latest |
| ModelMapper | 3.1.0 |

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Step 1: Clone Repository
```bash
git clone https://github.com/madhuri-1810/month2-task.git
cd month2-task
```

### Step 2: Create Database
```sql
CREATE DATABASE ecommerce_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO postgres;
```

### Step 3: Configure application.properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Step 4: Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

Application starts at: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT token |

### Products (Public - GET, Admin - POST/PUT/DELETE)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products (paginated) |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?keyword=` | Search products |
| GET | `/api/products/category/{id}` | Products by category |
| POST | `/api/products` | Create product (Admin) |
| PUT | `/api/products/{id}` | Update product (Admin) |
| DELETE | `/api/products/{id}` | Delete product (Admin) |

### Cart (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cart` | View cart |
| POST | `/api/cart/add?productId=&quantity=` | Add to cart |
| PUT | `/api/cart/update/{itemId}?quantity=` | Update quantity |
| DELETE | `/api/cart/remove/{itemId}` | Remove from cart |

### Orders (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Place order |
| GET | `/api/orders` | Order history |
| GET | `/api/orders/{id}` | Order details |
| GET | `/api/admin/orders` | All orders (Admin) |
| PUT | `/api/admin/orders/{id}/status` | Update status (Admin) |

## Usage Example

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Madhuri","email":"madhuri@example.com","password":"password123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"madhuri@example.com","password":"password123"}'
```
Copy the `token` from response.

### 3. Get Products
```bash
curl http://localhost:8080/api/products?page=0&size=10
```

### 4. Add to Cart (with token)
```bash
curl -X POST "http://localhost:8080/api/cart/add?productId=1&quantity=2" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Database Schema

- **users** - User accounts and authentication
- **categories** - Product categories
- **products** - Product catalog with stock management
- **carts** - User shopping carts (OneToOne with User)
- **cart_items** - Items in cart (ManyToMany bridge)
- **orders** - Placed orders
- **order_items** - Items in each order

## Security Features

- JWT token authentication (24h expiry)
- BCrypt password hashing (strength 12)
- Role-based access control (USER / ADMIN)
- Stateless session management

## Running Tests
```bash
mvn test
```

## Project Structure
```
src/main/java/com/ecommerce/
├── ECommerceApplication.java
├── config/          - SecurityConfig, AppConfig
├── controller/      - REST Controllers
├── service/         - Business Logic
├── repository/      - JPA Repositories
├── model/           - JPA Entities
├── dto/             - Data Transfer Objects
├── security/        - JWT, UserDetailsService
└── exception/       - Exception Handling
```

---
*Month 2 Task Submission | Full Stack Java Internship | January 2026*
