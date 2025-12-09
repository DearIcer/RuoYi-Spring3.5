# Authentication API

<cite>
**Referenced Files in This Document**   
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java)
- [LoginBody.java](file://src/main/java/com/ruoyi/framework/security/LoginBody.java)
- [SysLoginService.java](file://src/main/java/com/ruoyi/framework/security/service/SysLoginService.java)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java)
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java)
- [CaptchaController.java](file://src/main/java/com/ruoyi/project/common/CaptchaController.java)
- [application.yml](file://src/main/resources/application.yml)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Login Endpoint](#login-endpoint)
3. [JWT Token Management](#jwt-token-management)
4. [Logout Functionality](#logout-functionality)
5. [Authentication Interception](#authentication-interception)
6. [Anonymous Access](#anonymous-access)
7. [Client-Side Token Handling](#client-side-token-handling)
8. [Common Issues and Error Handling](#common-issues-and-error-handling)
9. [Security Configuration](#security-configuration)

## Introduction
The Authentication API in RuoYi-Vue provides secure user authentication through JWT-based token authentication. The system implements a comprehensive authentication mechanism that includes username/password authentication with captcha verification, token generation and refresh, and secure logout functionality. The API integrates with Spring Security to provide robust security features while maintaining a stateless architecture. This documentation details the authentication endpoints, token management, and related security mechanisms.

## Login Endpoint

The `/login` endpoint handles user authentication by validating credentials and generating an authentication token.

### Request Details
- **Endpoint**: `/login`
- **Method**: POST
- **Content-Type**: application/json

### Request Body Structure
The request body must contain the following fields:

| Field | Type | Description |
|-------|------|-------------|
| username | string | User's login username |
| password | string | User's password |
| code | string | Captcha code from the image |
| uuid | string | Unique identifier for the captcha session |

```json
{
  "username": "admin",
  "password": "admin123",
  "code": "1234",
  "uuid": "abc123-def456"
}
```

### Response Format
On successful authentication, the API returns a 200 OK response with the following structure:

```json
{
  "code": 200,
  "msg": "操作成功",
  "time": "2025-05-22T10:30:00",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.xxxxx"
  }
}
```

The response includes:
- `code`: HTTP status code (200 for success)
- `msg`: Status message
- `time`: Timestamp of the response
- `data.token`: JWT authentication token for subsequent requests

### HTTP Status Codes
| Status Code | Description |
|-------------|-------------|
| 200 | Successful authentication |
| 401 | Authentication failed (invalid credentials, expired captcha, etc.) |
| 400 | Bad request (missing required fields) |
| 429 | Too many login attempts (rate limiting) |

**Section sources**
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java#L56-L65)
- [LoginBody.java](file://src/main/java/com/ruoyi/framework/security/LoginBody.java#L8-L70)
- [SysLoginService.java](file://src/main/java/com/ruoyi/framework/security/service/SysLoginService.java#L63-L100)

## JWT Token Management

The authentication system uses JSON Web Tokens (JWT) for stateless authentication, with tokens stored in Redis for enhanced security and management.

### Token Generation
When a user successfully authenticates, the system generates a JWT token containing:
- User identifier (UUID)
- Username
- Token expiration time
- Digital signature using HS512 algorithm

The token is created with a configurable expiration time (default: 30 minutes) as defined in the application configuration.

### Token Refresh Mechanism
The system implements an automatic token refresh mechanism to enhance user experience:

```mermaid
sequenceDiagram
participant Client
participant TokenService
participant RedisCache
Client->>TokenService : Request with valid token
TokenService->>TokenService : Check token expiration
alt Expiration within 20 minutes
TokenService->>RedisCache : Refresh token TTL
TokenService->>TokenService : Update loginTime and expireTime
TokenService->>RedisCache : Store updated LoginUser
end
TokenService->>Client : Process request normally
```

The refresh process occurs automatically when a request is made and the token expires within 20 minutes of the current time. This extends the token's validity without requiring the user to re-authenticate.

### Token Structure and Storage
Tokens are stored in a hybrid approach:
1. **JWT in Authorization Header**: Contains minimal user information and signature
2. **Redis Cache**: Stores complete user session data with the key pattern `login_tokens:{token}`

This approach combines the benefits of stateless JWT with the ability to invalidate tokens immediately when needed.

**Diagram sources**
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L114-L155)

**Section sources**
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L114-L155)
- [application.yml](file://src/main/resources/application.yml#L92-L98)

## Logout Functionality

The logout endpoint provides a secure way for users to terminate their sessions.

### Logout Endpoint
- **Endpoint**: `/logout`
- **Method**: POST
- **Authentication**: Required (valid token in Authorization header)

### Logout Process
```mermaid
sequenceDiagram
participant Client
participant LogoutHandler
participant TokenService
participant RedisCache
Client->>LogoutHandler : POST /logout
LogoutHandler->>TokenService : Get LoginUser from token
TokenService->>RedisCache : Retrieve LoginUser object
TokenService->>RedisCache : Delete token from cache
LogoutHandler->>AsyncManager : Record logout log
LogoutHandler->>Client : Return success response
```

### Response Format
Successful logout returns:
```json
{
  "code": 200,
  "msg": "退出成功",
  "time": "2025-05-22T10:35:00",
  "data": null
}
```

After logout, the token is invalidated by removing the corresponding session data from Redis. Any subsequent requests with the same token will be rejected.

**Diagram sources**
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L38-L53)

**Section sources**
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L38-L53)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L122-L123)

## Authentication Interception

The system uses Spring Security filters to intercept and validate authentication for protected endpoints.

### JwtAuthenticationTokenFilter
The `JwtAuthenticationTokenFilter` is responsible for validating JWT tokens on incoming requests:

```mermaid
flowchart TD
A[Incoming Request] --> B{Has Authorization Header?}
B --> |No| C[Continue to next filter]
B --> |Yes| D[Extract JWT Token]
D --> E{Token Valid?}
E --> |No| F[Return 401 Unauthorized]
E --> |Yes| G{LoginUser in Redis?}
G --> |No| H[Return 401 Unauthorized]
G --> |Yes| I[Set Authentication in SecurityContext]
I --> J[Continue to next filter]
```

### Filter Processing Steps
1. Extract the JWT token from the Authorization header
2. Parse the token to retrieve the user identifier (UUID)
3. Retrieve the complete `LoginUser` object from Redis cache
4. Validate token expiration and refresh if necessary
5. Set the authentication context for the current request
6. Continue processing the request through the filter chain

The filter only processes requests that contain a token, allowing anonymous endpoints to bypass authentication.

**Diagram sources**
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L31-L43)

**Section sources**
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L31-L43)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L124-L124)

## Anonymous Access

The system provides mechanisms to allow certain endpoints to bypass authentication checks using the `@Anonymous` annotation.

### @Anonymous Annotation
The `@Anonymous` annotation can be applied to controllers or specific methods to exclude them from authentication requirements:

```java
@Anonymous
@PostMapping("/login")
public AjaxResult login(@RequestBody LoginBody loginBody)
{
    // This method can be accessed without authentication
}
```

### Configuration and Processing
The `PermitAllUrlProperties` class automatically detects methods and controllers annotated with `@Anonymous` and configures Spring Security to permit access:

```mermaid
classDiagram
class Anonymous {
+@Target({ElementType.METHOD, ElementType.TYPE})
+@Retention(RetentionPolicy.RUNTIME)
}
class PermitAllUrlProperties {
-ApplicationContext applicationContext
-List<String> urls
+afterPropertiesSet()
+getUrls()
}
PermitAllUrlProperties --> Anonymous : detects
SecurityConfig --> PermitAllUrlProperties : uses urls
```

During application startup, the system scans all request mappings and collects URLs from methods and classes annotated with `@Anonymous`. These URLs are then configured in Spring Security to permit all access, allowing unauthenticated access to login, registration, and captcha endpoints.

**Diagram sources**
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java#L14-L19)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L27-L74)

**Section sources**
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java#L14-L19)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L27-L74)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L112-L114)

## Client-Side Token Handling

Proper client-side token management is essential for maintaining secure and seamless user sessions.

### Token Storage
The authentication token should be stored securely on the client side:

- **Recommended**: Memory storage (JavaScript variables) for single-page applications
- **Alternative**: Secure HTTP-only cookies with SameSite protection
- **Avoid**: LocalStorage or SessionStorage due to XSS vulnerability risks

### Token Usage
Include the token in the Authorization header for all authenticated requests:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.xxxxx
```

### Token Expiration Handling
Implement client-side logic to handle token expiration:

1. Monitor token expiration time
2. Attempt to refresh the token before it expires
3. Redirect to login page when token is invalid or expired
4. Handle 401 responses by clearing local token and redirecting to login

### Security Best Practices
- Set appropriate token expiration times (30 minutes recommended)
- Implement secure token refresh mechanisms
- Use HTTPS for all authentication-related endpoints
- Validate token signatures on the server side
- Implement proper error handling for authentication failures

**Section sources**
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L37-L46)
- [application.yml](file://src/main/resources/application.yml#L92-L98)

## Common Issues and Error Handling

The authentication system provides specific error responses for common authentication issues.

### Invalid Credentials
When username or password is incorrect:
- **HTTP Status**: 401 Unauthorized
- **Error Code**: user.password.not.match
- **Message**: "用户不存在/密码错误"
- **Logging**: Failed login attempt is recorded with IP address

### Expired Captcha
When the captcha has expired or is invalid:
- **HTTP Status**: 401 Unauthorized
- **Error Code**: user.jcaptcha.expire
- **Message**: "验证码已过期"
- **Duration**: Captchas expire after a configurable time period

### Rate Limiting
The system implements rate limiting for login attempts:
- **Maximum Attempts**: Configurable (default: 5 attempts)
- **Lock Duration**: Configurable (default: 10 minutes)
- **HTTP Status**: 401 Unauthorized
- **Error Code**: user.password.retry.limit.exceed
- **Message**: "错误次数过多，账户已锁定"

### IP Blacklisting
The system supports IP-based access control:
- **Configuration**: IP addresses can be added to a blacklist
- **HTTP Status**: 401 Unauthorized
- **Error Code**: login.blocked
- **Message**: "登录已被阻止"

### Other Common Errors
| Error | Status | Code | Message |
|-------|--------|------|---------|
| Empty username/password | 401 | not.null | "用户名或密码不能为空" |
| Username length invalid | 401 | user.password.not.match | "用户名长度不符合要求" |
| Password length invalid | 401 | user.password.not.match | "密码长度不符合要求" |
| Blacklisted IP | 401 | login.blocked | "登录已被阻止" |

**Section sources**
- [SysLoginService.java](file://src/main/java/com/ruoyi/framework/security/service/SysLoginService.java#L80-L89)
- [CaptchaController.java](file://src/main/java/com/ruoyi/project/common/CaptchaController.java#L50-L98)
- [application.yml](file://src/main/resources/application.yml#L40-L47)

## Security Configuration

The authentication system is configured through Spring Security with JWT-based token authentication.

### Security Configuration Overview
```mermaid
graph TB
subgraph SecurityConfig
A[CSRF Disabled]
B[Stateless Session]
C[JWT Filter]
D[CORS Filter]
E[Anonymous Access]
F[Logout Handler]
end
Client --> C
C --> D
D --> E
E --> F
F --> Backend
style A fill:#f9f,stroke:#333
style B fill:#f9f,stroke:#333
style C fill:#f9f,stroke:#333
style D fill:#f9f,stroke:#333
style E fill:#f9f,stroke:#333
style F fill:#f9f,stroke:#333
```

### Key Configuration Settings
| Setting | Value | Description |
|--------|-------|-------------|
| Session Creation | STATELESS | No server-side sessions |
| CSRF Protection | Disabled | Not needed for stateless API |
| Token Header | Authorization | HTTP header for JWT |
| Token Prefix | Bearer | Prefix for token value |
| Token Secret | Configurable | Secret key for JWT signing |
| Token Expiration | 30 minutes | Default token lifetime |
| Captcha Enabled | True | Requires captcha for login |
| Captcha Type | math | Mathematical expression captcha |

### Authentication Flow
1. Client sends credentials to `/login` endpoint
2. Server validates credentials and captcha
3. On success, generates JWT token and stores session in Redis
4. Client includes token in Authorization header for subsequent requests
5. Server validates token on each request using JwtAuthenticationTokenFilter
6. Token is automatically refreshed when nearing expiration
7. User logs out by calling `/logout`, which invalidates the session

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L97-L128)
- [application.yml](file://src/main/resources/application.yml#L92-L98)
- [CaptchaConfig.java](file://src/main/java/com/ruoyi/framework/config/CaptchaConfig.java#L18-L83)