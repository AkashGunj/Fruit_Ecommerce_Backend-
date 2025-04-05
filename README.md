# Fruit Shop E-commerce Application

A Spring Boot-based e-commerce application for selling fruits online. This application provides user authentication, product management, shopping cart functionality, and order processing.

## Features

- User Registration and Authentication
- JWT-based Security
- Password Reset Functionality
- Product Management
- Shopping Cart
- Order Processing
- Email Notifications

## Technologies Used

- Java 11
- Spring Boot 2.7.0
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- JUnit 5
- Lombok
- Maven

## Getting Started

### Prerequisites

- JDK 11 or later
- Maven 3.6 or later
- Your favorite IDE (IntelliJ IDEA, Eclipse, etc.)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/fruit-shop-ecommerce.git
cd fruit-shop-ecommerce
```

2. Configure application.properties:
   - Update email configuration with your SMTP server details
   - Update JWT secret key
   - (Optional) Configure database settings if not using H2

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Endpoints

#### Authentication
- POST `/api/auth/signup` - Register new user
- POST `/api/auth/signin` - Login user
- POST `/api/auth/forgot-password` - Request password reset
- POST `/api/auth/reset-password` - Reset password

#### Products
- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get product by ID
- POST `/api/products` - Add new product (Admin only)
- PUT `/api/products/{id}` - Update product (Admin only)
- DELETE `/api/products/{id}` - Delete product (Admin only)

#### Cart
- GET `/api/cart` - View cart
- POST `/api/cart/add` - Add item to cart
- PUT `/api/cart/update` - Update cart item
- DELETE `/api/cart/remove/{id}` - Remove item from cart

#### Orders
- POST `/api/orders` - Place order
- GET `/api/orders` - Get user's orders
- GET `/api/orders/{id}` - Get order details

## Testing

Run the tests using:
```bash
mvn test
```

## Security

The application uses JWT for authentication. Protected endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request 