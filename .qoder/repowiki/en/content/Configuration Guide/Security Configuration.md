# Security Configuration

<cite>
**Referenced Files in This Document**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java)
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java)
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java)
- [application.yml](file://src/main/resources/application.yml)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction
This section documents the security configuration of RuoYi-Vue, focusing on the Spring Security setup. It explains how method-level security is enabled, how authentication is configured using DaoAuthenticationProvider and UserDetailsService, how JWT authentication integrates into the filter chain, and how authorization rules are enforced. It also covers the authentication entry point, logout handler, session management policy, CORS integration, password hashing with BCrypt, and the dynamic permit-all URL mechanism driven by annotations.

## Project Structure
Security-related components are organized under framework packages:
- Configuration: SecurityConfig, PermitAllUrlProperties
- Filters: JwtAuthenticationTokenFilter
- Handlers: AuthenticationEntryPointImpl, LogoutSuccessHandlerImpl
- Services: TokenService, UserDetailsServiceImpl
- Supporting model: LoginUser
- Annotation: Anonymous

```mermaid
graph TB
subgraph "Security Config"
SC["SecurityConfig.java"]
PA["PermitAllUrlProperties.java"]
end
subgraph "Filters"
JATF["JwtAuthenticationTokenFilter.java"]
end
subgraph "Handlers"
AEPI["AuthenticationEntryPointImpl.java"]
LSHI["LogoutSuccessHandlerImpl.java"]
end
subgraph "Services"
TKS["TokenService.java"]
UDSI["UserDetailsServiceImpl.java"]
end
subgraph "Model"
LU["LoginUser.java"]
end
subgraph "Annotation"
AN["Anonymous.java"]
end
subgraph "Config"
YML["application.yml"]
end
SC --> JATF
SC --> AEPI
SC --> LSHI
SC --> PA
SC --> UDSI
JATF --> TKS
TKS --> LU
PA --> AN
SC --> YML
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L1-L139)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java#L1-L267)
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java#L1-L20)
- [application.yml](file://src/main/resources/application.yml#L91-L100)

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L1-L139)

## Core Components
- Method-level security: Enabled via @EnableMethodSecurity with prePostEnabled and securedEnabled.
- AuthenticationManager: Configured using DaoAuthenticationProvider with UserDetailsService and BCryptPasswordEncoder.
- JWT filter: JwtAuthenticationTokenFilter extracts and validates tokens, sets Authentication in SecurityContext.
- Entry point and logout handler: AuthenticationEntryPointImpl and LogoutSuccessHandlerImpl provide standardized responses for authentication failures and successful logout.
- Session management: Stateless policy to support token-based authentication.
- Authorization rules: Permit-all for login/register/captcha and static resources; all other requests require authentication.
- CORS integration: CorsFilter registered and placed before JWT and Logout filters.
- Password hashing: BCryptPasswordEncoder bean configured for strong hashing.
- Permit-all URLs: Dynamic permit-all list built from @Anonymous annotations plus explicit endpoints.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L29-L30)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)
- [application.yml](file://src/main/resources/application.yml#L91-L100)

## Architecture Overview
The security architecture enforces token-based authentication with a clear filter chain order and centralized authorization rules.

```mermaid
graph TB
Client["Client"]
CORS["CorsFilter"]
JWT["JwtAuthenticationTokenFilter"]
SEC["SecurityFilterChain"]
AUTH_EP["AuthenticationEntryPointImpl"]
LOGOUT_H["LogoutSuccessHandlerImpl"]
AUTH_MGR["AuthenticationManager"]
DAO_AP["DaoAuthenticationProvider"]
UDS["UserDetailsService"]
PASS_ENC["BCryptPasswordEncoder"]
TOK["TokenService"]
LU["LoginUser"]
Client --> CORS
CORS --> JWT
JWT --> SEC
SEC --> AUTH_EP
SEC --> LOGOUT_H
SEC --> AUTH_MGR
AUTH_MGR --> DAO_AP
DAO_AP --> UDS
DAO_AP --> PASS_ENC
JWT --> TOK
TOK --> LU
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java#L1-L267)

## Detailed Component Analysis

### SecurityConfig: Spring Security Setup
- Enables method-level security with @EnableMethodSecurity.
- Configures AuthenticationManager using DaoAuthenticationProvider with UserDetailsService and BCryptPasswordEncoder.
- Defines SecurityFilterChain with:
  - CSRF disabled (stateless token-based).
  - Headers customization (cache control and frame options).
  - Exception handling via AuthenticationEntryPointImpl.
  - Session management set to STATELESS.
  - Authorization rules:
    - Permit-all for dynamic URLs from PermitAllUrlProperties.
    - Explicit permit-all for /login, /register, /captchaImage.
    - Permit-all for static resources and documentation endpoints.
    - All other requests require authentication.
  - Logout configured with LogoutSuccessHandlerImpl.
  - Adds JwtAuthenticationTokenFilter before UsernamePasswordAuthenticationFilter.
  - Adds CorsFilter before JwtAuthenticationTokenFilter and LogoutFilter.
- Provides BCryptPasswordEncoder bean.

```mermaid
flowchart TD
Start(["SecurityFilterChain build"]) --> DisableCSRF["Disable CSRF"]
DisableCSRF --> Headers["Configure headers<br/>cache-control and frame-options"]
Headers --> ExceptionHandling["Set AuthenticationEntryPointImpl"]
ExceptionHandling --> SessionStateless["Set session policy STATELESS"]
SessionStateless --> Authorize["AuthorizeHttpRequests"]
Authorize --> PermitAllDynamic["PermitAll dynamic URLs"]
PermitAllDynamic --> PermitAllExplicit["PermitAll /login, /register, /captchaImage"]
PermitAllExplicit --> StaticDocs["PermitAll static and docs"]
StaticDocs --> AnyOther["Any other request authenticated"]
AnyOther --> Logout["Configure logout with LogoutSuccessHandlerImpl"]
Logout --> AddJWT["Add JwtAuthenticationTokenFilter before UsernamePasswordAuthenticationFilter"]
AddJWT --> AddCORS1["Add CorsFilter before JwtAuthenticationTokenFilter"]
AddCORS1 --> AddCORS2["Add CorsFilter before LogoutFilter"]
AddCORS2 --> BuildEnd(["Build SecurityFilterChain"])
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L29-L30)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L131-L139)

### AuthenticationManager and DaoAuthenticationProvider
- AuthenticationManager is created with ProviderManager and configured with DaoAuthenticationProvider.
- DaoAuthenticationProvider uses:
  - UserDetailsService for loading user details.
  - BCryptPasswordEncoder for password encoding and verification.

```mermaid
classDiagram
class SecurityConfig {
+authenticationManager() AuthenticationManager
+bCryptPasswordEncoder() BCryptPasswordEncoder
}
class DaoAuthenticationProvider {
+setUserDetailsService(UserDetailsService)
+setPasswordEncoder(PasswordEncoder)
}
class ProviderManager {
+AuthenticationManager
}
class UserDetailsService
class BCryptPasswordEncoder
SecurityConfig --> ProviderManager : "creates"
ProviderManager --> DaoAuthenticationProvider : "uses"
DaoAuthenticationProvider --> UserDetailsService : "loads user"
DaoAuthenticationProvider --> BCryptPasswordEncoder : "encodes/verifies"
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L131-L139)

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L131-L139)

### JWT Authentication Flow with JwtAuthenticationTokenFilter
- JwtAuthenticationTokenFilter:
  - Extracts token from request using TokenService.
  - Verifies token validity via TokenService.
  - Creates UsernamePasswordAuthenticationToken with LoginUser principal and authorities.
  - Sets Authentication in SecurityContext if absent.
  - Continues filter chain.

```mermaid
sequenceDiagram
participant C as "Client"
participant F as "JwtAuthenticationTokenFilter"
participant T as "TokenService"
participant S as "SecurityContext"
participant N as "Next Filter"
C->>F : Request with Authorization header
F->>T : getLoginUser(request)
T-->>F : LoginUser or null
alt LoginUser present and no existing auth
F->>T : verifyToken(loginUser)
F->>S : setAuthentication(token)
end
F->>N : continue filter chain
```

**Diagram sources**
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java#L1-L267)

**Section sources**
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java#L1-L267)

### Authentication Entry Point and Logout Handler
- AuthenticationEntryPointImpl:
  - Handles authentication failure by writing a JSON error response with UNAUTHORIZED status.
- LogoutSuccessHandlerImpl:
  - On logout, retrieves LoginUser from TokenService, deletes cached token, records logout event asynchronously, and returns success JSON.

```mermaid
sequenceDiagram
participant C as "Client"
participant SEC as "SecurityFilterChain"
participant EP as "AuthenticationEntryPointImpl"
participant L as "LogoutSuccessHandlerImpl"
participant T as "TokenService"
C->>SEC : Unauthorized request
SEC->>EP : commence(...)
EP-->>C : JSON error response
C->>SEC : POST /logout
SEC->>L : onLogoutSuccess(...)
L->>T : getLoginUser(request)
L->>T : delLoginUser(token)
L-->>C : JSON success response
```

**Diagram sources**
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)

**Section sources**
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)

### Session Management Policy
- SessionCreationPolicy is set to STATELESS to enforce token-based authentication without server-side sessions.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L108-L109)

### Authorization Rules in SecurityFilterChain
- Dynamic permit-all URLs from PermitAllUrlProperties.
- Explicit permit-all for:
  - /login
  - /register
  - /captchaImage
- Permit-all for static resources and documentation endpoints.
- All other requests require authentication.

```mermaid
flowchart TD
A["Request received"] --> B{"Matches dynamic permit-all?"}
B --> |Yes| Allow1["Allow"]
B --> |No| C{"Matches /login, /register, /captchaImage?"}
C --> |Yes| Allow2["Allow"]
C --> |No| D{"Matches static/docs?"}
D --> |Yes| Allow3["Allow"]
D --> |No| Auth["Require authentication"]
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L111-L119)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L111-L119)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)

### CORS Filter Integration
- CorsFilter is injected and added to the filter chain:
  - Before JwtAuthenticationTokenFilter
  - Before LogoutFilter
- This ensures cross-origin requests are handled prior to authentication and logout processing.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L123-L128)

### BCryptPasswordEncoder Configuration
- A BCryptPasswordEncoder bean is provided for secure password hashing and verification.
- Used by DaoAuthenticationProvider in AuthenticationManager.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L131-L139)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L72-L79)

### PermitAllUrlProperties and @Anonymous Annotation
- PermitAllUrlProperties scans @Anonymous at method and type levels and builds a list of ant-style patterns with path variables replaced by wildcards.
- These URLs are dynamically added to the SecurityFilterChain’s permit-all rules.

```mermaid
classDiagram
class PermitAllUrlProperties {
+getUrls() String[]
+setUrls(String[])
+afterPropertiesSet()
}
class Anonymous {
<<annotation>>
}
class SecurityConfig {
+filterChain(HttpSecurity)
}
PermitAllUrlProperties --> Anonymous : "scans"
SecurityConfig --> PermitAllUrlProperties : "injects"
```

**Diagram sources**
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java#L1-L20)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L111-L113)

**Section sources**
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)
- [Anonymous.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/Anonymous.java#L1-L20)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L111-L113)

### Extending Security Configuration for Custom Authentication Requirements
To add custom authentication requirements:
- Add new permit-all endpoints by updating the SecurityFilterChain authorizeHttpRequests section.
- Introduce additional filters by adding them before or after existing filters in the chain.
- Extend UserDetailsService to integrate custom user loading logic.
- Customize TokenService for token creation/verification policies.
- Use @Anonymous on controllers or methods to expose endpoints without authentication.

Example extension points:
- Adding new permit-all endpoints in SecurityFilterChain.
- Placing additional filters before JwtAuthenticationTokenFilter.
- Implementing custom UserDetailsService logic in UserDetailsServiceImpl.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)

## Dependency Analysis
SecurityConfig orchestrates the security stack and depends on:
- JwtAuthenticationTokenFilter for token extraction and validation.
- AuthenticationEntryPointImpl and LogoutSuccessHandlerImpl for standardized responses.
- PermitAllUrlProperties for dynamic permit-all URLs.
- UserDetailsService and BCryptPasswordEncoder for authentication.
- TokenService for token retrieval and verification.

```mermaid
graph TB
SC["SecurityConfig"]
JATF["JwtAuthenticationTokenFilter"]
AEPI["AuthenticationEntryPointImpl"]
LSHI["LogoutSuccessHandlerImpl"]
PA["PermitAllUrlProperties"]
UDS["UserDetailsServiceImpl"]
PASS["BCryptPasswordEncoder"]
TKS["TokenService"]
SC --> JATF
SC --> AEPI
SC --> LSHI
SC --> PA
SC --> UDS
SC --> PASS
JATF --> TKS
TKS --> LU["LoginUser"]
```

**Diagram sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L1-L139)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L1-L45)
- [AuthenticationEntryPointImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/AuthenticationEntryPointImpl.java#L1-L35)
- [LogoutSuccessHandlerImpl.java](file://src/main/java/com/ruoyi/framework/security/handle/LogoutSuccessHandlerImpl.java#L1-L53)
- [PermitAllUrlProperties.java](file://src/main/java/com/ruoyi/framework/config/properties/PermitAllUrlProperties.java#L1-L74)
- [UserDetailsServiceImpl.java](file://src/main/java/com/ruoyi/framework/security/service/UserDetailsServiceImpl.java#L1-L66)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [LoginUser.java](file://src/main/java/com/ruoyi/framework/security/LoginUser.java#L1-L267)

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L1-L139)

## Performance Considerations
- Stateless session management reduces server memory overhead.
- Token verification occurs per request; caching user details in Redis minimizes database lookups.
- Avoid excessive static resource exposure; keep permit-all lists minimal.
- Ensure CORS configuration aligns with frontend origin to prevent preflight overhead.

## Troubleshooting Guide
Common issues and resolutions:
- Authentication failures:
  - Verify Authorization header format and token presence.
  - Confirm token secret and header name match application.yml configuration.
- Logout not clearing token:
  - Ensure TokenService.delLoginUser is invoked and Redis cache is reachable.
- CORS errors:
  - Confirm CorsFilter is placed before JWT and Logout filters.
  - Validate allowed origins and headers at the application level.
- Excessive permit-all endpoints:
  - Review PermitAllUrlProperties and @Anonymous usage to avoid unintended exposure.

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L96-L129)
- [TokenService.java](file://src/main/java/com/ruoyi/framework/security/service/TokenService.java#L1-L233)
- [application.yml](file://src/main/resources/application.yml#L91-L100)

## Conclusion
RuoYi-Vue’s security configuration establishes a robust, stateless, token-based authentication system. It leverages method-level security, a custom UserDetailsService, and a dedicated JWT filter to validate tokens and populate SecurityContext. Authorization rules are centralized and extensible, with dynamic permit-all support via annotations. CORS is integrated early in the chain, and authentication/logout handlers provide consistent responses. The configuration is production-ready with strong password hashing and stateless session management.