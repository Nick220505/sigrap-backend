# SIGRAP - Stationery Store Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)](https://spring.io/projects/spring-boot)
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

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos principal
- **H2** - Base de datos para pruebas
- **JWT** - Gestión de tokens de autenticación
- **MapStruct** - Mapeo de objetos
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI** - Documentación de API
- **JaCoCo** - Cobertura de código

## 🛠️ Prerequisites

- Java 21 or higher
- Maven 3.9.0 or higher
- PostgreSQL

## ⚙️ Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/sigrap-backend.git
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

## 📧 Contact

- **Project** - [GitHub Repository](https://github.com/your-username/sigrap-backend)
