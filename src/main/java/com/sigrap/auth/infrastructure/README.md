# Auth Module Infrastructure Layer

This document describes the infrastructure layer implementation for the Auth module following hexagonal architecture patterns.

## Overview

The infrastructure layer connects the Auth module's application layer to external systems and frameworks. It includes:

1. **JWT Adapters** - Token generation and validation
2. **Security Adapters** - Password encoding
3. **Persistence Adapters** - User authentication data access
4. **REST Controllers** - HTTP endpoints for authentication

## Package Structure

```
com.sigrap.auth.infrastructure
├── adapter
│   ├── in
│   │   └── rest
│   │       ├── AuthController.java
│   │       ├── AuthRequest.java
│   │       ├── AuthResponse.java
│   │       ├── RegisterRequest.java
│   │       └── AuthResponseMapper.java
│   └── out
│       ├── jwt
│       │   ├── JwtTokenGeneratorAdapter.java
│       │   └── JwtTokenValidatorAdapter.java
│       ├── security
│       │   └── SpringPasswordEncoderAdapter.java
│       └── persistence
│           └── UserAuthenticationAdapter.java
└── config
    └── AuthConfig.java
```

## Components

### 1. JWT Adapters (Output Adapters)

#### JwtTokenGeneratorAdapter
- **Implements**: `TokenGeneratorPort`
- **Purpose**: Generates JWT tokens for authenticated users
- **Dependencies**: `JwtUtil`, JWT expiration configuration
- **Key Methods**:
  - `generateToken(Email)` - Creates a JWT token with expiration time

#### JwtTokenValidatorAdapter
- **Implements**: `TokenValidatorPort`
- **Purpose**: Validates JWT tokens and extracts user information
- **Dependencies**: `JwtUtil`
- **Key Methods**:
  - `validateToken(String)` - Validates token and returns email
  - `isTokenValid(String)` - Checks token validity without throwing exceptions

### 2. Security Adapter (Output Adapter)

#### SpringPasswordEncoderAdapter
- **Implements**: `PasswordEncoderPort`
- **Purpose**: Encodes and validates passwords using BCrypt
- **Dependencies**: Spring Security's `BCryptPasswordEncoder`
- **Key Methods**:
  - `encode(Password)` - Encodes a plain text password
  - `matches(Password, String)` - Validates password against encoded hash

### 3. Persistence Adapter (Output Adapter)

#### UserAuthenticationAdapter
- **Implements**: `UserAuthenticationPort`
- **Purpose**: Bridges Auth module to User module's persistence layer
- **Dependencies**: 
  - `UserJpaRepository` (from User module)
  - `RoleJpaRepository` (from User module)
  - `PasswordEncoderPort`
- **Key Methods**:
  - `authenticate(Credentials)` - Validates user credentials
  - `findUserByEmail(Email)` - Retrieves user information
  - `existsByEmail(Email)` - Checks if user exists
  - `registerUser(RegistrationData, String)` - Creates new user account
  - `updateLastLogin(Email)` - Updates last login timestamp (placeholder)

**Note**: The current implementation doesn't update `lastLogin` as the `UserJpaEntity` doesn't have this field. This can be added to the User module if needed.

### 4. REST Controller (Input Adapter)

#### AuthController
- **Endpoint Base**: `/api/v2/auth`
- **Purpose**: Handles HTTP requests for authentication operations
- **Dependencies**: 
  - `AuthenticateUserUseCase`
  - `RegisterUserUseCase`
  - `ValidateTokenUseCase`
  - `AuthResponseMapper`

**Endpoints**:

| Method | Path | Description | Request | Response |
|--------|------|-------------|---------|----------|
| POST | `/api/v2/auth/login` | Authenticate user | `AuthRequest` | `AuthResponse` |
| POST | `/api/v2/auth/register` | Register new user | `RegisterRequest` | `AuthResponse` |
| GET | `/api/v2/auth/validate` | Validate JWT token | Header: Authorization | `TokenValidationResponse` |

### 5. DTOs

#### AuthRequest
- **Fields**: `email`, `password`
- **Validation**: Email format, password min length (8 chars)

#### RegisterRequest
- **Fields**: `name`, `email`, `password`
- **Validation**: Name length (2-50), email format, password min length (8 chars)

#### AuthResponse
- **Fields**: `token`, `expiresAt`, `email`, `name`, `authenticatedAt`, `role`
- **Purpose**: Returns authentication result with JWT token and user info

### 6. Mappers

#### AuthResponseMapper
- **Type**: MapStruct interface
- **Purpose**: Converts `AuthenticationResult` domain object to `AuthResponse` DTO
- **Mappings**:
  - `token` ← `token.value`
  - `expiresAt` ← `token.expiresAt`
  - `email` ← `email.value`
  - `authenticatedAt` ← `lastLogin`
  - `name` ← `name` (automatic)
  - `role` ← `role` (automatic)

## Configuration

### AuthConfig
- **Purpose**: Spring configuration for Auth module
- **Current State**: Minimal - all components are auto-configured via annotations
- **Auto-configured Components**:
  - Use case implementations (`@Service`)
  - Adapters (`@Component`)
  - MapStruct mappers (`@Mapper(componentModel = "spring")`)

### Security Configuration
- **Location**: `com.sigrap.config.SecurityConfig`
- **Public Endpoints**: `/api/auth/**` (covers both old and new endpoints)
- **Authentication**: JWT-based, stateless sessions
- **Note**: The existing configuration already allows the new `/api/v2/auth` endpoints

## Integration with Existing Code

### Reused Components
1. **JwtUtil** - Existing JWT utility for token operations
2. **UserJpaRepository** - User module's repository for data access
3. **RoleJpaRepository** - User module's repository for role assignment
4. **SecurityConfig** - Existing security configuration (no changes needed)

### Backward Compatibility
- Old endpoints at `/api/auth/**` remain functional
- New endpoints at `/api/v2/auth/**` follow hexagonal architecture
- Both can coexist during migration period
- Old endpoints can be deprecated and removed later

## Testing Strategy

### Unit Tests (Recommended)
1. **JWT Adapters**: Test token generation and validation logic
2. **Security Adapter**: Test password encoding and matching
3. **Persistence Adapter**: Test with mocked repositories
4. **Controller**: Test with mocked use cases

### Integration Tests (Recommended)
1. **End-to-End**: Test complete authentication flow
2. **API Tests**: Test REST endpoints with real Spring context
3. **Security Tests**: Verify JWT authentication works correctly

## Usage Examples

### Authentication Flow
```
1. Client sends POST /api/v2/auth/login with email and password
2. AuthController creates AuthenticateUserCommand
3. AuthenticateUserService validates credentials via UserAuthenticationAdapter
4. Service generates JWT token via JwtTokenGeneratorAdapter
5. Service returns AuthenticationResult
6. Controller maps to AuthResponse and returns to client
```

### Registration Flow
```
1. Client sends POST /api/v2/auth/register with name, email, and password
2. AuthController creates RegisterUserCommand
3. RegisterUserService checks if user exists via UserAuthenticationAdapter
4. Service encodes password via SpringPasswordEncoderAdapter
5. Service creates user via UserAuthenticationAdapter
6. Service generates JWT token via JwtTokenGeneratorAdapter
7. Service returns AuthenticationResult
8. Controller maps to AuthResponse and returns to client
```

## Future Enhancements

1. **Last Login Tracking**: Add `lastLogin` field to `UserJpaEntity` and implement update logic
2. **Refresh Tokens**: Implement refresh token mechanism for long-lived sessions
3. **Multi-Factor Authentication**: Add MFA support
4. **OAuth2 Integration**: Support social login providers
5. **Rate Limiting**: Add rate limiting for authentication endpoints
6. **Audit Logging**: Integrate with audit module for authentication events

## Architecture Compliance

This implementation follows hexagonal architecture principles:

✅ **Dependency Inversion**: Infrastructure depends on domain/application, not vice versa
✅ **Port-Adapter Pattern**: All external integrations go through ports
✅ **Framework Independence**: Domain/application layers have no Spring dependencies
✅ **Testability**: Each layer can be tested independently
✅ **Separation of Concerns**: Clear boundaries between layers
✅ **Naming Conventions**: Follows established patterns from other modules

## References

- [Hexagonal Architecture Migration Spec](../../../../.kiro/specs/hexagonal-architecture-migration/)
- [Patterns and Conventions](../../../../.kiro/specs/hexagonal-architecture-migration/patterns-and-conventions.md)
- [Auth Domain Layer](../domain/)
- [Auth Application Layer](../application/)
