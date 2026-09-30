# URL Shortener — Step-by-Step Tutorial (Spring Boot + JPA + SQL)

A beginner/intermediate-friendly walkthrough for building a working URL shortener like bit.ly,
using Java, Spring Boot, and a SQL database.

---

## 1. What we're building

Two main behaviors:

1. **Shorten**: `POST /api/shorten` with `{ "url": "https://very-long-link.com/..." }`
   → returns a short code like `aZ3xQ1`, so the short link becomes `http://localhost:8080/aZ3xQ1`.
2. **Redirect**: When someone visits `http://localhost:8080/aZ3xQ1` in a browser, our server
   looks up the original URL in the database and redirects them there, while counting the click.

We'll also add a small stats endpoint so you can check how many times a link was clicked.

---

## 2. Prerequisites

- **Java 17+** installed (`java -version` to check)
- **Maven 3.8+** installed (`mvn -version` to check) — or use an IDE that bundles it
- An IDE: **IntelliJ IDEA** (Community edition is fine) or VS Code with the Java extension pack
- A REST client to test with: **Postman**, **Insomnia**, or just `curl` from the terminal

You do **not** need MySQL installed to get started — this project uses **H2**, an in-memory
SQL database that needs zero setup, so you can run it immediately. Section 9 shows how to
switch to real MySQL later.

---

## 3. Project structure

```
url-shortener/
├── pom.xml                          # Maven dependencies & build config
└── src
    ├── main
    │   ├── java/com/example/urlshortener
    │   │   ├── UrlShortenerApplication.java     # main() entry point
    │   │   ├── entity/UrlMapping.java           # maps to the SQL table
    │   │   ├── repository/UrlMappingRepository.java  # DB access
    │   │   ├── service/UrlShortenerService.java # business logic
    │   │   ├── controller/UrlController.java    # REST endpoints
    │   │   ├── dto/ShortenRequest.java          # incoming JSON shape
    │   │   ├── dto/ShortenResponse.java         # outgoing JSON shape
    │   │   └── exception/                       # custom errors + handler
    │   └── resources/application.properties     # DB & app config
    └── test/java/com/example/urlshortener/UrlShortenerServiceTest.java
```

This is a standard **layered architecture**, which you'll see in almost every real-world
Spring Boot project:

```
Controller  →  Service  →  Repository  →  Database
(HTTP layer)  (business    (data access)
              logic)
```

Each layer only knows about the one below it. The Controller never talks to the database
directly — it always goes through the Service.

---

## 4. How the pieces fit together

### `pom.xml` — the project's dependency list
This tells Maven what libraries to download. Key ones:
- `spring-boot-starter-web` — lets us build REST APIs and run an embedded web server
- `spring-boot-starter-data-jpa` — lets us use Java objects to read/write SQL, instead of
  writing raw SQL and JDBC boilerplate ourselves
- `h2` — an embeddable SQL database (like SQLite, but pure Java)
- `mysql-connector-j` — the driver you'd use if you switch to real MySQL later

### `UrlMapping.java` — the entity (this **is** your SQL table)
The `@Entity` annotation tells Spring: "this class corresponds to a database table."
Each field becomes a column. `@Id` + `@GeneratedValue` means the database auto-generates
the primary key (1, 2, 3, ...) for us — we never set it manually.

You never wrote `CREATE TABLE url_mapping (...)` anywhere — Hibernate (the JPA
implementation Spring uses under the hood) generates and runs that SQL for you automatically
based on this class, because of the `spring.jpa.hibernate.ddl-auto=update` setting.

### `UrlMappingRepository.java` — talking to the database
This is just an **interface**, with no implementation code written by us at all. By extending
`JpaRepository<UrlMapping, Long>`, Spring Data JPA generates a working implementation at
startup, giving us `.save()`, `.findById()`, `.findAll()`, `.deleteById()`, etc. for free.

We also declared:
```java
Optional<UrlMapping> findByShortCode(String shortCode);
```
Spring reads this method name and automatically writes the SQL:
`SELECT * FROM url_mapping WHERE short_code = ?`. This "query derivation from method names"
is one of Spring Data JPA's most useful tricks — learn the naming patterns and you'll rarely
write manual SQL for simple lookups.

### `UrlShortenerService.java` — the actual logic
This is the heart of the app:
- **Generating a short code**: we build a random 6-character string from a 62-character
  alphabet (digits + lowercase + uppercase). With 62^6 (~56 billion) possible codes, collisions
  are extremely rare, but we still check the database and retry if one ever happens
  (see `generateUniqueCode()`).
- **Custom aliases**: if the user supplies their own alias (like `my-project`), we use that
  instead, after checking it isn't already taken.
- **Resolving + counting clicks**: `resolveAndRegisterClick()` looks up the mapping, checks it
  hasn't expired, increments `clickCount`, saves it, and returns the original URL.

`@Transactional` on these methods means each one runs as a single database transaction — if
anything fails partway through, all changes in that method are rolled back, keeping data
consistent.

### `UrlController.java` — the HTTP layer
Maps HTTP requests to service calls:
- `POST /api/shorten` — creates a short URL, returns `201 Created`
- `GET /{shortCode}` — the actual redirect. It responds with HTTP `302 Found` and a `Location`
  header, which is what makes the browser jump to the destination automatically.
- `GET /api/stats/{shortCode}` — returns click count and metadata without redirecting

### `GlobalExceptionHandler.java` — clean error responses
Without this, an invalid request or a missing short code would produce Spring's default,
ugly error page/stack trace. `@RestControllerAdvice` intercepts exceptions thrown anywhere in
the app and turns them into clean JSON like:
```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "No URL found for code: xxxx" }
```

### `application.properties` — configuration
Contains the database connection settings and JPA behavior flags. `spring.jpa.show-sql=true`
is very useful while learning — it prints every SQL statement Hibernate runs, so you can see
exactly what's happening under the hood.

---

## 5. Running the project

### Option A — from the command line
```bash
cd url-shortener
mvn spring-boot:run
```

### Option B — from an IDE
Open the folder as a Maven project, then run the `main()` method inside
`UrlShortenerApplication.java` (in IntelliJ, right-click the file → Run).

Either way, you should see log output ending with something like:
```
Tomcat started on port 8080
Started UrlShortenerApplication in X.XXX seconds
```

The app is now running at `http://localhost:8080`.

---

## 6. Testing it out

### Create a short URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com/search?q=spring+boot+tutorial"}'
```

Response:
```json
{
  "originalUrl": "https://www.google.com/search?q=spring+boot+tutorial",
  "shortCode": "aZ3xQ1",
  "shortUrl": "http://localhost:8080/aZ3xQ1",
  "createdAt": "2026-09-26T10:15:30",
  "expiresAt": null
}
```

### Use the short URL
Paste `http://localhost:8080/aZ3xQ1` into your browser — it should redirect you straight to
Google. Or check the redirect manually without following it:
```bash
curl -i http://localhost:8080/aZ3xQ1
```
Look for `HTTP/1.1 302` and a `Location:` header in the response.

### Check stats
```bash
curl http://localhost:8080/api/stats/aZ3xQ1
```

### Try a custom alias
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com", "customAlias": "my-github"}'
```
Now `http://localhost:8080/my-github` redirects to GitHub.

### Inspect the database directly
Visit `http://localhost:8080/h2-console` in your browser. Use:
- JDBC URL: `jdbc:h2:mem:urlshortener`
- Username: `sa`
- Password: *(leave blank)*

Then run `SELECT * FROM url_mapping;` to see your data live.

---

## 7. Understanding the request/response flow

```
Browser/Postman
      │  POST /api/shorten {"url": "..."}
      ▼
UrlController.shorten()
      │  calls
      ▼
UrlShortenerService.shortenUrl()
      │  generates code, builds UrlMapping, calls
      ▼
UrlMappingRepository.save(mapping)
      │  Spring Data JPA translates this into
      ▼
INSERT INTO url_mapping (...) VALUES (...)   [run against H2/MySQL]
```

And for the redirect:
```
Browser: GET /aZ3xQ1
      ▼
UrlController.redirect()
      ▼
UrlShortenerService.resolveAndRegisterClick()
      ▼
UrlMappingRepository.findByShortCode() → SELECT ... WHERE short_code = 'aZ3xQ1'
      ▼
UrlController returns 302 + Location header → browser follows it automatically
```

---

## 8. Common beginner questions

**Q: Why an interface (`UrlMappingRepository`) with no implementation?**
Spring generates a proxy implementation for you at startup using reflection. This is normal
and one of Spring Data JPA's core features — you'll see it in almost every Spring project.

**Q: What does `@Autowired`/constructor injection actually do?**
Spring manages object creation for you (this is called "Dependency Injection"). When
`UrlController` declares a constructor that takes a `UrlShortenerService`, Spring automatically
creates one instance of the service and passes it in — you never write `new UrlShortenerService()`
yourself.

**Q: Why DTOs (`ShortenRequest`/`ShortenResponse`) instead of using the entity directly?**
It decouples your API's JSON shape from your database schema. You can change your table
structure later without breaking clients, and you avoid accidentally exposing internal fields.

**Q: Why 302 instead of 301 for the redirect?**
301 (permanent redirect) tells browsers to cache the redirect forever, which would prevent you
from ever changing the destination or counting future clicks reliably. 302 (temporary) is the
standard choice for URL shorteners.

---

## 9. Switching from H2 to real MySQL

1. Install MySQL locally (or use a cloud instance) and create a database:
   ```sql
   CREATE DATABASE urlshortener;
   ```
2. In `application.properties`, comment out the whole H2 section and uncomment the MySQL
   section, filling in your own username/password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener?createDatabaseIfNotExist=true
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```
3. Restart the app. Hibernate will create the `url_mapping` table in MySQL automatically the
   same way it did in H2 (because of `ddl-auto=update`).

Nothing else in the code changes — this is the benefit of using JPA instead of raw
JDBC/SQL: the same Java code works against different databases.

---

## 10. Ideas to extend this for your internship project

Once the basics work, these are great additions to show initiative:
- **Expiry dates**: let users set `expiresAt` when creating a link (the field already exists
  on the entity — just wire it up in the request/service).
- **Rate limiting**: prevent one IP from creating too many links per minute.
- **A simple frontend**: an HTML form + JS `fetch()` call to `/api/shorten`, showing the result.
- **QR code generation**: generate a QR code image for each short URL (try the `zxing` library).
- **User accounts**: let users sign in and see/manage only their own links (Spring Security).
- **Analytics**: track referrers, timestamps of each click, and geolocation of visitors — you'd
  add a separate `ClickEvent` entity with a many-to-one relationship to `UrlMapping`.
- **Caching**: use Redis or an in-memory cache (Caffeine) to avoid hitting the DB on every
  redirect for hot links.

Any one of these is a solid "what I learned/added" talking point for your internship review.

---

## 11. Troubleshooting

- **Port 8080 already in use** → change `server.port` in `application.properties`.
- **`mvn` command not found** → install Maven, or open the project in IntelliJ (it bundles
  its own Maven).
- **Table not updating after changing an entity field** → since H2 is in-memory, just restart
  the app; for MySQL, you may need to drop the table or adjust it manually since `update`
  mode doesn't handle every kind of schema change (e.g. renaming a column).
