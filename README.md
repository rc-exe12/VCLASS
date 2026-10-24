VCLASS — Virtual Classroom Management System
Developed by: Ramcharan Sai
VCLASS is a web-based classroom management application built with Spring Boot, Thymeleaf, and a MySQL-compatible database. It provides a foundation for managing users, courses, assignments, attendance, and assignment submissions in one place.
> **Project status:** The source code is hosted on GitHub and the database has been migrated to TiDB Cloud. Add the live Render URL below after deployment is confirmed healthy.
Table of Contents
Overview
Features
Technology Stack
Architecture
Database
Prerequisites
Run Locally
Configuration
Deployment
Project Structure
Security
Troubleshooting
Author
Overview
VCLASS aims to make common classroom workflows easier to manage through a single web application. Spring Boot handles server-side application logic, Thymeleaf renders HTML pages, and a relational database stores application data.
Repository: https://github.com/rc-exe12/VCLASS
Live demo: Add your Render URL here after deployment succeeds.
Features
The database currently includes these core entities:
Users: application accounts and roles.
Courses: course information and teacher associations.
Assignments: assignment descriptions, due dates, marks, and course associations.
Attendance: attendance records.
Submissions: student assignment submission records.
The exact actions available in the interface depend on the controllers and templates included in the repository.
Technology Stack
Layer	Technology
Backend	Java 17, Spring Boot 3.3.5
Web UI	Thymeleaf, HTML/CSS
Data access	Spring Data JPA / Hibernate
Database	MySQL locally; TiDB Cloud (MySQL-compatible) for hosted data
Build tool	Maven
Deployment	Docker, Render
Source control	Git, GitHub
Architecture
```text
Browser
   |
   v
Spring Boot application
   |-- Controllers and application logic
   |-- Thymeleaf templates
   |-- Spring Data JPA / Hibernate
   |
   v
MySQL-compatible database
   |-- app_users
   |-- course
   |-- assignment
   |-- attendance
   `-- submission
```
Database
The database is named `vclass_db`.
Table	Purpose
`app_users`	User accounts and roles
`course`	Course records
`assignment`	Assignment records
`attendance`	Attendance records
`submission`	Assignment submissions
The verified database import contained these sample row counts at migration time:
Table	Rows
`app_users`	2
`course`	2
`assignment`	2
`attendance`	1
`submission`	2
These are sample-data counts and may change as the application is used.
Prerequisites
Java Development Kit (JDK) 17
Maven (or the Maven Wrapper, if included in the repository)
MySQL Server 8+ or another supported MySQL-compatible database
Git
Run Locally
1. Clone the repository
```bash
git clone https://github.com/rc-exe12/VCLASS.git
cd VCLASS
```
2. Create the database
Log in to your local MySQL server and run:
```sql
CREATE DATABASE vclass_db;
```
If you have a database backup to restore, import it into `vclass_db` with your local MySQL client. Do not commit SQL backups containing personal or sensitive data to GitHub.
3. Configure environment variables
In PowerShell:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/vclass_db"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_LOCAL_MYSQL_PASSWORD"
```
Replace `YOUR_LOCAL_MYSQL_PASSWORD` with your local MySQL password. Never commit actual secrets to source files.
4. Start the application
From the project root:
```powershell
mvn spring-boot:run
```
Or package and run the JAR:
```powershell
mvn clean package
java -jar target\*.jar
```
Open:
```text
http://localhost:8080
```
Configuration
The application's `src/main/resources/application.properties` is configured to read database settings from environment variables, using defaults for local development:
```properties
spring.application.name=VClass
server.port=${PORT:8080}

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/vclass_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.thymeleaf.cache=false
```
Variable	Purpose
`DB_URL`	JDBC URL for the database
`DB_USERNAME`	Database username
`DB_PASSWORD`	Database password
`PORT`	Usually supplied automatically by the hosting platform
Never include real passwords, API keys, or private connection details in this README or in Git.
Deployment
The repository includes a `Dockerfile` for container-based deployment.
General Render deployment steps:
Sign in to Render and connect your GitHub account.
Create a Web Service from `rc-exe12/VCLASS`, branch `main`.
Select Docker as the runtime to use the repository's Dockerfile.
Choose a compute plan that fits your budget.
Add `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in Render's environment settings.
Deploy and review the build and runtime logs.
Open the generated Render URL and test the application.
For TiDB Cloud, use the connection settings shown in its Connect panel. Ensure the JDBC URL and TLS/CA configuration match TiDB's current instructions. Do not disable certificate verification to bypass an SSL error.
Cost note: Hosting plans and free-tier limits can change. Confirm current pricing and database limits with Render and TiDB Cloud before relying on the deployment for ongoing use.
Project Structure
The repository may evolve, but a typical Spring Boot layout is:
```text
VCLASS/
├── src/
│   └── main/
│       ├── java/                 # Spring Boot source code
│       └── resources/
│           ├── application.properties
│           └── templates/        # Thymeleaf templates
├── pom.xml                       # Maven dependencies and build settings
├── Dockerfile                    # Container build instructions
├── .gitignore
└── README.md
```
Security
Keep database credentials in environment variables or a secret manager.
Do not commit `.env` files, private certificates, SQL backups, or credentials.
Use a strong, unique database password.
If a password was committed to Git or shared, rotate it and consider removing it from repository history.
Use HTTPS and verify database server certificates in production.
Replace demo accounts and sample data before production use.
Troubleshooting
Application does not start
Confirm Java 17 is installed: `java -version`
Confirm Maven is available: `mvn -version`
Review the complete build and startup logs.
Ensure the application listens on the port supplied by the hosting platform.
Database connection fails
Verify `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
Confirm the database is reachable from the deployment platform.
Check the JDBC URL, port, database name, permissions, and TLS settings.
Never share the database password when requesting help.
Tables are missing
Confirm the connection points to `vclass_db`.
Review Hibernate startup logs and the configured schema strategy.
Verify the database user has the necessary permissions.









Author
Ramcharan Sai  
B.Tech Computer Science and Engineering  
Geetanjali College of Engineering and Technology
GitHub: @rc-exe12
---
VCLASS — A classroom management project focused on organizing courses, assignments, attendance, and submissions.
