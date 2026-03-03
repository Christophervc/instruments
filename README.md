# Instruments API

> Bilingual README (English first, Spanish below).

## English

### Project description
Instruments API is a Spring Boot REST backend for a musical instruments store. It supports catalog management, manufacturers, users, authentication with JWT, and order management. The API also supports product image uploads to MinIO, Excel export for products, Redis cache, and Redis-based rate limiting.

### Tech stack
- **Java 21**
- **Spring Boot 3.3.3**
- Spring Web, Spring Data JPA, Spring Validation
- Spring Security + JWT (jjwt)
- PostgreSQL
- Redis (cache + distributed rate limit with Bucket4j)
- MinIO (object storage for product images)
- MapStruct + Lombok
- OpenAPI/Swagger (springdoc)
- Apache POI (Excel export)
- Maven
- Docker Compose (Postgres, Redis, MinIO)

### Main libraries used
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-cache`
- `springdoc-openapi-starter-webmvc-ui`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- `bucket4j-core`, `bucket4j-redis`
- `io.minio:minio`
- `org.mapstruct:mapstruct`
- `org.apache.poi:poi-ooxml`

### Key features
- JWT authentication and role-based authorization (`CUSTOMER`, `STAFF`, `ADMIN`).
- Product CRUD with filtering, pagination, soft-delete and image upload.
- Product catalog export to Excel.
- Manufacturer management.
- Order creation and status flow (`PENDING`, `PAID`, `DELIVERED`, `CANCELLED`).
- User profile management + admin/staff user administration.
- Redis cache with TTL.
- Rate limiting via Bucket4j + Redis (including special handling for heavy endpoints).
- MinIO integration for file storage.
- OpenAPI docs with Swagger UI.
- Dev data seeding (sample users/products/manufacturers in `dev` profile).

### API docs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### Authentication
Use Bearer JWT in protected endpoints:

```http
Authorization: Bearer <your_token>
```

---

## Endpoints

### Auth (`/api/v1/auth`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a customer user |
| POST | `/login` | Public | Login and receive JWT |
| GET | `/me` | Authenticated | Get current user profile |
| PUT | `/change-password` | Authenticated | Change current user password |

### Users (`/api/v1/users`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | ADMIN, STAFF | List users (paginated) |
| GET | `/{id}` | ADMIN, STAFF | Get user by id |
| PUT | `/{id}/role` | ADMIN | Change user role |
| PUT | `/{id}/status` | ADMIN | Activate/deactivate user |
| PUT | `/me` | Authenticated | Update own profile |

### Products (`/api/v1/products`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | Public | List products with filters + pagination |
| GET | `/{id}` | Public | Get product by UUID |
| GET | `/sku/{sku}` | Public | Get product by SKU |
| GET | `/export/excel` | ADMIN, STAFF | Export product catalog to Excel |
| POST | `/` (`multipart/form-data`) | ADMIN, STAFF | Create product (optional image file) |
| POST | `/{id}/image` (`multipart/form-data`) | ADMIN, STAFF | Upload product image |
| PUT | `/{id}` | ADMIN, STAFF | Update product |
| DELETE | `/{id}` | ADMIN | Soft delete product |

Product filters (`GET /api/v1/products`):
- `name`
- `minPrice`
- `maxPrice`
- `manufacturer`
- `type` (`ELECTRIC_GUITAR`, `ACOUSTIC_GUITAR`, `BASS`)
- plus pageable params (`page`, `size`, `sort`)

### Manufacturers (`/api/v1/manufacturers`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | Public | List manufacturers (optional `name` filter) |
| GET | `/{id}` | Public | Get manufacturer by UUID |
| POST | `/` | ADMIN, STAFF | Create manufacturer |
| PUT | `/{id}` | ADMIN, STAFF | Update manufacturer |

### Orders (`/api/v1/orders`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | CUSTOMER, STAFF, ADMIN | List order history (paginated + filters) |
| GET | `/{id}` | CUSTOMER, STAFF, ADMIN | Get order details |
| POST | `/` | CUSTOMER, STAFF, ADMIN | Create order |
| PATCH | `/{id}/cancel` | ADMIN, STAFF | Cancel order |
| PATCH | `/{id}/status` | ADMIN, STAFF | Update order status |

Order filters (`GET /api/v1/orders`):
- `status` (`PENDING`, `PAID`, `DELIVERED`, `CANCELLED`)
- `startDate`
- `endDate`
- `customerDni`
- plus pageable params (`page`, `size`, `sort`)

---

## How to run the project

### 1) Prerequisites
- Java 21
- Docker + Docker Compose
- Maven (or use `./mvnw`)

### 2) Start infrastructure services
Copy env template and start containers:

```bash
cp .env.template .env
docker compose up -d
```

This starts:
- PostgreSQL on `localhost:5432`
- Redis on `localhost:6379`
- MinIO API on `localhost:9000` and console on `localhost:9001`

### 3) Define application environment variables
Set these variables before starting Spring Boot:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/instruments_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=secret123

export JWT_SECRET_KEY=<BASE64_SECRET_MIN_256_BITS>
export JWT_TIME_EXPIRATION=86400000

export MINIO_URL=http://localhost:9000
export MINIO_ACCESS_KEY=minioadmin
export MINIO_SECRET_KEY=minioadmin
export MINIO_BUCKET_NAME=instruments
```

> Notes:
> - Keep `JWT_SECRET_KEY` as a Base64 value.
> - Redis rate limit currently expects Redis in `localhost:6379`.

### 4) Run the API

```bash
./mvnw spring-boot:run
```

The app runs at:
- `http://localhost:8080`

### 5) Seed data (dev profile)
With `spring.profiles.active=dev` (already configured), the app seeds demo data on first run:
- 100 products
- 2 manufacturers
- Demo users:
  - `admin@instruments.com` / `admin_password123`
  - `staff@instruments.com` / `staff_password123`
  - `juan@email.com` / `password123`
  - `maria@email.com` / `password123`

---

## Español

### Descripción del proyecto
Instruments API es un backend REST en Spring Boot para una tienda de instrumentos musicales. Incluye gestión de catálogo, fabricantes, usuarios, autenticación con JWT y gestión de órdenes. También incorpora subida de imágenes a MinIO, exportación de productos a Excel, cache con Redis y rate limiting distribuido.

### Stack tecnológico
- **Java 21**
- **Spring Boot 3.3.3**
- Spring Web, Spring Data JPA, Spring Validation
- Spring Security + JWT (jjwt)
- PostgreSQL
- Redis (cache + rate limiting con Bucket4j)
- MinIO (almacenamiento de imágenes)
- MapStruct + Lombok
- OpenAPI/Swagger (springdoc)
- Apache POI (exportación Excel)
- Maven
- Docker Compose (Postgres, Redis, MinIO)

### Librerías principales utilizadas
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-cache`
- `springdoc-openapi-starter-webmvc-ui`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- `bucket4j-core`, `bucket4j-redis`
- `io.minio:minio`
- `org.mapstruct:mapstruct`
- `org.apache.poi:poi-ooxml`

### Funcionalidades clave
- Autenticación JWT y autorización por roles (`CUSTOMER`, `STAFF`, `ADMIN`).
- CRUD de productos con filtros, paginación, soft-delete y carga de imágenes.
- Exportación del catálogo a Excel.
- Gestión de fabricantes.
- Creación de órdenes y flujo de estados (`PENDING`, `PAID`, `DELIVERED`, `CANCELLED`).
- Gestión de perfil de usuario + administración de usuarios por admin/staff.
- Cache en Redis con TTL.
- Rate limiting con Bucket4j + Redis (incluye tratamiento para endpoints pesados).
- Integración con MinIO para archivos.
- Documentación OpenAPI con Swagger UI.
- Seeder de datos en perfil `dev`.

### Documentación API
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### Autenticación
Usa JWT Bearer en endpoints protegidos:

```http
Authorization: Bearer <tu_token>
```

### Endpoints
Los endpoints son exactamente los mismos listados en la sección en inglés (Auth, Users, Products, Manufacturers y Orders), con los mismos métodos, rutas y reglas de acceso por rol.

### Cómo levantar el proyecto
1. **Prerequisitos**: Java 21, Docker, Docker Compose, Maven.
2. **Levantar infraestructura**:
   ```bash
   cp .env.template .env
   docker compose up -d
   ```
3. **Definir variables de entorno** (datasource, JWT y MinIO) como en la sección en inglés.
4. **Ejecutar la API**:
   ```bash
   ./mvnw spring-boot:run
   ```
5. **Acceder a Swagger** en `http://localhost:8080/swagger-ui.html`.

