# Phone Model API Integration

## Overview
This implementation adds functionality to your Spring Boot application to fetch phone model data from an external REST API (https://api.restful-api.dev/objects/{id}) and store it in a PostgreSQL database.

## Implementation Details

### 1. **PhoneModel Entity** (`model/PhoneModel.java`)
- JPA entity that maps to the `phone_models` table in the `ecommerce` schema
- Fields:
  - `id`: String (Primary Key)
  - `name`: Phone model name
  - `year`: Manufacturing year
  - `price`: Price in USD
  - `cpuModel`: CPU model name
  - `hardDiskSize`: Hard disk storage capacity

### 2. **PhoneModelRepository** (`repository/PhoneModelRepository.java`)
- Spring Data JPA repository extending `JpaRepository<PhoneModel, String>`
- Provides standard CRUD operations for the PhoneModel entity

### 3. **PhoneModelService** (`service/PhoneModelService.java`)
- Core business logic for handling phone model operations
- Uses **RestClient** (Spring Framework 6.1+) for HTTP operations
- **Key Methods:**
  - `getPhoneModelById(String id)`: 
    - First checks if data exists in the database
    - If not found, calls the external API using RestClient
    - Parses JSON response and saves to database
    - Returns the PhoneModel object
  
  - `refreshPhoneModel(String id)`:
    - Deletes existing record if present
    - Calls the external API to get fresh data
    - Saves updated data to database
    - Returns the refreshed PhoneModel

- Handles JSON parsing from the API response with the following structure:
  ```json
  {
    "id": "7",
    "name": "Apple MacBook Pro 16",
    "data": {
      "year": 2019,
      "price": 1849.99,
      "CPU model": "Intel Core i9",
      "Hard disk size": "1 TB"
    }
  }
  ```

### 4. **AppConfig** (`config/AppConfig.java`)
- Configuration class providing bean definitions:
  - `RestClient`: Modern Spring HTTP client for making API calls (replaces RestTemplate)
  - `ObjectMapper`: For JSON parsing and serialization

### 5. **PhoneModelController** (`controller/PhoneModelController.java`)
- REST controller with the following endpoints:

#### GET `/phone-model/{id}`
- Fetches phone model by ID
- First checks database, then calls API if not found
- Returns the PhoneModel object

```bash
curl http://localhost:8080/phone-model/7
```

#### POST `/phone-model/{id}/refresh`
- Forces a refresh of phone model data from the API
- Removes old data and fetches fresh information
- Returns the updated PhoneModel object

```bash
curl -X POST http://localhost:8080/phone-model/7/refresh
```

### 6. **Database Migrations with Liquibase (SQL-based)**
- **Master Changelog**: `db/changelog/db.changelog-master.xml`
  - XML orchestrator that includes all SQL migration files
  - Acts as entry point for Liquibase
  
- **SQL Migration Files**: `db/changelog/001_create_phone_models_table.sql`
  - Standard SQL migration using Liquibase formatted SQL syntax
  - Uses changeset annotations to define migration units
  - Includes rollback statements for reversibility
  - Creates the `phone_models` table with all required columns
  - Creates an index on the `name` column
  - Preconditions prevent duplicate table creation
  - Format:
    ```sql
    --liquibase formatted sql
    --changeset author:id
    -- SQL statements here
    --rollback SQL to revert changes
    ```

### 7. **Dependencies Updated** (`pom.xml`)
- Replaced Flyway with **Liquibase Core** for database version control
- Removed `flyway-core` and `flyway-database-postgresql`
- Added `liquibase-core` for comprehensive migration management

### 8. **Configuration** (`application.yaml`)
- Added Liquibase configuration:
  ```yaml
  spring:
    liquibase:
      change-log: classpath:db/changelog/db.changelog-master.xml
      enabled: true
      default-schema: ecommerce
  ```

## How It Works

1. **First Request**: User requests GET `/phone-model/7`
   - Controller receives request
   - Service checks if ID `7` exists in database
   - Data not found, so service calls external API using RestClient
   - API response is parsed and saved to database
   - Response returned to user

2. **Subsequent Requests**: User requests GET `/phone-model/7` again
   - Controller receives request
   - Service finds data in database
   - Returns immediately without calling external API

3. **Refresh Request**: User requests POST `/phone-model/7/refresh`
   - Old data is deleted from database
   - Fresh data is fetched from external API
   - New data is saved to database
   - Response returned to user

## Configuration Notes

- The application uses PostgreSQL with the schema `ecommerce`
- Connection details are configured in `application.yaml`
- **Liquibase** automatically runs migrations on application startup
- Liquibase tracks applied migrations in the `DATABASECHANGELOG` table
- JPA Hibernate is set to `ddl-auto: none` since Liquibase manages the schema
- RestClient is a modern, fluent API for HTTP operations (available in Spring Framework 6.1+)

## RestClient vs RestTemplate

**RestClient** advantages:
- Modern, fluent API with better readability
- Built-in support for custom error handling per request
- Preferred approach in Spring Framework 6.1+
- Better performance and memory efficiency
- Type-safe method chaining

## Liquibase vs Flyway

**Liquibase advantages:**
- More flexible with multiple changelog formats (XML, YAML, JSON, SQL)
- Better support for rollbacks with explicit rollback statements
- More granular control over migrations
- Stronger precondition support
- More suitable for complex database changes
- SQL-based migrations are easy to read and maintain

## Error Handling

- Exceptions during API calls are logged and re-thrown as RuntimeException
- Database operations are logged for debugging
- Check application logs for detailed error messages

## Testing the API

You can test using curl or Postman:

```bash
# Get phone entity (fetches from API if not in DB)
curl http://localhost:8080/phone-model/7

# Refresh phone entity data
curl -X POST http://localhost:8080/phone-model/7/refresh

# Try different IDs
curl http://localhost:8080/phone-model/1
curl http://localhost:8080/phone-model/13
```

## Database Migration Management

### SQL Migration File Format

Liquibase SQL files use a specific format with comments to define changesets:

```sql
--liquibase formatted sql
--changeset author:changesetId description:"Description of change"
SQL statements here;
--rollback SQL to revert the change;

--changeset author:changesetId2 description:"Another change"
Additional SQL statements;
--rollback Rollback SQL;
```

**Key Components:**
- `--liquibase formatted sql`: Marks the file as a Liquibase SQL migration
- `--changeset author:id`: Defines a unique changeset (author and id must be unique together)
- `--rollback`: SQL to execute when rolling back this changeset
- `--precondition-sql-check`: Optional precondition to check before applying the changeset

### Current Migrations

**File**: `db/changelog/001_create_phone_models_table.sql`
- **Changeset 1**: Creates the `phone_models` table
  - Precondition checks if table already exists
  - Includes rollback to drop the table
  
- **Changeset 2**: Creates index on `name` column
  - Improves query performance
  - Includes rollback to drop the index

To verify migrations have been applied:

```sql
-- Check applied migrations
SELECT * FROM DATABASECHANGELOG;

-- Check phone_models table
SELECT * FROM ecommerce.phone_models;
```

## Future Enhancements

- Add error handling for invalid IDs
- Implement caching with TTL
- Add pagination for batch operations
- Implement async processing for large datasets
- Add request validation
- Implement rate limiting for API calls
- Add more migration scripts as schema evolves

