# Web Protocol Fundamentals: HTTP Verbs, Headers, Status Codes & REST

> **Phase 10: Spring Boot, REST APIs & Data Persistence**  
> *Estimated Reading Time: 45 minutes | Total Practice Effort: ~3 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
Before writing your first `@RestController` in Spring Boot, you must understand the underlying protocol that powers the entire World Wide Web: **HTTP (Hypertext Transfer Protocol)**.

When a React frontend makes a request to a Spring Boot backend:
- Which HTTP method tells the backend to update a record vs create a new one?
- Why is returning HTTP `200 OK` with an error message `{ "error": "User not found" }` considered a severe architectural anti-pattern?
- What does it mean for an API operation to be **idempotent**, and why is this critical when handling network timeouts?

In this lesson, you will master **HTTP Request/Response Anatomy**, **Status Code semantics**, and Roy Fielding's **REST Architectural Principles**.

### Prerequisites
- [Topic 1.1: Setup & Anatomy of a Java Program](../01-foundations/01-setup-anatomy.md)
- [Topic 7.2: SQL CRUD Operations](../07-sql/02-crud-operations.md)

---

## 2. Plain-English Mental Model

```
[ An HTTP Request/Response: Sending a Postal Package ]
- Envelope Exterior:
  - Address: https://api.store.com/v1/orders (URI)
  - Method / Stamp: POST (Action to perform)
  - Headers: Content-Type: application/json, Authorization: Bearer eyJ... (Metadata)
- Package Contents:
  - Body: { "productId": 104, "quantity": 2 } (Payload data)
- Return Receipt from Post Office:
  - Status Code: 201 Created (Instant standardized status)
  - Response Body: { "id": 5001, "status": "CONFIRMED" }
```

---

## 3. Anatomy of an HTTP Request & Response

### The Raw HTTP Request:
```http
POST /api/v1/products HTTP/1.1
Host: api.example.com
User-Agent: Mozilla/5.0
Accept: application/json
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
Content-Length: 54

{
  "sku": "KB-WL-01",
  "title": "Wireless Keyboard",
  "price": 89.99
}
```

### The Raw HTTP Response:
```http
HTTP/1.1 201 Created
Date: Mon, 20 Oct 2026 14:00:00 GMT
Content-Type: application/json; charset=UTF-8
Location: /api/v1/products/42
Connection: keep-alive

{
  "id": 42,
  "sku": "KB-WL-01",
  "title": "Wireless Keyboard",
  "price": 89.99,
  "createdAt": "2026-10-20T14:00:00Z"
}
```

---

## 4. HTTP Verbs & The Concept of Idempotency

```mermaid
flowchart TD
    Verbs["Standard HTTP Verbs"]
    Verbs --> G["GET: Safe & Idempotent (Read)"]
    Verbs --> P["POST: Not Safe & Not Idempotent (Create)"]
    Verbs --> PU["PUT: Idempotent (Full Replace)"]
    Verbs --> PA["PATCH: Partial Update"]
    Verbs --> D["DELETE: Idempotent (Remove)"]
```

### What is Idempotency?
An HTTP method is **idempotent** if executing it **multiple times with identical parameters produces the exact same server state as executing it once**.

| HTTP Verb | CRUD Action | Safe? | Idempotent? | Description |
| :--- | :--- | :---: | :---: | :--- |
| **`GET`** | Read | ✅ Yes | ✅ Yes | Retrieves a resource without modifying server state. |
| **`POST`** | Create | ❌ No | ❌ No | Submits data to create a new resource or execute a non-idempotent process (e.g. charging a card). Calling 5 times creates 5 resources! |
| **`PUT`** | Replace | ❌ No | ✅ Yes | Completely replaces the target resource. Calling 5 times leaves the exact same replacement state. |
| **`PATCH`** | Partial Update | ❌ No | ⚠️ Contextual | Updates only specified fields. |
| **`DELETE`** | Delete | ❌ No | ✅ Yes | Deletes a resource. Calling 5 times leaves the resource deleted. |

> **Why Idempotency Matters in Distributed Systems:**  
> If an API client makes a request to `DELETE /orders/5` and the network times out before receiving the response, the client can safely **retry** the request automatically. If the method is `POST /payments`, a blind retry could charge the customer twice!

---

## 5. HTTP Status Codes Decoded

HTTP status codes are grouped into 5 categories by their hundreds digit:

### 1. `2xx` Success
- **`200 OK`**: Standard success response for `GET`, `PUT`, `PATCH`.
- **`201 Created`**: The request succeeded and a new resource was created (standard for `POST`). Usually includes a `Location` header.
- **`204 No Content`**: Succeeded, but there is no body payload to return (standard for `DELETE`).

### 2. `4xx` Client Errors (The caller made a mistake)
- **`400 Bad Request`**: Malformed syntax, invalid JSON, or missing required parameter.
- **`401 Unauthorized`**: Authentication is required and missing/invalid (**"Who are you?"**).
- **`403 Forbidden`**: Authenticated, but user lacks necessary permissions (**"I know who you are, but you cannot touch this"**).
- **`404 Not Found`**: The requested resource URI does not exist.
- **`409 Conflict`**: Request conflicts with current database state (e.g. email already registered).
- **`422 Unprocessable Entity`**: JSON syntax is valid, but fails business validation (e.g. age is `-5`).

### 3. `5xx` Server Errors (The server crashed or bugged)
- **`500 Internal Server Error`**: Unhandled exception on backend.
- **`502 Bad Gateway`**: Reverse proxy (Nginx) received an invalid response from upstream backend.
- **`503 Service Unavailable`**: Server overloaded or undergoing maintenance.
- **`504 Gateway Timeout`**: Upstream server took too long to respond.

---

## 6. REST Architectural Constraints (Roy Fielding)

**REST (Representational State Transfer)** is an architectural style designed to keep web systems decoupled, scalable, and cacheable.

```mermaid
flowchart LR
    R1["1. Client-Server<br/>Separation of UI from Data"]
    R2["2. Statelessness<br/>Every request contains all info"]
    R3["3. Cacheable<br/>Responses declare cache rules"]
    R4["4. Uniform Interface<br/>Standard URIs & Nouns"]
```

### The Uniform Interface: URI Naming Conventions
A RESTful URI identifies a **Resource (Noun)**, never an **Action (Verb)**:

| Bad URI (RPC Style / Anti-Pattern) | Clean RESTful URI | HTTP Method |
| :--- | :--- | :---: |
| `/api/getUsers` | `/api/v1/users` | `GET` |
| `/api/createUser` | `/api/v1/users` | `POST` |
| `/api/getUserById?id=42` | `/api/v1/users/42` | `GET` |
| `/api/updateUser` | `/api/v1/users/42` | `PUT` / `PATCH` |
| `/api/deleteUser?id=42` | `/api/v1/users/42` | `DELETE` |
| `/api/getUserOrders?userId=42` | `/api/v1/users/42/orders` | `GET` |

---

## 7. Common Rookie Mistakes & Anti-Patterns

### Anti-Pattern 1: The "Always 200 OK" Trap
```json
// HORRIBLE PRACTICE:
HTTP/1.1 200 OK
{
  "success": false,
  "errorMessage": "User unauthorized!"
}
```
*Why this breaks systems:* Frontend HTTP client interceptors (like Axios), API gateways, and monitoring tools evaluate HTTP status codes. If you return 200, automated tools assume the request succeeded! Always use the proper semantic code (`401 Unauthorized`).

### Anti-Pattern 2: Mutating State with `GET` Requests
```http
<!-- DANGEROUS! Web crawlers & pre-fetchers will accidentally delete users! -->
GET /users/delete?id=42
```
*Rule:* `GET` requests must **never** modify database records!

---

## 8. Guided Hands-On Exercise

### The Scenario
You are designing the RESTful API contract for an enterprise Blogging Platform.  
Resources: `articles` and `comments`.

### Your Challenge:
Specify the exact **HTTP Verb**, **URI Path**, and **Expected Success Status Code** for each of these 5 operations:
1. Retrieve all published articles.
2. Publish a brand-new article.
3. Update the title of article #88.
4. Delete comment #10 under article #88.
5. Retrieve all comments belonging to article #88.

<details>
<summary>💡 Hint: Use nested sub-resource URIs for comments</summary>

`/articles/{articleId}/comments/{commentId}`
</details>

<details>
<summary>✅ Solution</summary>

| Operation | HTTP Verb | RESTful URI Path | Success Status Code |
| :--- | :---: | :--- | :---: |
| 1. Retrieve all articles | `GET` | `/api/v1/articles` | `200 OK` |
| 2. Publish new article | `POST` | `/api/v1/articles` | `201 Created` |
| 3. Update article title | `PATCH` | `/api/v1/articles/88` | `200 OK` |
| 4. Delete comment #10 | `DELETE` | `/api/v1/articles/88/comments/10` | `204 No Content` |
| 5. Retrieve comments for article | `GET` | `/api/v1/articles/88/comments` | `200 OK` |
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **What is the key difference between HTTP `PUT` and HTTP `PATCH`?**
   - [x] A) `PUT` is intended to replace the entire target resource entity, whereas `PATCH` is intended for partial updates to specific fields.
   - [ ] B) `PUT` is for files and `PATCH` is for JSON.
   - [ ] C) `PATCH` is safe and read-only.
   - [ ] D) There is no difference; they are exact synonyms.

2. **What HTTP status code should be returned when a client attempts to access a protected admin endpoint without providing an Authorization token?**
   - [x] A) `401 Unauthorized`.
   - [ ] B) `403 Forbidden`.
   - [ ] C) `404 Not Found`.
   - [ ] D) `500 Internal Server Error`.

3. **Why should RESTful URIs use plural nouns (e.g. `/orders`) rather than verbs (e.g. `/createOrder`)?**
   - [x] A) Because the HTTP Verb (`GET`, `POST`, `DELETE`) already provides the action; the URI should strictly identify the resource being manipulated.
   - [ ] B) Browsers do not support verbs in URLs.
   - [ ] C) SQL does not support verbs.
   - [ ] D) It is required by the Apache Tomcat server.

---

### Junior Developer Interview Q&A

#### Q: "What is the difference between HTTP Status Code 401 Unauthorized and 403 Forbidden?"
> **Model Answer:**  
> "The fundamental difference comes down to **Authentication vs Authorization**:
>
> 1. **`401 Unauthorized`** means **Authentication Failed or Missing**. The server does not know who the client is. The client either sent no credentials or invalid/expired credentials (e.g. missing or expired JWT Bearer token). The client can authenticate with valid credentials to retry.
>
> 2. **`403 Forbidden`** means **Authorization Failed**. The server successfully verified the client's identity (e.g. 'You are user John Doe'), but John Doe does not have the necessary roles or permissions to perform the requested action (e.g. a regular user trying to access `/api/admin/system-reset`). Re-authenticating with the same credentials will not change the outcome."

---

## 10. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Deconstruct raw HTTP request and response structures.
- [ ] Define idempotency and identify which HTTP methods are idempotent.
- [ ] Choose semantic status codes (`200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`, `422`, `500`).
- [ ] Design clean RESTful URIs using plural nouns and nested resource hierarchies.

👉 **Next Lesson:** **[Topic 10.2: Spring Framework Core: Inversion of Control & Dependency Injection](02-ioc-di.md)**
