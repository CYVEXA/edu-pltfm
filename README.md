# CYVEXA Learning Platform

A modern, clean, soft UI learning platform built with Spring Boot, MySQL, and Vanilla CSS.

## Features
- **Neumorphic UI**: Soft shadows and smooth transitions.
- **Responsive Design**: Works on mobile and desktop.
- **Student Dashboard**: Track progress and continue courses.
- **Admin Panel**: Add and manage courses.
- **Restricted Access**: Courses require login.

## Tech Stack
- **Backend**: Spring Boot 3, Spring Data JPA
- **Database**: MySQL
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)

## Setup Instructions

### 1. Database Configuration
1. Ensure MySQL is running.
2. The application will attempt to create `cyvexa_db` automatically.
3. If you need to change the password, edit `src/main/resources/application.properties`.

### 2. Run the Application
If you have Maven installed:
```bash
mvn spring-boot:run
```
Alternatively, open the project in your favorite IDE (IntelliJ/VS Code) and run `CyvexaApplication.java`.

### 3. Login Credentials (Demo)
- **Student**: Register via the student login page (or use dummy login).
- **Admin**: 
  - Username: `admin`
  - Password: `admin123`

## UI/UX Highlights
- **Milky White Background**: `#f0f4f8`
- **Primary Color**: Light Blue (`#a0d8ef`)
- **Soft Shadows**: Neumorphic style for cards and buttons.
