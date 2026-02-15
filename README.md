# SIGRAP - Stationery Store Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📝 Description

SIGRAP is a comprehensive management system designed specifically to streamline operations for stationery stores. This application integrates sales tracking, inventory management, and supplier coordination into a unified platform.

### Key Features

- 🏪 **Sales Management**: Transaction control and tracking
- 📦 **Inventory Control**: Automatic stock updates
- 👥 **Customer Management**: Customer tracking and preferences
- 🤝 **Supplier Management**: Order coordination and tracking
- 👤 **Employee Management**: Access control and roles
- 📊 **Detailed Reports**: Report generation and analysis
- 🔔 **Notification System**: Stock and order alerts

## 🚀 Technologies

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Main database
- **H2** - Test database
- **JWT** - Authentication token management
- **MapStruct** - Object mapping
- **Lombok** - Boilerplate code reduction
- **SpringDoc OpenAPI** - API documentation
- **JaCoCo** - Code coverage

## 🏗️ Architecture

SIGRAP follows **Hexagonal Architecture** (also known as Ports and Adapters pattern) to ensure clean separation of concerns, improved testability, and maintainability.

### Core Principles

- **Isolation of Business Logic**: Domain logic is completely independent of frameworks and infrastructure
- **Dependency Inversion**: All dependencies point inward toward the domain layer
- **Testability**: Core business logic can be tested without Spring context or database
- **Flexibility**: Easy to swap implementations (e.g., different databases, REST vs GraphQL)

### Layer Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                      │
│  ┌────────────────────┐              ┌──────────────────┐  │
│  │  Input Adapters    │              │ Output Adapters  │  │
│  │  - REST Controller │              │ - JPA Repository │  │
│  │  - Request/Response│              │ - JPA Entities   │  │
│  └────────┬───────────┘              └────────┬─────────┘  │
│           │                                    │             │
└───────────┼────────────────────────────────────┼─────────────┘
            │                                    │
┌───────────▼────────────────────────────────────▼─────────────┐
│                    Application Layer                          │
│  ┌────────────────────┐              ┌──────────────────┐   │
│  │   Input Ports      │              │  Output Ports    │   │
│  │  - Use Cases       │              │  - Repository    │   │
│  │  - Commands        │              │    Interfaces    │   │
│  └────────────────────┘              └──────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           Use Case Implementations                    │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────────┐
│                       Domain Layer (Core)                      │
│  ┌──────────────────────────────────────────────────────┐    │
│  │              Domain Models                            │    │
│  │  - Entities (POJOs)                                  │    │
│  │  - Value Objects                                     │    │
│  │  - Domain Services                                   │    │
│  └──────────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────────┐    │
│  │              Repository Ports                         │    │
│  │  - Repository Interfaces                             │    │
│  └──────────────────────────────────────────────────────┘    │
└────────────────────────────────────────────────────────────────┘
```

### Package Structure

Each module follows a consistent hexagonal architecture structure:

```
com.sigrap.{module}
├── domain                    # Domain layer (core business logic)
│   ├── model                # Domain entities and value objects
│   ├── port                 # Output ports (repository interfaces)
│   └── service              # Domain services
├── application              # Application layer (use cases)
│   ├── port
│   │   ├── in              # Input ports (use case interfaces)
│   │   │   └── command     # Command DTOs
│   │   └── out             # Output ports
│   └── service             # Use case implementations
└── infrastructure           # Infrastructure layer (adapters)
    ├── adapter
    │   ├── in
    │   │   └── rest        # REST controllers and DTOs
    │   └── out
    │       └── persistence # JPA entities and repositories
    └── config              # Spring configuration
```

### Example: Category Module

The **category module** serves as the reference implementation demonstrating all hexagonal architecture patterns:

**Domain Layer** (`com.sigrap.category.domain`):
- `Category`: Pure POJO domain entity with business logic
- `CategoryId`, `CategoryName`: Self-validating value objects
- `CategoryRepositoryPort`: Repository interface defining domain needs

**Application Layer** (`com.sigrap.category.application`):
- `CreateCategoryUseCase`, `GetCategoryUseCase`: Input port interfaces
- `CreateCategoryCommand`, `UpdateCategoryCommand`: Immutable command DTOs
- `CreateCategoryService`, `GetCategoryService`: Use case implementations with transaction management

**Infrastructure Layer** (`com.sigrap.category.infrastructure`):
- `CategoryController`: REST adapter translating HTTP to use case calls
- `CategoryRequest`, `CategoryResponse`: REST DTOs with validation
- `CategoryJpaEntity`: JPA entity for persistence
- `CategoryPersistenceAdapter`: Adapter implementing repository port
- `CategoryPersistenceMapper`: MapStruct mapper for entity conversion

### Key Benefits

- **Framework Independence**: Business logic has no Spring or JPA dependencies
- **Testability**: Domain and application layers can be tested without Spring context
- **Maintainability**: Clear boundaries and responsibilities for each layer
- **Flexibility**: Easy to swap adapters (e.g., switch from REST to GraphQL, or PostgreSQL to MongoDB)
- **Backward Compatibility**: Existing APIs remain unchanged during migration

### Migration Status

The project is currently migrating from traditional layered architecture to hexagonal architecture:

- ✅ **Category Module**: Fully migrated (reference implementation)
- 🔄 **Other Modules**: Migration in progress

### Documentation

For detailed architecture documentation, see:
- [Design Document](.kiro/specs/hexagonal-architecture-migration/design.md) - Complete architecture design
- [Patterns and Conventions](.kiro/specs/hexagonal-architecture-migration/patterns-and-conventions.md) - Implementation patterns
- [Migration Guide](.kiro/specs/hexagonal-architecture-migration/migration-guide.md) - Step-by-step migration instructions
- [Category Module Architecture](.kiro/specs/hexagonal-architecture-migration/category-module-architecture.md) - Reference implementation details

## 🛠️ Prerequisites

- Java 21 or higher
- Maven 3.9.0 or higher
- PostgreSQL

## ⚙️ Setup

1. Clone the repository:
```bash
git clone https://github.com/Nick220505/sigrap-backend.git
cd sigrap-backend
```

2. Configure the database:
   - Create a PostgreSQL database
   - Update credentials in `application-dev.properties` or `application-prod.properties`

3. Build the project:
```bash
./mvnw clean install
```

## 🚀 Running the Application

### Development
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Production
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## 📚 API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🧪 Testing

Run unit tests:
```bash
./mvnw test
```

Generate coverage report (JaCoCo):
```bash
./mvnw verify
```
The report will be available at: `target/site/jacoco/index.html`

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/sigrap/
│   │   ├── audit/         # Audit logging
│   │   ├── auth/          # Authentication and security
│   │   ├── category/      # Category management
│   │   ├── customer/      # Customer management
│   │   ├── employee/      # Employee management
│   │   ├── product/       # Product management
│   │   ├── sale/          # Sales management
│   │   ├── supplier/      # Supplier management
│   │   └── user/          # User management
│   └── resources/         # Configuration files
└── test/                  # Unit and integration tests
```

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
