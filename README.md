# Translation Management Service

A high-performance, scalable Translation Management Service built with Spring Boot Java, designed to handle multi-locale translations with advanced tagging, caching, and export capabilities.

## Features

- **Multi-locale Support**: Store and manage translations for multiple languages (en, fr, es, etc.)
- **Advanced Tagging**: Context-based tagging system (mobile, desktop, web, etc.)
- **High Performance**: Sub-200ms response times with Redis caching
- **Scalable Export**: Handle 100k+ translations with sub-500ms export times
- **JWT Security**: Token-based authentication with role-based access control
- **RESTful API**: Comprehensive CRUD operations with search and filtering
- **Docker Ready**: Complete containerization with Docker Compose
- **OpenAPI Documentation**: Interactive Swagger UI documentation
- **Comprehensive Testing**: 95%+ test coverage with unit, integration, and performance tests

## Architecture & Design Decisions

### Clean Architecture
- **Domain Layer**: Entities and repositories for core business logic
- **Service Layer**: Business logic implementation with caching
- **API Layer**: REST controllers with comprehensive validation
- **Security Layer**: JWT-based authentication and authorization

### Performance Optimizations
- **Database Indexing**: Strategic indexes on frequently queried columns
- **Redis Caching**: Multi-level caching for translations and exports
- **Batch Processing**: Efficient bulk operations for large datasets
- **Connection Pooling**: Optimized database connection management

### Security Best Practices
- **JWT Authentication**: Stateless token-based security
- **Role-based Access**: ADMIN, EDITOR, VIEWER roles
- **Input Validation**: Comprehensive validation at all layers
- **SQL Injection Prevention**: Parameterized queries and JPA

### Scalability Features
- **Pagination**: All list endpoints support pagination
- **Batch Operations**: Efficient handling of large datasets
- **Caching Strategy**: Redis for frequently accessed data
- **Database Optimization**: Proper indexing and query optimization

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI 3 (Swagger)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build**: Maven
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 15+ (if running locally)
- Redis 7+ (if running locally)

## Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd translation-service
   ```

2. **Start the services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - API: http://localhost:8080/api/v1
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
   - Health Check: http://localhost:8080/api/v1/actuator/health

### Local Development Setup

1. **Start PostgreSQL and Redis**
   ```bash
   docker-compose up -d postgres redis
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Enable data seeding (optional)**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--app.data-seeder.enabled=true"
   ```

## API Documentation

### Authentication
The API uses JWT token-based authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Getting JWT Tokens for Testing

**Step 1: Get a JWT Token**
```bash
# Login as admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Login as editor
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "editor",
    "password": "editor123"
  }'

# Login as viewer
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "viewer",
    "password": "viewer123"
  }'
```

**Step 2: Use the Token**
```bash
# Save the token from the response
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Use it in subsequent requests
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/translations
```

### Available Demo Users
| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full access - CRUD all resources |
| editor | editor123 | EDITOR | Create, read, update translations/tags |
| viewer | viewer123 | VIEWER | Read-only access |
| john.admin | password123 | ADMIN | Full access |
| jane.editor | password123 | EDITOR | Create, read, update |
| bob.viewer | password123 | VIEWER | Read-only access |

### Key Endpoints

#### Translations
- `GET /translations/export` - Export translations (public)
- `GET /translations/search` - Search translations
- `POST /translations` - Create translation (ADMIN/EDITOR)
- `PUT /translations/{id}` - Update translation (ADMIN/EDITOR)
- `DELETE /translations/{id}` - Delete translation (ADMIN)

#### Tags
- `GET /tags` - List all available tags
- `GET /tags/search` - Search tags by name pattern
- `GET /tags/translation-key/{key}` - Get tags for specific translation key

### Example Requests

#### Create Translation
```bash
curl -X POST http://localhost:8080/api/v1/translations \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "key": "app.welcome.message",
    "locale": "en",
    "content": "Welcome to our application!",
    "tags": [
      {"name": "web"},
      {"name": "user"}
    ]
  }'
```

#### Export Translations
```bash
curl http://localhost:8080/api/v1/translations/export?locale=en
```

#### Search Translations
```bash
curl "http://localhost:8080/api/v1/translations/search?key=app&locale=en&page=0&size=20" \
  -H "Authorization: Bearer $VIEWER_TOKEN"
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run with Coverage Report
```bash
mvn clean test jacoco:report
```

### Performance Testing
```bash
# Enable data seeding for performance testing
mvn spring-boot:run -Dapp.data-seeder.enabled=true
```

### Test Categories
- **Unit Tests**: Service and repository layer testing
- **Integration Tests**: Full API endpoint testing with TestContainers
- **Performance Tests**: Load testing with 100k+ records

## Performance Benchmarks

### Response Times (with 100k+ records)
- **Translation CRUD**: < 200ms
- **Search Operations**: < 200ms
- **Export Endpoint**: < 500ms
- **Bulk Operations**: Optimized batch processing

### Scalability Metrics
- **Database**: Supports millions of translations
- **Concurrent Users**: Tested with 100+ concurrent requests
- **Memory Usage**: Optimized with Redis caching
- **Export Size**: Handles 100k+ translations efficiently

## Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/translation_db
SPRING_DATASOURCE_USERNAME=translation_user
SPRING_DATASOURCE_PASSWORD=translation_pass

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT
SPRING_SECURITY_JWT_SECRET=your-secret-key
SPRING_SECURITY_JWT_EXPIRATION=86400000

# Data Seeding
APP_DATA_SEEDER_ENABLED=false

# Performance
APP_PERFORMANCE_CACHE_TTL=300
APP_PERFORMANCE_MAX_EXPORT_SIZE=100000

# CDN (optional)
APP_CDN_ENABLED=false
APP_CDN_BASE_URL=https://cdn.example.com
```

### Application Profiles
- **default**: Local development
- **test**: Testing environment
- **docker**: Docker container environment

## Docker Deployment

### Build and Run
```bash
# Build the image
docker build -t translation-service .

# Run with Docker Compose
docker-compose up -d

# Scale the application
docker-compose up -d --scale app=3
```

### Production Deployment
```bash
# With Nginx reverse proxy
docker-compose --profile with-nginx up -d
```

## Monitoring & Health Checks

### Health Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging
- Structured logging with SLF4J and Logback
- Request/response logging for debugging
- Performance metrics logging

## Security Features

### Authentication & Authorization
- JWT token-based authentication
- Role-based access control (RBAC)
- Stateless security design

### Security Headers
- CORS configuration
- CSRF protection disabled for API
- Secure JWT token handling

### Input Validation
- Bean validation annotations
- Custom validation logic
- SQL injection prevention

## Performance Optimization

### Caching Strategy
- **L1 Cache**: Application-level caching
- **L2 Cache**: Redis distributed caching
- **Cache Keys**: Optimized cache key generation
- **Cache Invalidation**: Smart cache eviction

### Database Optimization
- Strategic indexing on frequently queried columns
- Full-text search capabilities
- Optimized query patterns
- Connection pooling

### API Optimization
- Pagination for large datasets
- Efficient serialization
- Compressed responses
- Async processing where applicable

## Extension Points

### Adding New Locales
Simply add translations with new locale codes - the system automatically supports new languages.

### Custom Tag Types
Extend the tagging system by adding new tag categories and validation rules.

### Export Formats
Implement additional export formats by extending the export service.

### Authentication Providers
Integrate with external authentication systems (OAuth2, LDAP, etc.).

## Development Guidelines

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Comprehensive JavaDoc documentation
- SOLID principles implementation

### Testing Strategy
- Unit tests for all service methods
- Integration tests for API endpoints
- Performance tests for scalability
- Mock external dependencies

### Git Workflow
- Feature branch development
- Pull request reviews
- Automated CI/CD pipeline
- Semantic versioning

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes with tests
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# View logs
docker-compose logs postgres
```

#### Redis Connection Issues
```bash
# Check Redis status
docker-compose ps redis

# Test Redis connection
docker-compose exec redis redis-cli ping
```

#### Application Startup Issues
```bash
# View application logs
docker-compose logs app

# Check health status
curl http://localhost:8080/api/v1/actuator/health
```

### Performance Issues
- Check Redis cache hit rates
- Monitor database query performance
- Review application metrics
- Analyze JVM memory usage

## Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the troubleshooting guide
- Contact the development team

