# Table Descriptions

<cite>
**Referenced Files in This Document**   
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java)
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java)
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java)
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java)
- [SysOperLog.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysOperLog.java)
- [SysLogininfor.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysLogininfor.java)
- [SysJob.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysJob.java)
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java)
- [SysUserMapper.xml](file://src/main/resources/mybatis/system/SysUserMapper.xml)
- [SysRoleMapper.xml](file://src/main/resources/mybatis/system/SysRoleMapper.xml)
- [SysMenuMapper.xml](file://src/main/resources/mybatis/system/SysMenuMapper.xml)
- [SysDeptMapper.xml](file://src/main/resources/mybatis/system/SysDeptMapper.xml)
- [SysConfigMapper.xml](file://src/main/resources/mybatis/system/SysConfigMapper.xml)
- [SysDictTypeMapper.xml](file://src/main/resources/mybatis/system/SysDictTypeMapper.xml)
- [SysDictDataMapper.xml](file://src/main/resources/mybatis/system/SysDictDataMapper.xml)
- [SysOperLogMapper.xml](file://src/main/resources/mybatis/monitor/SysOperLogMapper.xml)
- [SysLogininforMapper.xml](file://src/main/resources/mybatis/monitor/SysLogininforMapper.xml)
- [SysJobMapper.xml](file://src/main/resources/mybatis/monitor/SysJobMapper.xml)
- [GenTableMapper.xml](file://src/main/resources/mybatis/tool/GenTableMapper.xml)
</cite>

## Table of Contents
1. [System Management Tables](#system-management-tables)
2. [Monitoring Tables](#monitoring-tables)
3. [Tooling Tables](#tooling-tables)
4. [Table Categorization](#table-categorization)

## System Management Tables

### sys_user
The `sys_user` table stores user account information for the RuoYi-Vue system. It contains essential user data such as login credentials, personal information, and status. This table is central to authentication and authorization processes, with each user linked to departments, roles, and posts through foreign key relationships. The table includes fields for tracking login activity (last login IP and time) and password updates. The system initializes with a default admin user (userId=1) who has elevated privileges. User status is controlled by the `status` field (0=normal, 1=disabled) and soft deletion is implemented via the `delFlag` field (0=present, 2=deleted).

The MyBatis mapper for this table includes queries for user authentication (`selectUserByUserName`), uniqueness validation (`checkUserNameUnique`, `checkEmailUnique`), and role-based access control. The `SysUserMapper.xml` file contains SQL statements for CRUD operations and complex joins to retrieve user roles and department information. User-role relationships are managed through the `sys_user_role` junction table, while user-post assignments use the `sys_user_post` table.

**Section sources**
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java#L1-L341)
- [SysUserMapper.xml](file://src/main/resources/mybatis/system/SysUserMapper.xml)

### sys_role
The `sys_role` table defines roles within the system, serving as the foundation for role-based access control (RBAC). Each role has a unique identifier, name, and permission key that determines its authorization level. The `dataScope` field controls data access permissions with values representing different levels of data visibility (1=all data, 2=custom, 3=department, 4=department and below, 5=personal only). The table supports hierarchical menu and department selection through the `menuCheckStrictly` and `deptCheckStrictly` boolean flags.

Roles are linked to users via the `sys_user_role` junction table and to menus through `sys_role_menu`. The system includes a default administrator role (roleId=1) with full permissions. The `SysRoleMapper.xml` contains queries for role management, including `checkRoleKeyUnique` for validation and `selectRolesByUserId` for permission resolution. Role-menu assignments are critical for determining which menu items a user can access based on their assigned roles.

**Section sources**
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java#L1-L242)
- [SysRoleMapper.xml](file://src/main/resources/mybatis/system/SysRoleMapper.xml)

### sys_menu
The `sys_menu` table represents the navigation structure of the application, defining the hierarchical menu system. Each menu entry has a type (M=directory, C=menu, F=button) that determines its presentation and function. The table supports routing configuration with fields for `path` (URL route), `component` (Vue component path), and `routeName` (router name). Menu visibility is controlled by the `visible` field (0=visible, 1=hidden) and operational status by `status` (0=normal, 1=disabled).

The `perms` field contains permission strings used for fine-grained access control to specific buttons or actions. The hierarchical structure is maintained through `parentId` references and the `orderNum` field for sorting. The `SysMenuMapper.xml` includes recursive queries like `selectMenuListByRoleId` to build complete menu trees for authenticated users. Menu-role relationships in `sys_role_menu` determine which menus are accessible to users with specific roles.

**Section sources**
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java#L1-L275)
- [SysMenuMapper.xml](file://src/main/resources/mybatis/system/SysMenuMapper.xml)

### sys_dept
The `sys_dept` table manages the organizational hierarchy of departments within the system. It implements a tree structure through `parentId` references and maintains ancestor paths in the `ancestors` field for efficient hierarchical queries. Each department has basic information including name, order number, leader, contact details, and status. The table supports data scope limitations in role-based access control.

Departments are linked to users through the `deptId` field in `sys_user`, enabling department-level data filtering. The `SysDeptMapper.xml` contains queries for retrieving department hierarchies and validating department names. The tree structure allows for operations like finding all sub-departments of a given department, which is essential for implementing data scope rules where users can access data from their department and sub-departments.

**Section sources**
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java#L1-L204)
- [SysDeptMapper.xml](file://src/main/resources/mybatis/system/SysDeptMapper.xml)

### sys_config
The `sys_config` table stores system-wide configuration parameters as key-value pairs. Each configuration has a `configKey` (parameter key name), `configValue` (parameter value), and `configName` (descriptive name). The `configType` field indicates whether a parameter is system-built (Y) or user-defined (N). This table enables runtime configuration without code changes, supporting features like system title, activation status of registration, and account lockout policies.

The `SysConfigMapper.xml` contains optimized queries for retrieving configurations by key (`selectConfigByKey`) and listing all configurations. The system caches configuration values to improve performance. Configuration parameters are used throughout the application to control behavior, such as determining whether captcha is required during login or setting session timeout values.

**Section sources**
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java#L1-L112)
- [SysConfigMapper.xml](file://src/main/resources/mybatis/system/SysConfigMapper.xml)

### sys_dict_type
The `sys_dict_type` table defines dictionary types, which serve as categories for lookup values in the system. Each dictionary type has a unique `dictType` code (following the pattern of lowercase letters, numbers, and underscores) and a descriptive `dictName`. The `status` field controls whether the dictionary type is active (0) or disabled (1). This table provides the framework for standardized data entry and consistent value representation across the application.

Dictionary types are referenced by the `sys_dict_data` table, which contains the actual dictionary values. The `SysDictTypeMapper.xml` includes queries for type validation (`checkDictTypeUnique`) and retrieval by type code. Dictionary types are used to categorize various system values such as user status, gender, and menu types, ensuring data consistency and enabling dynamic dropdown population in the user interface.

**Section sources**
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java#L1-L97)
- [SysDictTypeMapper.xml](file://src/main/resources/mybatis/system/SysDictTypeMapper.xml)

### sys_dict_data
The `sys_dict_data` table contains the actual values for dictionary types defined in `sys_dict_type`. Each entry has a `dictLabel` (display text), `dictValue` (stored value), and references its parent type through `dictType`. The `dictSort` field controls the display order of values, while `isDefault` (Y/N) indicates the default selection. The table supports visual customization with `cssClass` and `listClass` fields for styling.

This table enables the system to maintain standardized sets of values for various fields, such as user status (0=normal, 1=disabled), gender (0=male, 1=female, 2=unknown), and system statuses. The `SysDictDataMapper.xml` contains queries for retrieving dictionary data by type, which are used to populate dropdowns and perform value translations in the user interface. Dictionary data is typically cached to improve application performance.

**Section sources**
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java#L1-L177)
- [SysDictDataMapper.xml](file://src/main/resources/mybatis/system/SysDictDataMapper.xml)

## Monitoring Tables

### sys_oper_log
The `sys_oper_log` table records operational activities performed by users in the system. Each log entry captures details such as the operation module (`title`), business type (0=other, 1=add, 2=edit, 3=delete, etc.), request method, URL, parameters, and execution result. The `status` field indicates whether the operation was successful (0) or resulted in an error (1), with error details stored in `errorMsg`.

This table is essential for auditing, troubleshooting, and security monitoring. The `SysOperLogMapper.xml` contains queries for log retrieval and export functionality. Operation logs are automatically generated through AOP (Aspect-Oriented Programming) using the `@Log` annotation on service methods. The logs include contextual information like operator name, department, IP address, location, and execution time, providing a comprehensive audit trail.

**Section sources**
- [SysOperLog.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysOperLog.java#L1-L270)
- [SysOperLogMapper.xml](file://src/main/resources/mybatis/monitor/SysOperLogMapper.xml)

### sys_logininfor
The `sys_logininfor` table tracks user login attempts and sessions. Each record includes the `userName`, login `status` (0=success, 1=failure), IP address (`ipaddr`), location, browser type, operating system, and timestamp. This information is crucial for security monitoring, detecting brute force attacks, and analyzing user access patterns.

The table serves as the foundation for security features like account lockout after multiple failed login attempts. The `SysLogininforMapper.xml` contains queries for retrieving login history and generating security reports. Login information is automatically recorded during authentication attempts, capturing both successful logins and failed attempts due to incorrect credentials or account restrictions.

**Section sources**
- [SysLogininfor.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysLogininfor.java#L1-L144)
- [SysLogininforMapper.xml](file://src/main/resources/mybatis/monitor/SysLogininforMapper.xml)

### sys_job
The `sys_job` table manages scheduled tasks in the system, built on the Quartz scheduler framework. Each job entry contains configuration details such as job name, group, cron expression for scheduling, implementation class, and status (0=normal, 1=paused). The table supports advanced scheduling features including concurrent execution control and misfire handling policies.

Job parameters are stored in the `params` field as JSON, allowing flexible configuration. The `SysJobMapper.xml` contains queries for job management operations including retrieval, insertion, and status updates. Scheduled jobs are used for various system maintenance tasks, data synchronization, report generation, and other automated processes that require periodic execution.

**Section sources**
- [SysJob.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysJob.java)
- [SysJobMapper.xml](file://src/main/resources/mybatis/monitor/SysJobMapper.xml)

## Tooling Tables

### gen_table
The `gen_table` table stores metadata for code generation functionality. It contains information about database tables for which code should be generated, including table name, comments, entity class name, package structure, module name, and business configuration. The `tplCategory` field specifies the template type (crud, tree, sub) to be used for code generation.

This table works in conjunction with `gen_table_column` to define the complete structure of the target table. The `GenTableMapper.xml` contains queries for retrieving table metadata from both the generation configuration and the actual database schema. The code generation feature uses this information to create controller, service, mapper, and Vue components automatically, significantly reducing development time for new modules.

**Section sources**
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java#L1-L385)
- [GenTableMapper.xml](file://src/main/resources/mybatis/tool/GenTableMapper.xml)

## Table Categorization

The RuoYi-Vue database schema can be categorized into three primary domains based on functionality:

**System Management Tables** include `sys_user`, `sys_role`, `sys_menu`, `sys_dept`, `sys_config`, `sys_dict_type`, and `sys_dict_data`. These tables form the core administrative framework, managing users, permissions, organizational structure, and system configuration. They enable user authentication, role-based authorization, menu navigation, and system parameter management.

**Monitoring Tables** consist of `sys_oper_log`, `sys_logininfor`, and `sys_job`. These tables provide operational visibility and system oversight. The logging tables capture user activities and system events for audit and security purposes, while the job table manages scheduled tasks and background processing.

**Tooling Tables** include `gen_table` and its related `gen_table_column`. These tables support the code generation feature, allowing developers to automatically generate CRUD interfaces and backend code from database schema definitions. This tooling domain enhances development efficiency and ensures consistency across generated components.

These categories reflect the architectural separation of concerns in the RuoYi-Vue system, with distinct data models serving administration, monitoring, and development tooling functions.

**Section sources**
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java)
- [SysConfig.java](file://src/main/java/com/ruoyi/project/system/domain/SysConfig.java)
- [SysDictType.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictType.java)
- [SysDictData.java](file://src/main/java/com/ruoyi/project/system/domain/SysDictData.java)
- [SysOperLog.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysOperLog.java)
- [SysLogininfor.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysLogininfor.java)
- [SysJob.java](file://src/main/java/com/ruoyi/project/monitor/domain/SysJob.java)
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java)