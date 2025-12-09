# Field Definitions

<cite>
**Referenced Files in This Document**   
- [ry_20250522.sql](file://sql/ry_20250522.sql)
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java)
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java)
- [GenTableColumn.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTableColumn.java)
- [BaseEntity.java](file://src/main/java/com/ruoyi/framework/web/domain/BaseEntity.java)
- [TreeEntity.java](file://src/main/java/com/ruoyi/framework/web/domain/TreeEntity.java)
- [GenConstants.java](file://src/main/java/com/ruoyi/common/constant/GenConstants.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Core Database Tables](#core-database-tables)
   - [sys_user](#sys_user)
   - [sys_role](#sys_role)
   - [sys_menu](#sys_menu)
   - [sys_dept](#sys_dept)
   - [gen_table](#gen_table)
   - [gen_table_column](#gen_table_column)
3. [Common Field Patterns](#common-field-patterns)
4. [Special Field Explanations](#special-field-explanations)
5. [Indexing and Performance](#indexing-and-performance)

## Introduction
This document provides comprehensive field-level documentation for key database tables in the RuoYi-Vue system. It details every column in core tables such as sys_user, sys_role, sys_menu, sys_dept, and code generation metadata tables gen_table and gen_table_column. For each field, this documentation includes data types, constraints, default values, business meaning, and mappings to corresponding Java entity fields. The document also explains special fields used for hierarchical queries, soft deletes, permission control, cryptographic storage, and code generation scaffolding.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L1-L704)

## Core Database Tables

### sys_user
The sys_user table stores user account information and authentication data.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| user_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | userId | Unique identifier for the user |
| dept_id | bigint(20) | - | NULL | deptId | Department to which the user belongs |
| user_name | varchar(30) | NOT NULL | - | userName | User's login account name |
| nick_name | varchar(30) | NOT NULL | - | nickName | User's display name or nickname |
| user_type | varchar(2) | - | '00' | userType | User category (00=system user) |
| email | varchar(50) | - | '' | email | User's email address |
| phonenumber | varchar(11) | - | '' | phonenumber | User's mobile phone number |
| sex | char(1) | - | '0' | sex | Gender (0=male, 1=female, 2=unknown) |
| avatar | varchar(100) | - | '' | avatar | URL path to user's profile picture |
| password | varchar(100) | - | '' | password | Bcrypt-hashed password for authentication |
| status | char(1) | - | '0' | status | Account status (0=active, 1=disabled) |
| del_flag | char(1) | - | '0' | delFlag | Soft delete flag (0=exists, 2=deleted) |
| login_ip | varchar(128) | - | '' | loginIp | Last login IP address |
| login_date | datetime | - | NULL | loginDate | Timestamp of last successful login |
| pwd_update_date | datetime | - | NULL | pwdUpdateDate | Timestamp when password was last changed |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |
| remark | varchar(500) | - | NULL | remark | Additional notes or comments |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L41-L64)
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java#L21-L341)

### sys_role
The sys_role table defines user roles and their associated permissions.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| role_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | roleId | Unique identifier for the role |
| role_name | varchar(30) | NOT NULL | - | roleName | Display name of the role |
| role_key | varchar(100) | NOT NULL | - | roleKey | Permission string used for access control |
| role_sort | int(4) | NOT NULL | - | roleSort | Display order for UI sorting |
| data_scope | char(1) | - | '1' | dataScope | Data access scope (1=all, 2=custom, 3=department, 4=department+sub) |
| menu_check_strictly | tinyint(1) | - | 1 | menuCheckStrictly | Whether menu selection is strict (1=yes, 0=no) |
| dept_check_strictly | tinyint(1) | - | 1 | deptCheckStrictly | Whether department selection is strict (1=yes, 0=no) |
| status | char(1) | NOT NULL | - | status | Role status (0=active, 1=disabled) |
| del_flag | char(1) | - | '0' | delFlag | Soft delete flag (0=exists, 2=deleted) |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |
| remark | varchar(500) | - | NULL | remark | Additional notes or comments |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L104-L121)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java#L18-L242)

### sys_menu
The sys_menu table defines the application's navigation structure and permission system.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| menu_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | menuId | Unique identifier for the menu item |
| menu_name | varchar(50) | NOT NULL | - | menuName | Display name of the menu |
| parent_id | bigint(20) | - | 0 | parentId | ID of parent menu (0=root) |
| order_num | int(4) | - | 0 | orderNum | Display order for sorting |
| path | varchar(200) | - | '' | path | Route path in the frontend router |
| component | varchar(255) | - | NULL | component | Vue component path |
| query | varchar(255) | - | NULL | query | Route query parameters |
| route_name | varchar(50) | - | '' | routeName | Named route in Vue router |
| is_frame | int(1) | - | 1 | isFrame | Whether it's an external link (0=yes, 1=no) |
| is_cache | int(1) | - | 0 | isCache | Whether to cache the page (0=yes, 1=no) |
| menu_type | char(1) | - | '' | menuType | Type (M=directory, C=menu, F=button) |
| visible | char(1) | - | 0 | visible | Visibility in menu (0=visible, 1=hidden) |
| status | char(1) | - | 0 | status | Menu status (0=active, 1=disabled) |
| perms | varchar(100) | - | NULL | perms | Permission string for access control |
| icon | varchar(100) | - | '#' | icon | Icon class name for display |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |
| remark | varchar(500) | - | '' | remark | Additional notes or comments |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L133-L156)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java#L17-L275)

### sys_dept
The sys_dept table manages the organizational hierarchy of departments.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| dept_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | deptId | Unique identifier for the department |
| parent_id | bigint(20) | - | 0 | parentId | ID of parent department (0=root) |
| ancestors | varchar(50) | - | '' | ancestors | Ancestor path (e.g., "0,100,101") for hierarchical queries |
| dept_name | varchar(30) | - | '' | deptName | Name of the department |
| order_num | int(4) | - | 0 | orderNum | Display order for sorting |
| leader | varchar(20) | - | NULL | leader | Name of the department leader |
| phone | varchar(11) | - | NULL | phone | Contact phone number |
| email | varchar(50) | - | NULL | email | Department email address |
| status | char(1) | - | '0' | status | Department status (0=active, 1=disabled) |
| del_flag | char(1) | - | '0' | delFlag | Soft delete flag (0=exists, 2=deleted) |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L4-L21)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java#L18-L204)

### gen_table
The gen_table table stores metadata for code generation functionality.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| table_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | tableId | Unique identifier for the generation record |
| table_name | varchar(200) | - | '' | tableName | Database table name |
| table_comment | varchar(500) | - | '' | tableComment | Description of the table |
| sub_table_name | varchar(64) | - | NULL | subTableName | Name of associated sub-table |
| sub_table_fk_name | varchar(64) | - | NULL | subTableFkName | Foreign key field name for sub-table |
| class_name | varchar(100) | - | '' | className | Java entity class name |
| tpl_category | varchar(200) | - | 'crud' | tplCategory | Template category (crud, tree) |
| tpl_web_type | varchar(30) | - | '' | tplWebType | Frontend template type |
| package_name | varchar(100) | - | - | packageName | Java package path |
| module_name | varchar(30) | - | - | moduleName | Module name |
| business_name | varchar(30) | - | - | businessName | Business name |
| function_name | varchar(50) | - | - | functionName | Feature name |
| function_author | varchar(50) | - | - | functionAuthor | Code author |
| gen_type | char(1) | - | '0' | genType | Code generation method (0=zip, 1=custom path) |
| gen_path | varchar(200) | - | '/' | genPath | Generation path |
| options | varchar(1000) | - | - | options | Additional generation options |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |
| remark | varchar(500) | - | NULL | remark | Additional notes or comments |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L649-L673)
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java#L16-L385)

### gen_table_column
The gen_table_column table stores column metadata for code generation.

| Column Name | Data Type | Constraints | Default Value | Java Field | Business Meaning |
|-------------|---------|------------|---------------|----------|------------------|
| column_id | bigint(20) | NOT NULL, AUTO_INCREMENT | - | columnId | Unique identifier for the column |
| table_id | bigint(20) | - | - | tableId | Reference to gen_table |
| column_name | varchar(200) | - | - | columnName | Database column name |
| column_comment | varchar(500) | - | - | columnComment | Column description |
| column_type | varchar(100) | - | - | columnType | Database column type |
| java_type | varchar(500) | - | - | javaType | Java data type |
| java_field | varchar(200) | - | - | javaField | Java field name |
| is_pk | char(1) | - | - | isPk | Whether it's a primary key (1=yes) |
| is_increment | char(1) | - | - | isIncrement | Whether it's auto-increment (1=yes) |
| is_required | char(1) | - | - | isRequired | Whether it's a required field (1=yes) |
| is_insert | char(1) | - | - | isInsert | Whether it's an insert field (1=yes) |
| is_edit | char(1) | - | - | isEdit | Whether it's an edit field (1=yes) |
| is_list | char(1) | - | - | isList | Whether it appears in list views (1=yes) |
| is_query | char(1) | - | - | isQuery | Whether it's a query field (1=yes) |
| query_type | varchar(200) | - | 'EQ' | queryType | Query method (EQ, NE, GT, etc.) |
| html_type | varchar(200) | - | - | htmlType | UI component type |
| dict_type | varchar(200) | - | '' | dictType | Dictionary type for value mapping |
| sort | int | - | - | sort | Display order |
| create_by | varchar(64) | - | '' | createBy | User who created this record |
| create_time | datetime | - | NULL | createTime | Timestamp when record was created |
| update_by | varchar(64) | - | '' | updateBy | User who last updated this record |
| update_time | datetime | - | NULL | updateTime | Timestamp of last update |

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L679-L704)
- [GenTableColumn.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTableColumn.java#L12-L373)

## Common Field Patterns

### BaseEntity Fields
The BaseEntity class provides common audit fields inherited by all entity classes:

| Database Column | Java Field | Type | Purpose |
|----------------|-----------|------|---------|
| create_by | createBy | String | Records who created the record |
| create_time | createTime | Date | Timestamp of record creation |
| update_by | updateBy | String | Records who last modified the record |
| update_time | updateTime | Date | Timestamp of last modification |
| remark | remark | String | Stores additional comments or notes |

These fields are automatically populated by the framework during CRUD operations.

### TreeEntity Fields
The TreeEntity class extends BaseEntity and adds hierarchical data support:

| Database Column | Java Field | Type | Purpose |
|----------------|-----------|------|---------|
| parent_id | parentId | Long | References parent node in tree structure |
| ancestors | ancestors | String | Stores path of ancestors for efficient hierarchical queries |
| order_num | orderNum | Integer | Determines display order within siblings |
| parent_name | parentName | String | Denormalized parent name for display purposes |

The ancestors field uses a comma-separated format (e.g., "0,100,101") to represent the full path from root to current node, enabling efficient subtree queries without recursive operations.

**Section sources**
- [BaseEntity.java](file://src/main/java/com/ruoyi/framework/web/domain/BaseEntity.java#L16-L119)
- [TreeEntity.java](file://src/main/java/com/ruoyi/framework/web/domain/TreeEntity.java#L11-L80)
- [GenConstants.java](file://src/main/java/com/ruoyi/common/constant/GenConstants.java#L8-L63)

## Special Field Explanations

### Cryptographic Fields
**password**: Stored as bcrypt-hashed values (evident from the $2a$ prefix in sample data). This provides strong protection against password cracking even if the database is compromised. The system never stores or handles plain-text passwords.

### Hierarchical Query Fields
**ancestors**: Used in sys_dept and other tree-structured tables to enable efficient hierarchical queries. The comma-separated path allows querying all descendants with a simple LIKE operation (e.g., WHERE ancestors LIKE '0,100,%').

### Soft Delete Fields
**del_flag**: Implements soft delete pattern where records are marked as deleted (value '2') rather than being physically removed. This preserves data integrity and allows for potential recovery. Value '0' indicates active records.

### Permission Control Fields
**data_scope**: In sys_role, defines the extent of data access a role has (all data, custom, department-level, or department+sub-departments). This enables fine-grained data access control based on organizational hierarchy.

**perms**: In sys_menu, contains permission strings (e.g., 'system:user:list') that are used for fine-grained access control. These are checked during authorization to determine if a user can access specific functionality.

### Code Generation Metadata
The gen_table and gen_table_column tables drive the CRUD scaffolding system:
- **tpl_category**: Determines which template to use ('crud' for standard tables, 'tree' for hierarchical data)
- **html_type**: Specifies the UI component to use for field rendering (input, select, checkbox, etc.)
- **dict_type**: Links fields to dictionary types for value mapping and display
- **is_insert/is_edit/is_list/is_query**: Boolean flags that control which operations include the field

**Section sources**
- [SysUser.java](file://src/main/java/com/ruoyi/project/system/domain/SysUser.java#L57-L58)
- [SysDept.java](file://src/main/java/com/ruoyi/project/system/domain/SysDept.java#L29-L30)
- [SysRole.java](file://src/main/java/com/ruoyi/project/system/domain/SysRole.java#L38-L40)
- [SysMenu.java](file://src/main/java/com/ruoyi/project/system/domain/SysMenu.java#L64-L65)
- [GenTable.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTable.java#L42-L46)
- [GenTableColumn.java](file://src/main/java/com/ruoyi/project/tool/gen/domain/GenTableColumn.java#L62-L67)

## Indexing and Performance

### Frequently Queried Fields
The following fields have dedicated indexes to optimize query performance:

**sys_oper_log**:
- idx_sys_oper_log_bt (business_type): Optimizes filtering by operation type
- idx_sys_oper_log_s (status): Optimizes filtering by execution status  
- idx_sys_oper_log_ot (oper_time): Optimizes time-based queries and sorting

**sys_logininfor**:
- idx_sys_logininfor_s (status): Optimizes login status filtering
- idx_sys_logininfor_lt (login_time): Optimizes time-based queries for login history

### Performance Considerations
- **login_ip** and **oper_ip**: Stored as varchar(128) to accommodate both IPv4 and IPv6 addresses. Consider using IP-to-location services for enhanced analytics.
- **status flags**: Single-character status fields (status, del_flag, visible) enable efficient indexing and filtering.
- **ancestors field**: The comma-separated path pattern allows efficient hierarchical queries without recursive SQL or multiple joins.
- **password field**: Bcrypt hashing with appropriate work factor provides strong security while balancing performance. The fixed length (100 chars) accommodates bcrypt's output format.

**Section sources**
- [ry_20250522.sql](file://sql/ry_20250522.sql#L439-L441)
- [ry_20250522.sql](file://sql/ry_20250522.sql#L573-L574)