# Minimal E-Commerce Backend API

A Spring Boot application providing a backend for a minimal E-Commerce system. Features include Product management, Shopping Cart, Order placement, and Payment integration (Mock Service with Webhooks).

## 🏗 Architecture
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.1
- **Database**: MongoDB
- **Build Tool**: Maven

## 🚀 Features
1. **Product Management**: Create and list products.
2. **Shopping Cart**: Add items, view cart, clear cart.
3. **Order Management**: Create orders from cart, view order status (CREATED, PAID).
   - Mock Payment Service running on port 8081.
   - Async Webhook callbacks to update order status.

## 🔄 Project Workflow

This project implements the core backend logic for an e-commerce platform:

1.  **The Store Shelf (Products)**:
    -   **Concept**: Shopkeepers list items (e.g., Laptops) for sale.
    -   **Action**: Use `POST` to add items to the database.
2.  **The Basket (Cart)**:
    -   **Concept**: Customers select products and add them to a personal cart. The system checks stock availability.
    -   **Action**: Use `POST /api/cart/add` to create a basket of items.
3.  **The Checkout (Order)**:
    -   **Concept**: Converting the cart into a formal "Order". This captures the final price and items.
    -   **Action**: Use `POST /api/orders` to generate an Order ID.
4.  **The Cashier (Payment)**:
    -   **Concept**: Initiating the transaction. Since we use a Mock Payment Service, it simulates a real bank delay (3 seconds) and uses a **Webhook** to confirm success.
    -   **Action**: `POST /api/payments/create` starts the process -> Wait -> Order status updates to `PAID` automatically.

## 📋 Folder Structure
```
src/main/java/com/example/ecommerce
├── controller      # REST Controllers (Product, Cart, Order, Payment)
├── service         # Business Logic
├── repository      # MongoDB Repositories
├── model           # Data Entities (User, Product, Order, etc.)
├── dto             # Data Transfer Objects
└── webhook         # Webhook Handler
```

## 🛠 Setup & Run

### 1. Database
Ensure MongoDB is running locally on port `27017`.

### 2. Mock Payment Service
Run the python script to simulate external payment gateway:
```bash
python3 mock_payment_service.py
```

### 3. Application
Build and run the Spring Boot app:
```bash
./mvnw clean package -DskipTests
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```
The API will be available at `http://localhost:8080`.

## 🧪 Testing
See `how_to_test.md` for detailed instructions and `curl` scripts.