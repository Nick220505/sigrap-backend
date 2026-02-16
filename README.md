# SIGRAP Backend API

Backend REST API for the SIGRAP (Sistema Integral de Gestión de Recursos y Administración de Productos) application.

## Technology Stack

- **Java 25**
- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **PostgreSQL**
- **SpringDoc OpenAPI** (Swagger)
- **Maven**

## Architecture

This application follows **Hexagonal Architecture** (Ports and Adapters) principles:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: REST controllers, database adapters, external integrations

Each module is self-contained and independent, making the codebase modular and maintainable.

## Getting Started

### Prerequisites

- Java 25 or higher
- Maven 3.9+
- PostgreSQL 14+

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd sigrap/sigrap-backend
   ```

2. **Configure the database**:
   Update `application.properties` with your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/sigrap
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**:
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI JSON: http://localhost:8080/v3/api-docs

## API Documentation

### Accessing Swagger UI

The API documentation is available through Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

Swagger UI provides:
- Interactive API documentation
- Ability to test endpoints directly from the browser
- Request/response schemas with examples
- Authentication support (JWT Bearer tokens)

### Using the API Documentation

1. **Browse Endpoints**: Endpoints are organized by module (Category Management, Product Management, etc.)
2. **View Details**: Click on any endpoint to see detailed documentation
3. **Try It Out**: Use the "Try it out" button to test endpoints
4. **Authentication**: Click "Authorize" to add your JWT token for protected endpoints

### API Modules

The API is organized into the following modules:

- **Category Management**: Product category CRUD operations
- **Product Management**: Product inventory and pricing
- **Customer Management**: Customer information and contacts
- **Supplier Management**: Supplier and purchase order management
- **Sales Management**: Sales transactions and returns
- **User Management**: User, role, and permission management
- **Authentication**: User login and registration
- **Audit Logs**: System audit trail
- **Employee Management**: Attendance and schedule tracking
- **Status**: Application health and version information

### Authentication

Most endpoints require JWT authentication. To authenticate:

1. **Login**: POST to `/api/v2/auth/login` with credentials
2. **Get Token**: Copy the JWT token from the response
3. **Authorize**: In Swagger UI, click "Authorize" and paste the token
4. **Format**: Use `Bearer <your-token>` format

Example:
```bash
curl -X POST http://localhost:8080/api/v2/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sigrap.com","password":"admin123"}'
```

## Development

### Project Structure

```
sigrap-backend/
├── src/main/java/com/sigrap/
│   ├── category/           # Category module
│   ├── product/            # Product module
│   ├── customer/           # Customer module
│   ├── supplier/           # Supplier module
│   ├── sale/               # Sale module
│   ├── user/               # User module
│   ├── auth/               # Authentication module
│   ├── audit/              # Audit module
│   ├── employee/           # Employee module
│   ├── common/             # Shared utilities
│   ├── config/             # Application configuration
│   └── exception/          # Global exception handling
└── src/main/resources/
    ├── application.properties
    └── db/migration/       # Database migrations
```

### Adding New Endpoints

When adding new API endpoints, ensure you:

1. Add proper OpenAPI annotations (@Operation, @ApiResponse, @Parameter)
2. Document request/response models with @Schema
3. Include realistic examples
4. Document all possible response codes
5. Test in Swagger UI

See [API Documentation Guide](.kiro/specs/swagger-api-documentation/API_DOCUMENTATION_GUIDE.md) for detailed instructions.

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package
java -jar target/sigrap-backend-1.0.0.jar
```

## API Endpoints Overview

### Category Management
- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Product Management
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Authentication
- `POST /api/v2/auth/login` - User login
- `POST /api/v2/auth/register` - User registration

For complete endpoint documentation, visit the Swagger UI.

## Configuration

### Application Properties

Key configuration properties:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/sigrap
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## Security

- JWT-based authentication
- Role-based access control (RBAC)
- Permission-based authorization
- Audit logging for all operations

## Contributing

1. Follow the hexagonal architecture pattern
2. Write comprehensive tests
3. Document all API endpoints with OpenAPI annotations
4. Follow the code style guidelines
5. Update documentation when adding features

## Resources

- [API Documentation Guide](.kiro/specs/swagger-api-documentation/API_DOCUMENTATION_GUIDE.md)
- [Adding New Endpoints](.kiro/specs/swagger-api-documentation/ADDING_NEW_ENDPOINTS.md)
- [Best Practices](.kiro/specs/swagger-api-documentation/BEST_PRACTICES.md)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## License

[Your License Here]

## Support

For questions or issues, please contact the development team.
