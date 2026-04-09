# QuizFlow — Backend

A RESTful API backend for the QuizFlow online quiz application built with **Spring Boot 3.5**, **Spring Security**, **JWT Authentication**, and **MySQL**.

**Live API:** https://quizbackend-production-b31f.up.railway.app/api

**Swagger UI:** https://quizbackend-production-b31f.up.railway.app/swagger-ui/index.html

**Frontend:** https://quizapplication-frontend.netlify.app/

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming language |
| Spring Boot | 3.5.13 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | Database ORM |
| MySQL | 8.x | Relational database |
| JWT (jjwt) | 0.12.5 | Token-based authentication |
| Lombok | Latest | Boilerplate reduction |
| SpringDoc OpenAPI | 2.8.8 | Swagger UI documentation |
| JavaMailSender | Built-in | Email notifications |
| Maven | 3.x | Build tool |

---

## Project Structure

```
src/main/java/com/quizapp/quiz/
├── config/
│   ├── ApplicationConfig.java    # UserDetailsService, PasswordEncoder, AuthManager
│   ├── SecurityConfig.java       # HTTP security, JWT filter, route permissions
│   ├── WebConfig.java            # CORS configuration
│   └── OpenApiConfig.java        # Swagger/OpenAPI setup
├── controller/
│   ├── AuthController.java           # POST /api/auth/register, /api/auth/login
│   ├── AdminQuizController.java      # CRUD /api/admin/quizzes
│   └── ParticipantQuizController.java # GET quizzes, POST submit, GET history
├── dto/
│   ├── AuthenticationRequest.java
│   ├── AuthenticationResponse.java
│   ├── RegisterRequest.java
│   ├── QuizRequest.java
│   ├── QuestionRequest.java
│   ├── OptionRequest.java
│   ├── QuizSubmitRequest.java
│   ├── AnswerSubmitDTO.java
│   ├── QuizResultResponse.java
│   └── QuizAttemptDTO.java
├── model/
│   ├── User.java        # Implements UserDetails
│   ├── Role.java        # Enum: ADMIN, PARTICIPANT
│   ├── Quiz.java
│   ├── Question.java
│   ├── Option.java
│   └── QuizAttempt.java
├── repository/
│   ├── UserRepository.java
│   ├── QuizRepository.java
│   ├── QuestionRepository.java
│   ├── OptionRepository.java
│   └── QuizAttemptRepository.java
├── security/
│   ├── JwtService.java               # Token generation & validation
│   └── JwtAuthenticationFilter.java  # Per-request JWT filter
└── service/
    ├── AuthenticationService.java    # Register & login logic
    ├── QuizService.java              # Quiz CRUD operations
    ├── ParticipantService.java       # Quiz submission & history
    └── EmailService.java             # Email notifications
```

---

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.x** running locally
- A **Mailtrap** account (or any SMTP server) for email — optional, quiz submission works without it

---

## Getting Started

### 1. Create the MySQL Database

The database is created automatically when the app starts (via `createDatabaseIfNotExist=true`), but you can also create it manually:

```sql
CREATE DATABASE quiz_app_db;
```

### 2. Configure application properties

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_app_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JWT
jwt.secret=YOUR_SECRET_KEY_HERE
jwt.expiration=86400000

# Email (optional — quiz submission works without it)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=587
spring.mail.username=YOUR_MAILTRAP_USERNAME
spring.mail.password=YOUR_MAILTRAP_PASSWORD
```

> **Tip:** Use `application-local.properties` for secrets and never commit them to Git. See the Environment Variables section below.

### 3. Build and run

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run

# Or build a JAR first
./mvnw clean package
java -jar target/quiz-0.0.1-SNAPSHOT.jar
```

The server starts at **http://localhost:8080**

---

## API Endpoints

### Auth — Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### Admin — Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/quizzes` | Get all quizzes |
| POST | `/api/admin/quizzes` | Create a new quiz |
| GET | `/api/admin/quizzes/{id}` | Get quiz by ID |
| PUT | `/api/admin/quizzes/{id}` | Update a quiz |
| DELETE | `/api/admin/quizzes/{id}` | Delete a quiz |

### Participant — Requires `PARTICIPANT` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/participant/quizzes` | Get all available quizzes |
| GET | `/api/participant/quizzes/{id}` | Get a specific quiz to take |
| POST | `/api/participant/quizzes/{id}/submit` | Submit answers for grading |
| GET | `/api/participant/quizzes/history` | Get own quiz attempt history |

---

## Authentication

All protected endpoints require a `Bearer` JWT token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Obtain the token by calling `POST /api/auth/login`.

---

## Swagger UI

Interactive API documentation is available after starting the app:

```
http://localhost:8080/swagger-ui/index.html
```

Click **Authorize** and paste your JWT token to test protected endpoints.

---

## Environment Variables (Recommended for Production)

Instead of hardcoding secrets in `application.properties`, use `application-local.properties`:

**`src/main/resources/application-local.properties`** (do NOT commit this):
```properties
DB_URL=jdbc:mysql://localhost:3306/quiz_app_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=587
MAIL_USERNAME=your_mailtrap_user
MAIL_PASSWORD=your_mailtrap_pass
```

Activate with:
```properties
# In application.properties
spring.profiles.active=local
```

Add `application-local.properties` to your `.gitignore`.

---

## Database Schema

Tables created automatically by Hibernate (`ddl-auto=update`):

| Table | Description |
|---|---|
| `users` | User accounts with roles |
| `quizzes` | Quiz metadata (title, description, time limit) |
| `questions` | Questions belonging to a quiz |
| `options` | Answer options per question (with `is_correct` flag) |
| `quiz_attempts` | Records of participant quiz submissions and scores |

---

## Security Configuration

| Route Pattern | Access |
|---|---|
| `/api/auth/**` | Public |
| `/v3/api-docs/**` | Public |
| `/swagger-ui/**` | Public |
| `/api/admin/**` | ADMIN role only |
| All others | Any authenticated user |

Sessions are **stateless** — JWT tokens are validated on every request.

---

## Email Notifications

Emails are sent via `EmailService` for:
- **Registration** — Welcome email on new account creation
- **Quiz result** — Score summary after quiz submission

> Email failures are **caught and logged** — they never cause quiz submission to fail. If the mail server is unreachable, the quiz result is still returned normally.

---

## Loading Demo Data

A SQL seed file is included to populate the database with sample quizzes and users.

Run **after** the app has started at least once (so Hibernate creates the tables):

```bash
mysql -u root -p quiz_app_db < demo_data.sql
```

Demo login credentials (password: `Test@1234`):

| Email | Role |
|---|---|
| admin@quiz.com | ADMIN |
| alice@quiz.com | PARTICIPANT |
| bob@quiz.com | PARTICIPANT |

---

## CORS Configuration

The backend allows requests from `http://localhost:5173` (Vite dev server) by default.

To change this for production, update `src/main/java/com/quizapp/quiz/config/WebConfig.java`:

```java

registry.addMapping("/**")
    .allowedOrigins("https://your-frontend-domain.com")
    ...

```

---

