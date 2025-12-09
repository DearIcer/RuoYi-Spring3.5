# Installation & Setup

<cite>
**Referenced Files in This Document**   
- [pom.xml](file://pom.xml)
- [application.yml](file://src/main/resources/application.yml)
- [application-druid.yml](file://src/main/resources/application-druid.yml)
- [ry.bat](file://ry.bat)
- [ry.sh](file://ry.sh)
- [DruidConfig.java](file://src/main/java/com/ruoyi/framework/config/DruidConfig.java)
- [RedisConfig.java](file://src/main/java/com/ruoyi/framework/config/RedisConfig.java)
- [RuoYiApplication.java](file://src/main/java/com/ruoyi/RuoYiApplication.java)
- [sql/ry_20250522.sql](file://sql/ry_20250522.sql)
- [sql/quartz.sql](file://sql/quartz.sql)
- [README.md](file://README.md)
</cite>

## Table of Contents
1. [Environment Requirements](#environment-requirements)
2. [Database Initialization](#database-initialization)
3. [Configuration Setup](#configuration-setup)
4. [Application Execution](#application-execution)
5. [Common Setup Issues and Solutions](#common-setup-issues-and-solutions)

## Environment Requirements

To successfully install and run the RuoYi-Vue application, ensure that your development environment meets the following requirements:

- **Java JDK**: Version 1.8 or higher. The project is configured to use Java 8 as specified in the `pom.xml` file under the `<java.version>` property.
- **Maven**: Version 3.6 or higher. The project uses Maven as its build tool, and dependencies are managed through the `pom.xml` configuration file.
- **Database**: MySQL 5.7 or higher. The application uses MySQL as its primary database, with connection details configured in the `application-druid.yml` file.
- **Redis**: Required for caching and session management. The Redis connection settings are defined in the `application.yml` file under the `spring.redis` section.
- **Node.js and npm**: Required for the frontend Vue application (not covered in this backend-focused document).

The project is built on Spring Boot 2.5.15, as specified in the `pom.xml` file, and uses Spring Security, JWT, and Redis for authentication and authorization.

**Section sources**
- [pom.xml](file://pom.xml#L25)
- [application.yml](file://src/main/resources/application.yml#L69-L89)
- [application-druid.yml](file://src/main/resources/application-druid.yml#L9)

## Database Initialization

Before running the application, you must initialize the database using the provided SQL scripts located in the `sql/` directory.

1. **Import the main database schema and data**:
   - Execute the `ry_20250522.sql` script to create the core database tables and insert initial data.
   - This script includes DDL statements for creating tables such as `sys_dept`, `sys_user`, `sys_role`, and `sys_menu`, along with INSERT statements for populating default records.
   - The script also sets up initial user accounts, including the admin user with username `admin` and password `admin123`.

2. **Import Quartz scheduler tables**:
   - Execute the `quartz.sql` script to create the necessary tables for the Quartz job scheduling system.
   - This includes tables such as `QRTZ_JOB_DETAILS`, `QRTZ_TRIGGERS`, `QRTZ_FIRED_TRIGGERS`, and others required for job persistence and management.

These SQL scripts should be executed against your MySQL database instance. Ensure that the database user has sufficient privileges to create tables and insert data.

**Section sources**
- [sql/ry_20250522.sql](file://sql/ry_20250522.sql#L1-L200)
- [sql/quartz.sql](file://sql/quartz.sql#L1-L174)

## Configuration Setup

The RuoYi-Vue application uses Spring Boot's configuration mechanism with YAML files for environment-specific settings.

### Main Configuration (application.yml)

The `application.yml` file contains general application settings:

- **Server Configuration**: The server port is set to 8080 by default, with context path `/`. This can be modified under the `server` section.
- **Redis Configuration**: The Redis connection details are specified under `spring.redis`, including host (`192.168.40.41`), port (`6378`), password (`8icymZp_WvFkzMt`), and connection pool settings.
- **Token Configuration**: JWT token settings including header name (`Authorization`), secret key (`abcdefghijklmnopqrstuvwxyz`), and expiration time (30 minutes).
- **Profile Activation**: The active profile is set to `druid`, which loads the database-specific configuration from `application-druid.yml`.

### Database Configuration (application-druid.yml)

This profile-specific configuration file contains the data source settings:

- **Master Data Source**: Configured with URL pointing to `jdbc:mysql://192.168.40.41:3306/ry`, username `root`, and password `123456`.
- **Druid Connection Pool**: Configured with initial size of 5, minimum idle connections of 10, maximum active connections of 20, and various timeout settings.
- **Druid Monitoring**: The StatViewServlet is enabled, accessible via `/druid/*` with login credentials `ruoyi`/`123456`.

The `DruidConfig.java` class configures the dynamic data source and removes the default banner from the Druid monitoring page.

**Section sources**
- [application.yml](file://src/main/resources/application.yml#L1-L149)
- [application-druid.yml](file://src/main/resources/application-druid.yml#L1-L61)
- [DruidConfig.java](file://src/main/java/com/ruoyi/framework/config/DruidConfig.java#L32-L127)
- [RedisConfig.java](file://src/main/java/com/ruoyi/framework/config/RedisConfig.java#L17-L70)

## Application Execution

The RuoYi-Vue application can be started using several methods depending on your operating system and preferences.

### Using Platform-Specific Scripts

#### Windows (ry.bat)
The `ry.bat` script provides a menu-driven interface for managing the application:
- Option 1: Start the application
- Option 2: Stop the application
- Option 3: Restart the application
- Option 4: Check application status
- Option 5: Exit

The script uses `jps` to check for running processes and `taskkill` to terminate them. It starts the application with predefined JVM options including memory settings and timezone configuration.

#### Linux/Unix (ry.sh)
The `ry.sh` script supports command-line arguments:
- `./ry.sh start`: Start the application
- `./ry.sh stop`: Stop the application
- `./ry.sh restart`: Restart the application
- `./ry.sh status`: Check application status

The script uses `ps` and `grep` to find the Java process and `kill -TERM` to gracefully shut it down.

### Using Maven (run-tomcat.bat)

The `bin/run-tomcat.bat` script runs the application using Maven's Spring Boot plugin:
```bash
mvn clean spring-boot:run -Dmaven.test.skip=true -U
```
This compiles the code and starts the embedded Tomcat server. The script sets appropriate JVM memory options.

### Building and Running JAR

Alternatively, you can package the application and run it as a JAR:
1. Build the package using `bin/package.bat` or `mvn clean package -Dmaven.test.skip=true`
2. Run the generated JAR file using `java -jar ruoyi.jar`

The main application class `RuoYiApplication.java` contains the `main` method that starts the Spring Boot application.

**Section sources**
- [ry.bat](file://ry.bat#L1-L68)
- [ry.sh](file://ry.sh#L1-L87)
- [bin/run-tomcat.bat](file://bin/run-tomcat.bat#L1-L14)
- [bin/package.bat](file://bin/package.bat#L1-L12)
- [RuoYiApplication.java](file://src/main/java/com/ruoyi/RuoYiApplication.java#L12-L31)

## Common Setup Issues and Solutions

### Port Conflicts

**Issue**: Application fails to start with "Port 8080 is already in use" error.
**Solution**: 
- Change the server port in `application.yml` under `server.port`
- Or terminate the process using port 8080 using `netstat -ano | findstr :8080` (Windows) or `lsof -i :8080` (Linux/Mac) and then kill the process

### Database Connection Issues

**Issue**: Unable to connect to MySQL database.
**Solution**:
- Verify MySQL server is running and accessible at `192.168.40.41:3306`
- Check database credentials in `application-druid.yml`
- Ensure MySQL driver is available (included in `pom.xml`)
- Verify database `ry` exists and has been initialized with the SQL scripts

### Redis Connection Issues

**Issue**: Application fails to start due to Redis connection timeout.
**Solution**:
- Ensure Redis server is running at `192.168.40.41:6378`
- Verify Redis password is correct
- Check firewall settings to ensure port 6378 is accessible
- Temporarily disable Redis in `application.yml` by commenting out the `spring.redis` section for testing

### Dependency Resolution Problems

**Issue**: Maven fails to download dependencies.
**Solution**:
- The `pom.xml` file configures Alibaba's Maven repository as the primary repository
- Ensure internet connectivity
- Clear Maven cache (`%USER_HOME%\.m2\repository`) and retry
- Verify repository URL in `pom.xml` under `<repositories>`

### Authentication Issues

**Issue**: Unable to log in with default credentials.
**Solution**:
- Verify `ry_20250522.sql` was executed successfully
- Check that the admin user was inserted into `sys_user` table
- Verify password encryption is working (stored as bcrypt hash)

**Section sources**
- [application.yml](file://src/main/resources/application.yml#L18-L19)
- [application-druid.yml](file://src/main/resources/application-druid.yml#L9-L11)
- [application.yml](file://src/main/resources/application.yml#L71-L77)
- [pom.xml](file://pom.xml#L251-L273)
- [sql/ry_20250522.sql](file://sql/ry_20250522.sql#L69)