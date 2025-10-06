# 🎯 Quiz API

A full-featured REST API for an online quiz platform built with Spring Boot. Supports role-based access control, secure authentication, and comprehensive quiz management.

## 📸 Screenshots

<div align="center">
  <img width="800" alt="Swagger API Documentation" src="https://github.com/user-attachments/assets/513bebeb-d35b-4912-89c8-263555249a11" />
  <img width="800" alt="Database Schema" src="https://github.com/user-attachments/assets/3db58e9f-d4b1-4103-b8e0-bcf9a3b8ffca" />
  <img width="800" alt="Quiz Endpoints" src="https://github.com/user-attachments/assets/203f2237-3644-496f-9355-fc232b807ade" />
  <img width="800" alt="Authentication Flow" src="https://github.com/user-attachments/assets/3e54171e-c6df-48f1-a0a5-e9f7c6e775df" />
  <img width="800" alt="Quiz Attempt History" src="https://github.com/user-attachments/assets/bfc7f100-81cf-4a35-9f5a-fe3c262a5020" />
  <img width="800" alt="User Dashboard" src="https://github.com/user-attachments/assets/75efe329-5499-41dd-a6ff-8b5ad22b1fcd" />
  <img width="800" alt="Admin Panel" src="https://github.com/user-attachments/assets/6b4e27c3-8f90-49f2-b149-cebdeb261b43" />
</div>

## ✨ Features

- **Role-Based Access Control** - Admin and User roles with different permissions
- **JWT Authentication** - Secure token-based authentication
- **Quiz Management** - Create, publish, and manage quizzes (Admin only)
- **Question Types** - Support for single choice, multiple choice, and text questions
- **Automatic Scoring** - Real-time quiz evaluation 
- **Attempt History** - Track user performance and quiz attempts
- **Clean Architecture** - Modular design with separation of concerns
- **Exception Handling** - Comprehensive error handling with meaningful responses
- **Database Design** - Normalized PostgreSQL schema with proper relationships
- **Dockerized** - Fully containerized with Docker Compose
- **API Documentation** - Interactive Swagger UI for testing endpoints

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.5, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **Documentation**: Springdoc OpenAPI (Swagger)
- **Testing**: JUnit, Mockito

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Maven (for local development)

### Quick Start with Docker
```bash
# Clone the repository
git clone https://github.com/yourusername/quiz-api.git
cd quiz-api

# Start the application
docker-compose up --build

# Access the API
# Swagger UI: http://localhost:8080/swagger-ui/index.html
# API Docs: http://localhost:8080/v3/api-docs
