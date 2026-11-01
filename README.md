# Ahoi Burger API

REST API for a fictional burger restaurant (Spring Boot 4, Kotlin, MongoDB).
Clients can list burgers and drinks, search by name, filter by price, and fetch item details.

## Run

### Locally (needs MongoDB on localhost:27017)
    ./gradlew bootRun

### With Docker (API + MongoDB)
    docker compose up --build

Tested locally with Docker Desktop. The API is then available at http://localhost:8080 and Swagger UI at http://localhost:8080/swagger-ui.html. Demo data (from the mockups) is inserted on first start.

### Configuration (environment variables)
| Variable | Default | Purpose |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/ahoi_burger` | MongoDB connection |
| `API_KEY` | `dev-api-key` | Value clients must send in `X-API-Key` (**change in production**) |
| `SERVER_PORT` | `8080` | HTTP port |

## Documentation (Swagger / OpenAPI)
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
(Use the **Authorize** button and enter the API key.)

## Authentication
All `/api/**` endpoints require the header `X-API-Key: <key>`; otherwise the API answers `401`.
Swagger UI and static images are public.

## Endpoints
| Method | Path | Description |
|---|---|---|
| GET | `/api/restaurant` | Restaurant name and address |
| GET | `/api/burgers` | List burgers. Query params: `search`, `minPrice`, `maxPrice`, `available` |
| GET | `/api/burgers/{id}` | Burger details |
| GET | `/api/drinks` | List drinks. Same query params |
| GET | `/api/drinks/{id}` | Drink details |

Example:

    curl -H "X-API-Key: dev-api-key" "http://localhost:8080/api/burgers?search=big&minPrice=5&maxPrice=10"
    curl -H "X-API-Key: dev-api-key" http://localhost:8080/api/burgers/big-burger

### Errors
All errors share one JSON shape: `{ status, error, message, details, timestamp }`.
| Status | When |
|---|---|
| 400 | invalid parameter (e.g. `minPrice=abc`, `minPrice > maxPrice`, negative price, too long search) |
| 401 | missing or invalid API key |
| 404 | unknown id, or id belongs to the other category (a drink id under `/burgers`), unknown route |
| 405 / 415 | method or media type not supported |
| 500 | unexpected error (logged server-side, no internals exposed) |

## Tests
    ./gradlew test
- `MenuServiceTest`: business logic and query building (mocked repository/template).
- `MenuControllerTest`: HTTP layer, error format and API key check (standalone MockMvc, no database).

## Assumptions and design decisions
- **Read-only API**: the task asks to query items and get details, so no create/update/delete endpoints.
- **Unavailable items are returned** in lists (flagged `available=false`) because the mockup shows them greyed out. Use `?available=true` to hide them.
- **One collection** (`menu_items`) for burgers and drinks, told apart by `category`.
- **Prices** are stored as integer cents and returned as decimals (`9.99`) with a `currency` (`USD`, as in the mockup).
- **Drink details** are not in the mockups; drinks use the same detail shape and have `volumeMl` instead of `weightGrams`.
- **Allergens** are returned as the codes shown in the mockup (e.g. `rr`, `zz`, `mm`).
- **Search** is a case-insensitive "contains" on the name. User input is quoted, so it is never interpreted as a regex.
- **Price filter**: `minPrice`/`maxPrice` are inclusive and given in the item currency (e.g. `7.99`).
- **Images** are referenced by URL (`imageUrl`). Placeholder paths under `/images/` are used; files placed in `src/main/resources/static/images` are served automatically.
- **Seed data** is inserted only when the collection is empty.

## Not done / possible improvements
- Deployment to a public URL.
- Pagination and sorting for large menus.
- Per-client API keys, rate limiting, CORS configuration, and a read/write role split.
- Integration tests against a real MongoDB (Testcontainers).

## Use of AI
I used an AI assistant (Claude) while working on this challenge: for guidance on the Kotlin/Spring Boot/MongoDB setup, 
for drafting parts of the code and tests, and for help with the git commands. I reviewed the code, ran the test suite, 
tested the API manually (curl and Swagger) and in Docker, and fixed the issues that came up (e.g. the Spring Boot 4 
MongoDB property name). I can explain every part of the solution.
