# System Management Tables

<cite>
**Referenced Files in This Document**   
- [ry_20250522.sql](file://sql/ry_20250522.sql)
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java)
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java)
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java)
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java)
- [SysUserMapper.xml](file://src/main/resources/mybatis/system/SysUserMapper.xml)
- [SysRoleMapper.xml](file://src/main/resources/mybatis/system/SysRoleMapper.xml)
- [SysMenuMapper.xml](file://src/main/resources/mybatis/system/SysMenuMapper.xml)
- [SysDeptMapper.xml](file://src/main/resources/mybatis/system/SysDeptMapper.xml)
- [SysConfigMapper.xml](file://src/main/resources/mybatis/system/SysConfigMapper.xml)
- [SysDictTypeMapper.xml](file://src/main/resources/mybatis/system/SysDictTypeMapper.xml)
- [SysDictDataMapper.xml](file://src/main/resources/mybatis/system/SysDictDataMapper.xml)
</cite>

## Table of Contents
1. [sys_user Table](#sys_user-table)
2. [sys_role Table](#sys_role-table)
3. [sys_menu Table](#sys_menu-table)
4. [sys_dept Table](#sys_dept-table)
5. [sys_config Table](#sys_config-table)
6. [sys_dict_type and sys_dict_data Tables](#sys_dict_type-and-sys_dict_data-tables)
7. [Mapping Tables](#mapping-tables)
8. [MyBatis Mapper Interactions](#mybatis-mapper-interactions)
9. [System Integration and Business Context](#system-integration-and-business-context)

## sys_user Table

The `sys_user` table is the central entity for user management in the RuoYi-Vue system, storing all information related to system users. It serves as the foundation for authentication, authorization, and user profile management.

**Key Attributes:**
- **user_id**: Primary key, auto-incrementing unique identifier for the user.
- **dept_id**: Foreign key referencing `sys_dept.dept_id`, establishing the user's departmental affiliation.
- **user_name**: Unique user account name used for login, with constraints to prevent duplicates.
- **nick_name**: Display name for the user within the system.
- **password**: Stores the user's password in an encrypted format (bcrypt, indicated by the `$2a$` prefix in initialization data).
- **status**: Indicates the account status (0 for normal, 1 for disabled).
- **login_ip** and **login_date**: Track the user's last login activity for security and auditing purposes.

The table includes comprehensive metadata such as creation and update timestamps, responsible parties, and a remark field for additional notes. The `del_flag` column implements soft deletion, marking records as deleted (value '2') rather than removing them physically.

**Initialization Data:**
The system is initialized with two users:
- A super administrator (`user_id=1`) with the username `admin`, belonging to the R&D department (`dept_id=103`).
- A test user (`user_id=2`) with the username `ry`, belonging to the Testing department (`dept_id=105`).

The `SysUser` Java domain class (file://src/main/java/com/ruoyi/project/system/domain/SysUser.java) extends `BaseEntity` and includes validation annotations (e.g., `@NotBlank`, `@Email`) to enforce data integrity. It also contains utility methods like `isAdmin()` to check if a user is the super administrator (user_id = 1).

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L41-L64)
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java#L21-L341)

## sys_role Table

The `sys_role` table defines the roles within the system, which are essential for implementing role-based access control (RBAC). Roles act as containers for permissions and are assigned to users to grant them specific capabilities.

**Key Attributes:**
- **role_id**: Primary key, auto-incrementing unique identifier for the role.
- **role_name**: Descriptive name of the role (e.g., "Super Administrator", "Normal Role").
- **role_key**: A unique permission string that identifies the role programmatically. This is used in code for permission checks.
- **data_scope**: A critical field that defines the data access scope for users with this role:
  - `1`: All data permissions.
  - `2`: Custom data permissions (defined via `sys_role_dept`).
  - `3`: Data permissions for the user's own department.
  - `4`: Data permissions for the user's department and its sub-departments.
- **menu_check_strictly** and **dept_check_strictly**: Boolean flags that control the behavior of the menu and department selection trees in the UI.

**Initialization Data:**
Two roles are initialized:
- The **Super Administrator** (`role_id=1`, `role_key=admin`) with full system access.
- A **Normal Role** (`role_id=2`, `role_key=common`) with standard permissions.

The `SysRole` Java class (file://src/main/java/com/ruoyi/project/system/domain/SysRole.java) includes a `permissions` field of type `Set<String>` which is populated at runtime with the permissions associated with the role's menus. The `isAdmin()` method allows for quick checks to determine if a role is the super administrator.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L104-L121)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java#L18-L242)

## sys_menu Table

The `sys_menu` table defines the navigation structure and functional permissions of the application. It represents a hierarchical tree of menus, sub-menus, and buttons, forming the backbone of the system's user interface and security model.

**Key Attributes:**
- **menu_id**: Primary key, auto-incrementing unique identifier.
- **parent_id**: References `menu_id` of the parent menu, enabling the creation of a hierarchical tree (e.g., "System Management" is a parent of "User Management").
- **menu_name**: The display name of the menu item.
- **path** and **component**: Define the Vue.js routing configuration, mapping the menu to a specific frontend page.
- **perms**: The permission string (e.g., `system:user:list`) that is checked by the backend to authorize access to this menu or its associated actions.
- **menu_type**: Classifies the menu item:
  - `M`: Directory (a top-level grouping).
  - `C`: Menu (a clickable page).
  - `F`: Button (a specific action, like "Add" or "Delete").
- **visible**: Controls whether the menu item is displayed in the navigation sidebar.
- **status**: Indicates if the menu is active (0) or disabled (1).

**Initialization Data:**
The system is initialized with a rich menu structure, including:
- **Top-level directories** for "System Management", "System Monitoring", and "System Tools".
- **Second-level menus** for managing users, roles, departments, etc.
- **Third-level menus** for specific functions like "Operation Log" and "Login Log".
- **Button-level permissions (F)** for granular actions like "User Query" (`system:user:query`) and "User Add" (`system:user:add`).

The `SysMenu` Java class (file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java) includes a `children` list to represent the hierarchical structure in memory, allowing for easy tree traversal in the application logic.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L133-L156)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java#L17-L275)

## sys_dept Table

The `sys_dept` table manages the organizational hierarchy of the enterprise, representing departments and their relationships. It supports multi-level structures (e.g.,总公司 -> 分公司 -> 部门) and is crucial for implementing data scope permissions.

**Key Attributes:**
- **dept_id**: Primary key, auto-incrementing unique identifier.
- **parent_id**: References `dept_id` of the parent department, forming the organizational tree.
- **ancestors**: A string containing a comma-separated list of all ancestor department IDs (e.g., '0,100,101' for the R&D department). This field is critical for efficiently querying all sub-departments without recursive SQL.
- **dept_name**: The name of the department.
- **leader**, **phone**, **email**: Contact information for the department head.
- **status**: Indicates if the department is active (0) or disabled (1).

**Initialization Data:**
The system is initialized with a three-level hierarchy:
- A root department "RuoYi Technology" (`dept_id=100`).
- Two child departments: "Shenzhen HQ" (`dept_id=101`) and "Changsha Branch" (`dept_id=102`).
- Sub-departments under Shenzhen HQ, including "R&D Department" (`dept_id=103`) and "Market Department" (`dept_id=104`).

The `SysDept` Java class (file://src/main/java/com/ruoyi/project/system/domain/SysDept.java) includes a `children` list to represent the department tree in memory and a `parentName` field for easier display in UI components.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L4-L21)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java#L18-L204)

## sys_config Table

The `sys_config` table provides a centralized mechanism for managing system-wide configuration parameters. It allows administrators to modify application behavior without changing code, promoting flexibility and ease of maintenance.

**Key Attributes:**
- **config_id**: Primary key, auto-incrementing unique identifier.
- **config_name**: A human-readable name for the configuration parameter (e.g., "User Management - Initial Password").
- **config_key**: A unique key used to programmatically retrieve the configuration value (e.g., `sys.user.initPassword`).
- **config_value**: The actual value of the configuration parameter.
- **config_type**: A flag indicating if the parameter is a built-in system parameter (`Y`) or a user-defined one (`N`).

**Initialization Data:**
Several key parameters are initialized:
- `sys.index.skinName`: Sets the default UI skin to "skin-blue".
- `sys.user.initPassword`: Defines the default password for new users as "123456".
- `sys.account.captchaEnabled`: A boolean flag (`true`) to enable the login captcha.
- `sys.login.blackIPList`: An empty string for the IP blacklist, which can be updated by administrators.

The `SysConfig` Java class (file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java) contains simple getters and setters with validation annotations to ensure data integrity (e.g., `@NotBlank` on `configName`).

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L533-L546)
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java#L16-L112)

## sys_dict_type and sys_dict_data Tables

These two tables work together to implement a data dictionary system, which is used to manage standardized lists of values (e.g., user genders, system statuses) throughout the application. This ensures data consistency and simplifies maintenance.

### sys_dict_type Table
This table defines the categories or types of dictionary data.

**Key Attributes:**
- **dict_id**: Primary key.
- **dict_name**: The display name of the dictionary type (e.g., "User Gender").
- **dict_type**: A unique key for the dictionary type (e.g., `sys_user_sex`), used in code to query the associated data.
- **status**: Indicates if the dictionary type is active.

**Initialization Data:**
Multiple dictionary types are defined, including `sys_user_sex` (user gender), `sys_show_hide` (menu visibility), and `sys_normal_disable` (system status).

### sys_dict_data Table
This table contains the actual values for each dictionary type.

**Key Attributes:**
- **dict_code**: Primary key, auto-incrementing.
- **dict_sort**: The display order of the value within its type.
- **dict_label**: The display text (e.g., "Male").
- **dict_value**: The internal code value (e.g., "0").
- **dict_type**: A foreign key referencing `sys_dict_type.dict_type`, linking the data to its type.
- **is_default**: Indicates if this is the default value for the type.
- **list_class**: A CSS class used to style the value when displayed in a table (e.g., "primary" for "Normal", "danger" for "Disabled").

**Initialization Data:**
For the `sys_user_sex` type, three entries are created: "Male" (0), "Female" (1), and "Unknown" (2). Similarly, the `sys_normal_disable` type has "Normal" (0) and "Disabled" (1).

The `SysDictData` class (file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java) includes a `getDefault()` method that returns a boolean by comparing `isDefault` to the constant `UserConstants.YES`.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L448-L546)
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java#L17-L97)
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java#L17-L177)

## Mapping Tables

The RuoYi-Vue system uses several junction tables to manage many-to-many relationships between core entities, enabling a flexible and scalable permission model.

### sys_user_role
This table establishes the many-to-many relationship between users and roles. A user can have multiple roles, and a role can be assigned to multiple users.

- **Composite Primary Key**: `(user_id, role_id)`
- **Initialization Data**: The `admin` user (1) is assigned the `Super Administrator` role (1), and the `ry` user (2) is assigned the `Normal Role` (2).

### sys_role_menu
This table links roles to menus, defining what a role can access. It is the primary mechanism for controlling menu visibility and button-level permissions.

- **Composite Primary Key**: `(role_id, menu_id)`
- **Initialization Data**: The `Normal Role` (2) is granted access to a comprehensive set of menus and buttons, including all user, role, and department management functions, as well as system monitoring and tool features.

### sys_role_dept
This table defines the data scope for roles that have a custom data scope (`data_scope=2`). It specifies which departments a role can access data for.

- **Composite Primary Key**: `(role_id, dept_id)`
- **Initialization Data**: The `Normal Role` (2) is granted access to the "RuoYi Technology" (`100`), "Shenzhen HQ" (`101`), and "Testing Department" (`105`) departments.

### sys_user_post and sys_user_online
While not the focus of this document, `sys_user_post` links users to their job positions (posts), and `sys_user_online` tracks currently logged-in users.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L267-L396)

## MyBatis Mapper Interactions

The MyBatis XML mappers define the SQL queries and result mappings that the application uses to interact with the database tables. They are located in `src/main/resources/mybatis/system/`.

### Key Query Patterns
- **Result Maps**: Each mapper defines a `<resultMap>` that maps database columns (e.g., `user_name`) to Java object properties (e.g., `userName`). Complex objects like `SysUser` include associations (e.g., `dept`) and collections (e.g., `roles`).
- **Dynamic SQL**: The mappers extensively use MyBatis' dynamic SQL features (`<if>`, `<where>`, `<foreach>`) to build queries conditionally. For example, the `selectUserList` query only adds a `WHERE` clause for `userName` if the parameter is provided.
- **Data Scope Filtering**: A critical security feature is the use of `${params.dataScope}` in queries (e.g., `SysUserMapper.xml`). This injects a dynamically generated SQL fragment that filters results based on the user's role and data scope, preventing unauthorized data access.

### Example: User Query with Joins
The `selectUserList` query in `SysUserMapper.xml` performs a left join with `sys_dept` to retrieve the department name, demonstrating how the mapper combines data from multiple tables into a single `SysUser` object.

### Example: Permission Retrieval
The `selectMenuPermsByUserId` query in `SysMenuMapper.xml` joins `sys_menu`, `sys_role_menu`, `sys_user_role`, and `sys_role` to retrieve all permission strings (`perms`) for a given user, which are then used by the security framework to authorize requests.

**Section sources**
- [SysUserMapper.xml](file://src/main/resources/mybatis/system/SysUserMapper.xml#L7-L227)
- [SysRoleMapper.xml](file://src/main/resources/mybatis/system/SysRoleMapper.xml#L7-L152)
- [SysMenuMapper.xml](file://src/main/resources/mybatis/system/SysMenuMapper.xml#L7-L206)
- [SysDeptMapper.xml](file://src/main/resources/mybatis/system/SysDeptMapper.xml#L7-L159)
- [SysConfigMapper.xml](file://src/main/resources/mybatis/system/SysConfigMapper.xml#L7-L117)
- [SysDictTypeMapper.xml](file://src/main/resources/mybatis/system/SysDictTypeMapper.xml#L7-L105)
- [SysDictDataMapper.xml](file://src/main/resources/mybatis/system/SysDictDataMapper.xml#L7-L124)

## System Integration and Business Context

The system management tables form an integrated ecosystem that powers the core functionality of the RuoYi-Vue application.

### Authentication and Authorization Flow
1.  **Authentication**: A user logs in with their `user_name` and `password` from `sys_user`.
2.  **Role Retrieval**: The system queries `sys_user_role` to find all roles assigned to the user.
3.  **Permission Retrieval**: For each role, the system queries `sys_role_menu` to find all associated menus and their `perms` values.
4.  **Access Control**: When the user attempts to access a resource, the backend checks if the required permission (e.g., `system:user:add`) is present in the user's permission set.

### Data Scope Enforcement
The `data_scope` field in `sys_role` dictates how data is filtered:
- For `data_scope=3` (own department), the application automatically adds a filter for the user's `dept_id`.
- For `data_scope=2` (custom), the application uses the `sys_role_dept` table to determine which departments the user can access.
- The `${params.dataScope}` injection in MyBatis queries is the technical implementation of this business rule.

### Configuration and Dictionary Usage
- **Configuration Management**: The `sys_config` table is queried at startup and when configuration pages are accessed. The `RuoYiConfig` class likely caches these values for performance.
- **Data Dictionary Functionality**: The `sys_dict_type` and `sys_dict_data` tables are used to populate dropdowns and format display values. For example, a user's `sex` field (0, 1, 2) is displayed as "Male", "Female", or "Unknown" by looking up the `dict_label` in `sys_dict_data` where `dict_type='sys_user_sex'`.

This comprehensive structure ensures that the system is secure, maintainable, and adaptable to different organizational requirements.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql)
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java)
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java)
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java)
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java)
- [SysUserMapper.xml](file://src/main/resources/mybatis/system/SysUserMapper.xml)
- [SysRoleMapper.xml](file://src/main/resources/mybatis/system/SysRoleMapper.xml)
- [SysMenuMapper.xml](file://src/main/resources/mybatis/system/SysMenuMapper.xml)
- [SysDeptMapper.xml](file://src/main/resources/mybatis/system/SysDeptMapper.xml)
- [SysConfigMapper.xml](file://src/main/resources/mybatis/system/SysConfigMapper.xml)
- [SysDictTypeMapper.xml](file://src/main/resources/mybatis/system/SysDictTypeMapper.xml)
- [SysDictDataMapper.xml](file://src/main/resources/mybatis/system/SysDictDataMapper.xml)