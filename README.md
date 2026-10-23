# VClass — Virtual Classroom

Fresh Spring Boot + Thymeleaf + JPA project with a responsive dark glass interface.

## Requirements
Java 17+ and Maven.

## Run (works without MySQL)
```bash
mvn spring-boot:run
```
Open http://localhost:8080

Demo accounts:
- Teacher: teacher@demo.com / teacher123
- Student: student@demo.com / student123

Default storage is a local H2 database. To use MySQL, create a database named `vclass`, edit `application-mysql.properties`, then run:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Features: demo login, dashboard, course creation, assignment creation, student submissions, teacher grading, attendance records, dark/light/system theme selection.

This is an academic starter, not production authentication. Demo passwords are for local demonstrations only.
