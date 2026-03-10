file: src/NOTES.md
# Technical Documentation & Interview Guide

This document serves as a detailed technical breakdown of the Weather App. It is designed to help developers understand the internal workings of the application and prepare for technical interviews regarding this specific codebase.

---

## 1. Architecture Overview

The application follows a **Microservice-style Layered Architecture**. It functions as a **BFF (Backend for Frontend)** or a Proxy API. It sits between the client (Browser) and the third-party provider (WeatherAPI.com) to obscure API keys and transform data.

### Data Flow
1.  **Client (Browser)**: User requests data via the UI.
2.  **Controller Layer**: Receives the HTTP request.
3.  **Service Layer**:
    *   Constructs the request for the external API.
    *   Injects the API Key.
    *   Calls `api.weatherapi.com`.
4.  **Transformation**: The Service converts the complex external JSON into a simplified Internal DTO.
5.  **Response**: The simplified JSON is sent back to the browser.

---

## 2. Backend Deep Dive (Java & Spring Boot)

### Core Components

#### A. The Controller (`Controller.java`)
*   **Role**: The entry point for HTTP requests. It defines the API contract.
*   **Key Endpoint**: `GET /weather/forecast/{city}?days={n}`
*   **Responsibility**: It strictly handles web concerns (HTTP verbs, parameters). It **does not** contain business logic. It delegates work to the `WeatherService`.

#### B. The Service (`WeatherService.java`)
*   **Role**: The "Brain" of the application.
*   **Primary Tasks**:
    1.  **External Consumption**: Uses `RestTemplate` (or similar HTTP client) to fetch data from WeatherAPI.
    2.  **Deserialization**: Maps the raw JSON string from the external API into the `Root` Java object.
    3.  **Data Transformation (The Mapper Pattern)**:
        *   The external API returns deeply nested data (e.g., `Root -> Forecast -> ForecastDay -> Day`).
        *   The Service extracts only what is needed (Date, Min Temp, Max Temp, Icon) and maps it to `WeatherForCast`.
    4.  **Sanitization**: Ensures icon URLs are valid (e.g., prepending `https:`).

#### C. Data Transfer Objects (DTOs)
We use two sets of objects to maintain **Separation of Concerns**:
1.  **Domain Models** (`Root`, `Location`, `Current`): These exactly match the structure of the external WeatherAPI. If the external API changes, these classes change.
2.  **Response DTOs** (`WeatherForCast`, `WeatherResponse`): These represent exactly what our Frontend needs. They are optimized for the UI.

---

## 3. Frontend Deep Dive (Static Folder)

*   **Technology**: standard HTML5, CSS3, and Vanilla JavaScript (ES6).
*   **Location**: `src/main/resources/static/index.html`.
*   **Mechanism**:
    *   The page is a **Single Page Application (SPA)** behavior mimicking static file.
    *   Uses `window.onload` to fetch default data (Pune).
    *   Uses the **Fetch API** (`fetch('/weather/forecast/...')`) for asynchronous network calls.
    *   **DOM Manipulation**: JavaScript constructs HTML cards dynamically based on the array length of the returned data.

---

## 4. Application Configuration

*   **File**: `src/main/resources/application.properties`
*   **Purpose**: Externalizes configuration from code.
*   **Key Properties**:
    *   `weather.api.key`: The secret key for authentication.
    *   `weather.api.url`: Base URL for current weather.
    *   `weather.api.forcast.url`: Base URL for forecast data.

---

## 5. Annotation Dictionary (Interview Cheat Sheet)

These are the specific annotations used in this project. Using these definitions in an interview demonstrates strong framework knowledge.

| Annotation | Location | Conceptual Definition |
| :--- | :--- | :--- |
| **`@SpringBootApplication`** | Main Class | A convenience annotation that combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It bootstraps the Spring context. |
| **`@RestController`** | Controller | A specialized version of `@Component`. It treats the class as a Web Handler where every method automatically writes its return value to the HTTP response body (JSON). |
| **`@RequestMapping`** | Controller | Defines the base URI path (e.g., `/weather`) for all endpoints within the class. |
| **`@GetMapping`** | Controller | specific routing for HTTP GET requests. Used when we want to **retrieve** data without modifying the server state. |
| **`@Autowired`** | Controller | The core of **Dependency Injection**. It tells Spring: *"I need a `WeatherService` here. Please find the instance you created in memory and give it to me."* |
| **`@PathVariable`** | Controller Method | Extracts a value directly from the URI path. <br>Ex: `/weather/{London}` -> `String city = "London"` <br> *Usage: Identifying a specific resource.* |
| **`@RequestParam`** | Controller Method | Extracts a value from the query string. <br>Ex: `?days=3` -> `int days = 3` <br> *Usage: Filtering, sorting, or optional and configuration.* |
| **`@Service`** | Service Class | Stereotype annotation. Functionally identical to `@Component`, but semantically indicates the class holds **Business Logic**. |
| **`@JsonPropertyOrder`** | DTOs | Tells the Jackson library (JSON converter) to serialize fields in a specific sequence. Used here to ensure `weatherResponse` appears before `dayTemp` for readability. |

---

## 6. Interview Q&A Guide

**Q1: Why did you create a separate DTO (`WeatherForCast`) instead of just returning the API data directly?**
> "To prevent **Over-fetching** and **Tight Coupling**. The external API returns huge objects with astronomical data and hourly breakdowns that my UI doesn't need. If I passed that through, my API would be slow and wasteful. Also, by using my own DTO, if the external API changes its field names, I only have to update my Service mapper, not my Frontend."

**Q2: How does Dependency Injection work in your Controller?**
> "I use constructor (or field) injection via `@Autowired`. I don't instantiate `WeatherService` using the `new` keyword. Instead, I let the Spring IoC Container create the service bean at startup and inject it into the Controller. This makes my Controller easier to test because I can mock the Service."

**Q3: Explain the difference between PathVariable and RequestParam.**
> "I used `@PathVariable` for the 'City' because the city is the main resource I am identifying. I used `@RequestParam` for 'Days' because that is a modifier/filter on the request."

**Q4: How do you handle errors?**
> "If the external API fails or returns a 400, the `RestTemplate` in the service throws an exception. Ideally, this bubbles up to the Controller. In a production app, I would use a `@ControllerAdvice` to catch these globally and return a neat JSON error message to the user."
