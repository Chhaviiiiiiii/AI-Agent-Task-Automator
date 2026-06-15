# AI Agent Task Automator - Setup Guide

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git
- Google Gemini API Key (optional but recommended)

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://github.com/Chhaviiiiiiii/AI-Agent-Task-Automator.git
cd AI-Agent-Task-Automator
```

### 2. Set Up MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE ai_agent_db;
USE ai_agent_db;
```

### 3. Configure Environment Variables

Create a `.env` file in the root directory:

```bash
# Database Configuration
DATABASE_URL=jdbc:mysql://localhost:3306/ai_agent_db
DATABASE_USERNAME=root
DATABASE_PASSWORD=your_mysql_password

# Email Configuration (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Google Gemini API
GEMINI_API_KEY=your-gemini-api-key
```

### 4. Update Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  mail:
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

gemini:
  api:
    key: ${GEMINI_API_KEY}
```

### 5. Build the Project

```bash
mvn clean install
```

### 6. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR file
java -jar target/aiagent-1.0.0.jar
```

The application will start at `http://localhost:8080`

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn test -Dgroups=integration
```

## API Documentation

Once the application is running:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/v3/api-docs

## Google Gemini API Setup

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Copy the API key and set it in your `.env` file:
   ```bash
   GEMINI_API_KEY=your-api-key-here
   ```

## Troubleshooting

### MySQL Connection Issues

If you get `java.sql.SQLException: Access denied for user 'root'@'localhost'`:

1. Check your MySQL is running: `mysql -u root -p`
2. Verify database exists: `SHOW DATABASES;`
3. Update credentials in `application.yml`

### Email Configuration Issues

If emails are not sending:

1. For Gmail, enable "Less secure app access" or use App Password
2. Check MAIL_USERNAME and MAIL_PASSWORD in `.env`
3. Ensure port 587 is not blocked by firewall

### Gemini API Issues

If AI features are not working:

1. Verify API key is valid and active
2. Check API quotas in Google Cloud Console
3. Ensure network allows HTTPS requests to Google APIs

## Project Structure

```
AI-Agent-Task-Automator/
├── src/
│   ├── main/
│   │   ├── java/com/aiagent/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── config/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── AiAgentTaskAutomatorApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/aiagent/
├── pom.xml
├── .gitignore
├── README.md
└── SETUP.md
```

## Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -am 'Add your feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Submit a Pull Request

## License

MIT License - see LICENSE file for details

## Support

For issues or questions:
- Open an issue on GitHub
- Check existing documentation
- Contact maintainers

---

**Happy Automating! 🚀**
