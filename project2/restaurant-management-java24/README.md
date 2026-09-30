# Restaurant Management System

Simple Java + Spring Boot + MySQL backend.

## Requirements
- JDK 24
- MySQL 8+
- NetBeans
- Maven (included through Maven project / NetBeans support)
- Postman

## Setup
1. Open this project in NetBeans as a Maven project.
2. Open `src/main/resources/application.properties`.
3. Replace `YOUR_MYSQL_PASSWORD` with your MySQL root password.
4. Run `RestaurantManagementApplication.java`.
5. The API runs at `http://localhost:8080`.

The database `restaurant_db` is created automatically by the JDBC URL.

## Main APIs
GET/POST/PUT/DELETE `/api/menu`
GET/POST `/api/tables`
GET/POST/DELETE `/api/reservations`
GET/POST `/api/orders`
GET `/api/orders/{id}`
PUT `/api/orders/{id}/status?value=READY`
GET/POST `/api/inventory/{menuId}`

## Example order
POST `http://localhost:8080/api/orders`

{
  "tableId": 1,
  "items": [
    {"menuItemId": 1, "quantity": 2}
  ]
}

When an order is placed, inventory is automatically reduced inside a transaction.
