# Shorty - URL Shortener

## Tech Stack

- **Language:** Java 25 (Amazon Corretto)
- **Framework:** Spring Boot 4.0.3
- **Build:** Gradle 8.14.4
- **Database:** Redis
- **Template Engine:** Thymeleaf + Spring Security dialect
- **Security:** Spring Security 7 (form login)
- **Libraries:** Slugify 3.0.7, Lombok, ArchUnit 1.4.1

## Build & Test Commands

```bash
./gradlew build        # Build the project
./gradlew check        # Run all checks (compile + tests)
./gradlew bootRun      # Run the application (requires Redis)
./gradlew bootJar      # Build executable JAR
./gradlew test         # Run tests only
```

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SHORTY_DB_HOST` | `localhost` | Redis host |
| `SHORTY_DB_PORT` | `6379` | Redis port |
| `SHORTY_ADMIN` | `admin` | Admin username |
| `SHORTY_ADMIN_PASSWORD` | `1234` | Admin password |

## Architecture

Standard Spring Boot MVC application with Redis persistence:

```
src/main/java/de/hhu/propra/link/
├── LinkApplication.java          # Entry point
├── controllers/
│   └── LinkController.java       # Web controller (session-scoped)
├── entities/
│   └── Link.java                 # Domain entity (RedisHash)
├── repositories/
│   └── LinkRepository.java       # Spring Data Redis repository
├── security/
│   └── SecurityConfiguration.java # SecurityFilterChain config
├── services/
│   ├── AbbreviationService.java  # URL abbreviation logic
│   └── LinkService.java          # CRUD operations
├── util/
│   └── StringUtil.java           # Slugify + string helpers
└── validation/
    ├── UnreservedAbbreviation.java          # Custom constraint annotation
    └── UnreservedAbbreviationValidator.java # Constraint validator
```

## Key Endpoints

- `GET /` — Index page (create short links)
- `POST /` — Create new short link
- `GET /{abbreviation}` — Redirect to original URL
- `POST /{abbreviation}/delete` — Delete link (ADMIN only)
- `GET /login` — Login page
- `GET /admin` — Admin page (ADMIN only)

## Docker

```bash
docker compose up      # Run with Redis (uses production.env)
docker build .         # Build Docker image
```
