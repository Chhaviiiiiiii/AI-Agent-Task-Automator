# AI Agent Task Automator 🤖

A smart task automation platform powered by Google Gemini AI. This application allows users to create, manage, and automate tasks with AI-powered assistance.

## Features ✨

- **User Authentication**: Secure registration and login with JWT tokens
- **Task Management**: Create, read, update, and manage tasks
- **Priority Levels**: Tasks can be assigned different priority levels (Low, Medium, High, Urgent)
- **Task Status Tracking**: Monitor task progress (Pending, In Progress, Completed, Failed, Cancelled)
- **AI Integration**: Powered by Google Gemini AI for intelligent task automation
- **Email Notifications**: Get notified about task updates
- **RESTful API**: Clean and well-documented API endpoints
- **Swagger Documentation**: Interactive API documentation

## Tech Stack 🛠️

### Backend
- **Java 17**: Latest Java LTS version
- **Spring Boot 3.2.5**: Latest Spring Boot framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database operations
- **MySQL**: Relational database
- **JWT (JJWT)**: Token-based authentication
- **Lombok**: Code generation and boilerplate reduction
- **SpringDoc OpenAPI**: Swagger API documentation

## Project Structure 📂

```
src/
├── main/
│   ├── java/com/aiagent/
│   │   ├── AiAgentTaskAutomatorApplication.java (Main entry point)
│   │   ├── config/
│   │   │   ├── JwtConfig.java
│   │   │   ├── SecurityConfig.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── Task.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── TaskRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── TaskService.java
│   │   │   └── JwtService.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   └── TaskController.java
│   │   ├── dto/
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── TaskRequest.java
│   │   │   └── TaskResponse.java
│   │   └── apiresponse/
│   │       └── ApiResponse.java
│   └── resources/
│       └── application.yml (Configuration)
└── test/ (Unit and integration tests)
```

## Prerequisites 📋

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git

## Installation & Setup 🚀

### 1. Clone the Repository
```bash
git clone https://github.com/Chhaviiiiiiii/AI-Agent-Task-Automator.git
cd AI-Agent-Task-Automator
```

### 2. Set Up MySQL Database
```sql
CREATE DATABASE ai_agent_db;
```

### 3. Configure Application
Edit `src/main/resources/application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_agent_db
    username: root
    password: your_password
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

## API Endpoints 📡

### Authentication
- **POST** `/api/auth/register` - Register a new user
- **POST** `/api/auth/login` - Login and get JWT token

### Tasks
- **GET** `/api/tasks` - Get all tasks for the authenticated user
- **GET** `/api/tasks/{taskId}` - Get a specific task
- **POST** `/api/tasks` - Create a new task

### API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

## Authentication 🔐

This application uses JWT (JSON Web Tokens) for authentication.

### How to Use JWT:
1. Register a new user or login
2. Copy the `token` from the response
3. Add it to the Authorization header: `Authorization: Bearer <your_token>`

## Database Schema 🗄️

### Users Table
```sql
- id (PK, Auto-increment)
- username (Unique)
- email (Unique)
- password (Encrypted)
- first_name
- last_name
- is_active
- created_at
- updated_at
```

### Tasks Table
```sql
- id (PK, Auto-increment)
- user_id (FK)
- title
- description
- status (PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED)
- priority (LOW, MEDIUM, HIGH, URGENT)
- result
- ai_response
- created_at
- updated_at
- completed_at
```

## Future Enhancements 🔮

- [ ] Google Gemini AI integration for task automation
- [ ] Email notifications for task updates
- [ ] Task scheduling and automation workflows
- [ ] WebSocket support for real-time updates
- [ ] File upload/attachment support
- [ ] Advanced filtering and search
- [ ] Task templates and workflows
- [ ] Team collaboration features

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact 📧

For any queries or suggestions, please reach out to the maintainer.

---

**Happy Automating! 🚀**
