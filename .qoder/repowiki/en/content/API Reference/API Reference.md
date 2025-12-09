# API Reference

<cite>
**Referenced Files in This Document**   
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java)
- [SysUserController.java](file://src/main/java/com/ruoyi/project/system/controller/SysUserController.java)
- [SysRoleController.java](file://src/main/java/com/ruoyi/project/system/controller/SysRoleController.java)
- [SysMenuController.java](file://src/main/java/com/ruoyi/project/system/controller/SysMenuController.java)
- [GenController.java](file://src/main/java/com/ruoyi/project/tool/gen/controller/GenController.java)
- [SysLogininforController.java](file://src/main/java/com/ruoyi/project/monitor/controller/SysLogininforController.java)
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java)
- [SwaggerConfig.java](file://src/main/java/com/ruoyi/framework/config/SwaggerConfig.java)
- [RateLimiter.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/RateLimiter.java)
- [AjaxResult.java](file://src/main/java/com/ruoyi/framework/web/domain/AjaxResult.java)
- [application.yml](file://src/main/resources/application.yml)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Authentication API](#authentication-api)
3. [User Management API](#user-management-api)
4. [Role Management API](#role-management-api)
5. [Menu Management API](#menu-management-api)
6. [Code Generation API](#code-generation-api)
7. [Monitoring API](#monitoring-api)
8. [Security Requirements](#security-requirements)
9. [Error Handling](#error-handling)
10. [Rate Limiting](#rate-limiting)
11. [API Versioning](#api-versioning)
12. [Swagger UI Usage](#swagger-ui-usage)
13. [Client Implementation Guidelines](#client-implementation-guidelines)
14. [Common Use Cases](#common-use-cases)

## Introduction

The RuoYi-Vue system provides a comprehensive RESTful API for managing users, roles, menus, and code generation. The API uses JWT tokens for authentication and authorization, with endpoints organized by functional groups. All API responses follow a consistent format using the AjaxResult class, which includes status codes, messages, and data payloads. The base URL for API endpoints is typically prefixed with `/dev-api` as configured in the application settings.

**Section sources**
- [application.yml](file://src/main/resources/application.yml#L116-L120)

## Authentication API

The authentication API handles user login and token management. It provides endpoints for user authentication, retrieving user information, and obtaining routing information for the frontend application.

### Login Endpoint
- **HTTP Method**: POST
- **URL Pattern**: `/login`
- **Parameters**:
  - `username`: User's login name
  - `password`: User's password (encrypted)
  - `code`: CAPTCHA code (if enabled)
  - `uuid`: Unique identifier for CAPTCHA validation
- **Request Schema**: LoginBody object containing authentication credentials
- **Response Schema**: AjaxResult with JWT token
- **Security Requirements**: Anonymous access allowed

### Get User Information
- **HTTP Method**: GET
- **URL Pattern**: `/getInfo`
- **Parameters**: None (uses JWT token from Authorization header)
- **Response Schema**: AjaxResult containing user details, roles, permissions, and password status
- **Security Requirements**: Valid JWT token required

### Get Routing Information
- **HTTP Method**: GET
- **URL Pattern**: `/getRouters`
- **Parameters**: None (uses JWT token from Authorization header)
- **Response Schema**: AjaxResult containing menu routing structure
- **Security Requirements**: Valid JWT token required

**Section sources**
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java#L56-L131)

## User Management API

The user management API provides comprehensive operations for managing system users, including CRUD operations, password management, and role assignment.

### Get User List
- **HTTP Method**: GET
- **URL Pattern**: `/system/user/list`
- **Parameters**: SysUser object for filtering
- **Response Schema**: TableDataInfo containing paginated list of SysUser objects
- **Security Requirements**: `system:user:list` permission required
- **Example Response**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "rows": [...],
    "total": 100
  }
}
```

### Get User Details
- **HTTP Method**: GET
- **URL Pattern**: `/system/user/{userId}` or `/system/user/`
- **Parameters**: userId (path variable, optional)
- **Response Schema**: AjaxResult containing SysUser object with associated posts and roles
- **Security Requirements**: `system:user:query` permission required

### Create User
- **HTTP Method**: POST
- **URL Pattern**: `/system/user`
- **Request Schema**: SysUser object with required fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:add` permission required
- **Validation**: Checks for unique username, phone, and email

### Update User
- **HTTP Method**: PUT
- **URL Pattern**: `/system/user`
- **Request Schema**: SysUser object with updated fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:edit` permission required

### Delete User
- **HTTP Method**: DELETE
- **URL Pattern**: `/system/user/{userIds}`
- **Parameters**: userIds (array of user IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:remove` permission required
- **Restriction**: Cannot delete currently logged-in user

### Reset Password
- **HTTP Method**: PUT
- **URL Pattern**: `/system/user/resetPwd`
- **Request Schema**: SysUser object with userId and new password
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:resetPwd` permission required

### Change User Status
- **HTTP Method**: PUT
- **URL Pattern**: `/system/user/changeStatus`
- **Request Schema**: SysUser object with userId and status
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:edit` permission required

### Assign Roles to User
- **HTTP Method**: PUT
- **URL Pattern**: `/system/user/authRole`
- **Parameters**: userId (query parameter), roleIds (array of role IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:user:edit` permission required

### Get Department Tree
- **HTTP Method**: GET
- **URL Pattern**: `/system/user/deptTree`
- **Parameters**: SysDept object for filtering
- **Response Schema**: AjaxResult containing department tree structure
- **Security Requirements**: `system:user:list` permission required

**Section sources**
- [SysUserController.java](file://src/main/java/com/ruoyi/project/system/controller/SysUserController.java#L59-L256)

## Role Management API

The role management API handles operations related to user roles, including CRUD operations, data scope management, and user-role assignments.

### Get Role List
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/list`
- **Parameters**: SysRole object for filtering
- **Response Schema**: TableDataInfo containing paginated list of SysRole objects
- **Security Requirements**: `system:role:list` permission required

### Get Role Details
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/{roleId}`
- **Parameters**: roleId (path variable)
- **Response Schema**: AjaxResult containing SysRole object
- **Security Requirements**: `system:role:query` permission required

### Create Role
- **HTTP Method**: POST
- **URL Pattern**: `/system/role`
- **Request Schema**: SysRole object with required fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:add` permission required
- **Validation**: Checks for unique role name and role key

### Update Role
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role`
- **Request Schema**: SysRole object with updated fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required

### Update Data Scope
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role/dataScope`
- **Request Schema**: SysRole object with data scope settings
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required
- **Note**: Updates user permissions cache if current user is affected

### Change Role Status
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role/changeStatus`
- **Request Schema**: SysRole object with roleId and status
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required

### Delete Role
- **HTTP Method**: DELETE
- **URL Pattern**: `/system/role/{roleIds}`
- **Parameters**: roleIds (array of role IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:remove` permission required

### Get Role Options
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/optionselect`
- **Response Schema**: AjaxResult containing list of all roles
- **Security Requirements**: `system:role:query` permission required

### Get Assigned Users
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/authUser/allocatedList`
- **Parameters**: SysUser object for filtering
- **Response Schema**: TableDataInfo containing paginated list of assigned users
- **Security Requirements**: `system:role:list` permission required

### Get Unassigned Users
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/authUser/unallocatedList`
- **Parameters**: SysUser object for filtering
- **Response Schema**: TableDataInfo containing paginated list of unassigned users
- **Security Requirements**: `system:role:list` permission required

### Revoke User Assignment
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role/authUser/cancel`
- **Request Schema**: SysUserRole object with userId and roleId
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required

### Batch Revoke User Assignments
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role/authUser/cancelAll`
- **Parameters**: roleId (query parameter), userIds (array of user IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required

### Batch Assign Users
- **HTTP Method**: PUT
- **URL Pattern**: `/system/role/authUser/selectAll`
- **Parameters**: roleId (query parameter), userIds (array of user IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:role:edit` permission required

### Get Department Tree for Role
- **HTTP Method**: GET
- **URL Pattern**: `/system/role/deptTree/{roleId}`
- **Parameters**: roleId (path variable)
- **Response Schema**: AjaxResult containing department tree with checked keys
- **Security Requirements**: `system:role:query` permission required

**Section sources**
- [SysRoleController.java](file://src/main/java/com/ruoyi/project/system/controller/SysRoleController.java#L58-L262)

## Menu Management API

The menu management API provides operations for managing system menu structures and navigation.

### Get Menu List
- **HTTP Method**: GET
- **URL Pattern**: `/system/menu/list`
- **Parameters**: SysMenu object for filtering
- **Response Schema**: AjaxResult containing list of menu items
- **Security Requirements**: `system:menu:list` permission required

### Get Menu Details
- **HTTP Method**: GET
- **URL Pattern**: `/system/menu/{menuId}`
- **Parameters**: menuId (path variable)
- **Response Schema**: AjaxResult containing SysMenu object
- **Security Requirements**: `system:menu:query` permission required

### Get Menu Tree Select
- **HTTP Method**: GET
- **URL Pattern**: `/system/menu/treeselect`
- **Parameters**: SysMenu object for filtering
- **Response Schema**: AjaxResult containing hierarchical menu tree
- **Security Requirements**: Anonymous access allowed

### Get Role Menu Tree Select
- **HTTP Method**: GET
- **URL Pattern**: `/system/menu/roleMenuTreeselect/{roleId}`
- **Parameters**: roleId (path variable)
- **Response Schema**: AjaxResult containing menu tree with checked keys
- **Security Requirements**: Anonymous access allowed

### Create Menu
- **HTTP Method**: POST
- **URL Pattern**: `/system/menu`
- **Request Schema**: SysMenu object with required fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:menu:add` permission required
- **Validation**: Checks for unique menu name and valid external URL format

### Update Menu
- **HTTP Method**: PUT
- **URL Pattern**: `/system/menu`
- **Request Schema**: SysMenu object with updated fields
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:menu:edit` permission required
- **Validation**: Prevents parent-child circular references

### Delete Menu
- **HTTP Method**: DELETE
- **URL Pattern**: `/system/menu/{menuId}`
- **Parameters**: menuId (path variable)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `system:menu:remove` permission required
- **Restrictions**: Cannot delete menu with child menus or assigned roles

**Section sources**
- [SysMenuController.java](file://src/main/java/com/ruoyi/project/system/controller/SysMenuController.java#L39-L141)

## Code Generation API

The code generation API provides functionality for generating code from database tables.

### Get Generation List
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/list`
- **Parameters**: GenTable object for filtering
- **Response Schema**: TableDataInfo containing paginated list of GenTable objects
- **Security Requirements**: `tool:gen:list` permission required

### Get Generation Details
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/{talbleId}`
- **Parameters**: talbleId (path variable)
- **Response Schema**: AjaxResult containing table info, columns, and related tables
- **Security Requirements**: `tool:gen:query` permission required

### Get Database List
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/db/list`
- **Parameters**: GenTable object for filtering
- **Response Schema**: TableDataInfo containing list of database tables
- **Security Requirements**: `tool:gen:list` permission required

### Get Column List
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/column/{tableId}`
- **Parameters**: tableId (path variable)
- **Response Schema**: TableDataInfo containing list of table columns
- **Security Requirements**: `tool:gen:list` permission required

### Import Table Structure
- **HTTP Method**: POST
- **URL Pattern**: `/tool/gen/importTable`
- **Parameters**: tables (comma-separated table names)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `tool:gen:import` permission required

### Create Table Structure
- **HTTP Method**: POST
- **URL Pattern**: `/tool/gen/createTable`
- **Parameters**: sql (SQL CREATE TABLE statement)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: Admin role required
- **Note**: Creates table and imports it into code generation

### Update Generation Configuration
- **HTTP Method**: PUT
- **URL Pattern**: `/tool/gen`
- **Request Schema**: GenTable object with updated configuration
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `tool:gen:edit` permission required

### Delete Generation
- **HTTP Method**: DELETE
- **URL Pattern**: `/tool/gen/{tableIds}`
- **Parameters**: tableIds (array of table IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `tool:gen:remove` permission required

### Preview Generated Code
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/preview/{tableId}`
- **Parameters**: tableId (path variable)
- **Response Schema**: AjaxResult containing map of file names to generated code
- **Security Requirements**: `tool:gen:preview` permission required

### Download Generated Code
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/download/{tableName}`
- **Parameters**: tableName (path variable)
- **Response**: ZIP file containing generated code
- **Security Requirements**: `tool:gen:code` permission required

### Generate Code to Local
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/genCode/{tableName}`
- **Parameters**: tableName (path variable)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `tool:gen:code` permission required
- **Restriction**: Only allowed if `gen.allowOverwrite` is true in configuration

### Synchronize Database
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/synchDb/{tableName}`
- **Parameters**: tableName (path variable)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `tool:gen:edit` permission required

### Batch Generate Code
- **HTTP Method**: GET
- **URL Pattern**: `/tool/gen/batchGenCode`
- **Parameters**: tables (comma-separated table names)
- **Response**: ZIP file containing generated code for multiple tables
- **Security Requirements**: `tool:gen:code` permission required

**Section sources**
- [GenController.java](file://src/main/java/com/ruoyi/project/tool/gen/controller/GenController.java#L57-L262)

## Monitoring API

The monitoring API provides access to system monitoring data and log information.

### Get Login Log List
- **HTTP Method**: GET
- **URL Pattern**: `/monitor/logininfor/list`
- **Parameters**: SysLogininfor object for filtering
- **Response Schema**: TableDataInfo containing paginated list of login logs
- **Security Requirements**: `monitor:logininfor:list` permission required

### Export Login Logs
- **HTTP Method**: POST
- **URL Pattern**: `/monitor/logininfor/export`
- **Parameters**: SysLogininfor object for filtering
- **Response**: Excel file containing login logs
- **Security Requirements**: `monitor:logininfor:export` permission required

### Delete Login Logs
- **HTTP Method**: DELETE
- **URL Pattern**: `/monitor/logininfor/{infoIds}`
- **Parameters**: infoIds (array of log IDs)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `monitor:logininfor:remove` permission required

### Clear Login Logs
- **HTTP Method**: DELETE
- **URL Pattern**: `/monitor/logininfor/clean`
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `monitor:logininfor:remove` permission required

### Unlock User Account
- **HTTP Method**: GET
- **URL Pattern**: `/monitor/logininfor/unlock/{userName}`
- **Parameters**: userName (path variable)
- **Response Schema**: AjaxResult with operation status
- **Security Requirements**: `monitor:logininfor:unlock` permission required
- **Note**: Clears login attempt records for the user

**Section sources**
- [SysLogininforController.java](file://src/main/java/com/ruoyi/project/monitor/controller/SysLogininforController.java#L38-L82)

## Security Requirements

The RuoYi-Vue API implements a comprehensive security model using JWT tokens and role-based access control.

### Authentication Method
The system uses JWT (JSON Web Token) for authentication. Upon successful login, the server returns a JWT token that must be included in the Authorization header for subsequent requests.

```http
Authorization: Bearer <token>
```

The token contains user information and permissions, which are validated on each request by the JwtAuthenticationTokenFilter.

### Authorization Model
The system implements role-based access control using Spring Security's @PreAuthorize annotation. Permissions are defined using the format `system:<module>:<action>`.

Common permission patterns:
- `system:user:list` - View user list
- `system:user:add` - Create new users
- `system:user:edit` - Modify user information
- `system:user:remove` - Delete users
- `system:role:*` - Role management permissions
- `system:menu:*` - Menu management permissions
- `tool:gen:*` - Code generation permissions
- `monitor:logininfor:*` - Monitoring permissions

### Security Configuration
The SecurityConfig class configures the security framework with the following settings:
- Stateless authentication (no session)
- JWT token-based authentication
- CSRF protection disabled (stateless API)
- Anonymous access to login, registration, and CAPTCHA endpoints
- Swagger UI endpoints accessible without authentication
- All other endpoints require authentication

**Section sources**
- [SecurityConfig.java](file://src/main/java/com/ruoyi/framework/config/SecurityConfig.java#L29-L139)
- [JwtAuthenticationTokenFilter.java](file://src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java#L24-L44)
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java#L56-L64)

## Error Handling

The API uses a consistent error handling strategy with standardized response formats.

### Response Format
All API responses use the AjaxResult class, which has the following structure:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { /* optional data */ }
}
```

### Status Codes
The system defines the following status codes in HttpStatus.java:

- **200**: SUCCESS - Operation completed successfully
- **500**: ERROR - General error occurred
- **401**: UNAUTHORIZED - Authentication failed or token expired
- **403**: FORBIDDEN - Insufficient permissions
- **301**: WARN - Warning message (operation may have limitations)

### Error Response Examples

Authentication failure:
```json
{
  "code": 401,
  "msg": "登录失败，用户名或密码错误"
}
```

Permission denied:
```json
{
  "code": 403,
  "msg": "没有权限访问该资源"
}
```

Validation error:
```json
{
  "code": 500,
  "msg": "新增用户'admin'失败，登录账号已存在"
}
```

### Exception Handling
The GlobalExceptionHandler class intercepts exceptions and converts them to appropriate AjaxResult responses. Common exceptions include:
- UserPasswordNotMatchException: Invalid credentials
- CaptchaException: Invalid CAPTCHA
- UserNotExistsException: User not found
- ServiceException: General service exception

**Section sources**
- [AjaxResult.java](file://src/main/java/com/ruoyi/framework/web/domain/AjaxResult.java#L13-L216)
- [SysUserController.java](file://src/main/java/com/ruoyi/project/system/controller/SysUserController.java#L131-L140)
- [SysLoginService.java](file://src/main/java/com/ruoyi/framework/security/service/SysLoginService.java#L80-L89)

## Rate Limiting

The API implements rate limiting to prevent abuse and protect system resources.

### Rate Limiting Configuration
Rate limiting is implemented using the @RateLimiter annotation and Redis-based counting. The RateLimiterAspect handles the rate limiting logic.

```java
@RateLimiter(time = 60, count = 100, limitType = LimitType.DEFAULT)
```

### Parameters
- **time**: Time window in seconds (default: 60)
- **count**: Maximum number of requests allowed in the time window (default: 100)
- **limitType**: Limiting strategy (DEFAULT or IP)

### Limiting Strategies
- **DEFAULT**: Global rate limiting for the endpoint
- **IP**: Rate limiting based on client IP address

### Implementation
The rate limiting uses Redis with a Lua script to atomically increment and check request counts. When the limit is exceeded, a ServiceException is thrown with the message "访问过于频繁，请稍候再试".

### Example Usage
```java
@RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
@PostMapping("/login")
public AjaxResult login(@RequestBody LoginBody loginBody)
```

This configuration limits login attempts to 10 per minute per IP address.

**Section sources**
- [RateLimiter.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/annotation/RateLimiter.java#L16-L40)
- [RateLimiterAspect.java](file://src/main/java/com/ruoyi/framework/aspectj/RateLimiterAspect.java#L27-L89)
- [LimitType.java](file://src/main/java/com/ruoyi/framework/aspectj/lang/enums/LimitType.java#L9-L20)

## API Versioning

The RuoYi-Vue API does not implement explicit versioning in the URL structure. Instead, it uses a stable API design with backward compatibility considerations.

### Current Approach
- No version prefix in API URLs (e.g., `/api/v1/`)
- Relies on careful backward-compatible changes
- Configuration-driven behavior changes
- Deprecation warnings in documentation

### Version Information
The system version is available in the application configuration and exposed through the Swagger documentation. The current version can be found in the application.yml file.

```yaml
ruoyi:
  version: 3.9.0
```

### Recommendations for Clients
- Monitor release notes for breaking changes
- Implement defensive coding for optional fields
- Use feature detection rather than version detection
- Test thoroughly after system updates

Future versions may implement proper API versioning to better support client applications.

**Section sources**
- [application.yml](file://src/main/resources/application.yml#L6-L7)
- [SwaggerConfig.java](file://src/main/java/com/ruoyi/framework/config/SwaggerConfig.java#L110-L122)

## Swagger UI Usage

The system provides Swagger UI for API exploration and testing.

### Accessing Swagger UI
The Swagger UI is available at:
```
http://<server>:<port>/swagger-ui.html
```

Due to the path mapping configuration, API requests should be prefixed with `/dev-api`.

### Configuration
The SwaggerConfig class configures the Swagger documentation with the following settings:
- Enabled by default (configurable via `swagger.enabled`)
- Path mapping set to `/dev-api`
- Documentation type: OpenAPI 3.0
- Security scheme for JWT token authentication
- Automatic scanning of @ApiOperation-annotated methods

### Authentication in Swagger
To test authenticated endpoints:
1. Click "Authorize" button
2. Enter the JWT token in the format: `Bearer <token>`
3. The token will be included in the Authorization header for all subsequent requests

### Documentation Features
- Interactive API explorer
- Request/response model definitions
- Example values for parameters
- HTTP status code descriptions
- Authentication requirements
- Request/response examples

### Testing APIs
Swagger UI allows direct testing of API endpoints:
- Set request parameters and body content
- View cURL command equivalent
- See raw HTTP request and response
- Test authentication flows

**Section sources**
- [SwaggerConfig.java](file://src/main/java/com/ruoyi/framework/config/SwaggerConfig.java#L29-L124)
- [application.yml](file://src/main/resources/application.yml#L116-L120)

## Client Implementation Guidelines

### Authentication Flow
1. Send POST request to `/login` with credentials
2. Extract JWT token from response
3. Include token in Authorization header for subsequent requests
4. Handle 401 responses by re-authenticating
5. Refresh token before expiration (default: 30 minutes)

### Error Handling
Implement robust error handling:
- Check response status code
- Parse error messages for user feedback
- Handle specific error types appropriately
- Implement retry logic for transient failures
- Log errors for debugging

### Pagination
Use the TableDataInfo response format for paginated data:
- Check total count for navigation
- Use consistent page size
- Implement loading states
- Handle empty results gracefully

### Caching
Consider client-side caching for:
- User and role information
- Menu structures
- Configuration data
- Implement cache invalidation on updates

### Security Best Practices
- Store tokens securely (not in localStorage for web)
- Implement token refresh mechanisms
- Validate SSL certificates
- Sanitize user inputs
- Use HTTPS in production

### Performance Optimization
- Batch requests when possible
- Use filtering parameters to reduce payload size
- Implement lazy loading for large datasets
- Cache responses appropriately
- Minimize unnecessary API calls

**Section sources**
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java#L56-L64)
- [AjaxResult.java](file://src/main/java/com/ruoyi/framework/web/domain/AjaxResult.java#L13-L216)
- [TableDataInfo.java](file://src/main/java/com/ruoyi/framework/web/page/TableDataInfo.java)

## Common Use Cases

### User Management Workflow
1. **List Users**: GET `/system/user/list` with pagination
2. **View User**: GET `/system/user/{id}` to get detailed information
3. **Edit User**: PUT `/system/user` with updated user data
4. **Assign Roles**: PUT `/system/user/authRole` with userId and roleIds
5. **Update Status**: PUT `/system/user/changeStatus` to enable/disable user

### Role-Based Access Control Setup
1. **Create Role**: POST `/system/role` with role details
2. **Set Data Scope**: PUT `/system/role/dataScope` to define data access
3. **Assign to Users**: PUT `/system/role/authUser/selectAll` to assign role to users
4. **Verify Permissions**: GET `/system/user/getInfo` to confirm permission changes

### Code Generation Process
1. **Import Table**: POST `/tool/gen/importTable` with table names
2. **Configure Generation**: PUT `/tool/gen` to set code generation options
3. **Preview Code**: GET `/tool/gen/preview/{id}` to review generated code
4. **Generate Code**: GET `/tool/gen/download/{name}` to download as ZIP
5. **Synchronize**: GET `/tool/gen/synchDb/{name}` after database changes

### System Monitoring
1. **View Login Logs**: GET `/monitor/logininfor/list` to monitor access
2. **Export Data**: POST `/monitor/logininfor/export` for analysis
3. **Clean Logs**: DELETE `/monitor/logininfor/clean` to manage storage
4. **Unlock Accounts**: GET `/monitor/logininfor/unlock/{user}` for locked accounts

### Authentication Integration
1. **Login**: POST `/login` with credentials to obtain token
2. **Get User Info**: GET `/getInfo` to retrieve user details and permissions
3. **Get Routers**: GET `/getRouters` to obtain navigation structure
4. **Handle Expiration**: Catch 401 responses and re-authenticate

### Batch Operations
- **Delete Multiple Users**: DELETE `/system/user/{ids}` with array of IDs
- **Batch Assign Roles**: PUT `/system/role/authUser/selectAll` with multiple userIds
- **Export Multiple Tables**: GET `/tool/gen/batchGenCode` with multiple table names

**Section sources**
- [SysUserController.java](file://src/main/java/com/ruoyi/project/system/controller/SysUserController.java)
- [SysRoleController.java](file://src/main/java/com/ruoyi/project/system/controller/SysRoleController.java)
- [GenController.java](file://src/main/java/com/ruoyi/project/tool/gen/controller/GenController.java)
- [SysLogininforController.java](file://src/main/java/com/ruoyi/project/monitor/controller/SysLogininforController.java)
- [SysLoginController.java](file://src/main/java/com/ruoyi/project/system/controller/SysLoginController.java)