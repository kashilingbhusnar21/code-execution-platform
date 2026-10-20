# Stateless API Security: JWT Authentication, Claims & Bearer Auth

> **Phase 11: Production Engineering, Security & Docker**  
> *Estimated Reading Time: 55 minutes | Total Practice Effort: ~4 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In traditional server-rendered web applications, user authentication was **stateful**:
- The server stored a session in RAM (`HttpSession`) and sent a `JSESSIONID` cookie to the browser.
- When your system scales to **10 load-balanced backend instances**, Session #1 on Server A does not exist on Server B!
- Either you configure complex shared session clusters (like Redis Session replication), or a user is forced to log in again every time the load balancer routes their request to a different server instance.

Modern full-stack architectures (React + Spring Boot) use **Stateless Authentication** with **JSON Web Tokens (JWT)**.

With JWT:
- The server stores **zero session state in memory**.
- The token itself contains the user's identity, roles, and expiration date, cryptographically signed with a private secret.
- **Any backend server instance can instantly verify the user in microseconds without querying the database!**

### Prerequisites
- [Topic 10.1: Web Protocol Fundamentals & Headers](../10-spring/01-http-rest.md)
- [Topic 11.1: Spring Security Architecture & Filter Chain](01-security-filter-chain.md)
- [Topic 11.2: Password Hashing with BCrypt](02-password-hashing.md)

---

## 2. Plain-English Mental Model

```
[ Stateful Session: A Coat Check Token ]
- You hand your jacket to the coat check attendant.
- They store your jacket in a back closet and give you a plastic claim ticket #42.
- If you walk across the street to a different branch of the club, claim ticket #42 is useless!

[ Stateless JWT: A Tamper-Proof Holographic Concert Wristband ]
- You buy a VIP concert ticket.
- The entrance officer gives you a secure wristband stamped with:
  "Holder: Alice | Access: VIP Lounge | Valid Until: Midnight | Cryptographic Seal: Valid"
- You can walk into any gate, bar, or stage across the entire stadium.
- Every guard simply inspects the holographic cryptographic seal on your wristband.
- The guards don't need a central database to know you are VIP!
```

---

## 3. Anatomizing a JSON Web Token

A JWT is a string of characters separated by two dots (`.`):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbGljZUBjb3JwLmNvbSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjE3Mjk0MzAwMDB9.s7zD_0G6hX1s8wK_...
------------------------------------.-------------------------------------------------------------------------------------.---------------------
            1. HEADER                                                     2. PAYLOAD                                                3. SIGNATURE
```

```mermaid
flowchart TD
    Token["JSON Web Token (JWT)"]
    Token --> H["1. Header (Base64Url encoded)<br/>Algorithm: HMAC-SHA256, Type: JWT"]
    Token --> P["2. Payload (Base64Url encoded)<br/>Claims: sub, roles, exp, iat"]
    Token --> S["3. Signature (Cryptographic hash)<br/>HMACSHA256(Base64(Header) + '.' + Base64(Payload), SecretKey)"]
```

### 1. Header
Identifies the token type and cryptographic hashing algorithm:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### 2. Payload (The Claims)
Contains statements about the user and metadata. Standard claims include:
- `sub` (Subject): The user's unique identity (e.g. `email` or `userId`).
- `iat` (Issued At): Unix timestamp when the token was created.
- `exp` (Expiration): Unix timestamp when the token becomes invalid.
- Custom claims: `roles: ["ROLE_ADMIN"]`, `tier: "PREMIUM"`.

```json
{
  "sub": "sarah@sky.net",
  "userId": 42,
  "roles": ["ROLE_ADMIN"],
  "iat": 1729430000,
  "exp": 1729433600
}
```

> **CRITICAL WARNING: Base64Url is NOT Encryption!**  
> Anyone can paste your JWT into [jwt.io](https://jwt.io) and read the payload in plain text! **Never store passwords, credit card numbers, or social security numbers inside a JWT payload.**

### 3. Cryptographic Signature
The signature is calculated by taking the encoded Header, the encoded Payload, and signing them with a **Secret Key** known only to the backend server:

$$\text{Signature} = \text{HMAC-SHA256}(\text{Base64Url}(\text{Header}) + \text{"."} + \text{Base64Url}(\text{Payload}), \text{ServerSecretKey})$$

If an attacker modifies the payload (e.g. changing `"roles": ["ROLE_USER"]` to `"roles": ["ROLE_ADMIN"]`), the signature becomes invalid, and the backend rejects the token!

---

## 4. The Complete End-to-End JWT Authentication Flow

```mermaid
sequenceDiagram
    participant React as React Frontend
    participant Filter as JwtAuthenticationFilter
    participant Controller as AuthController
    participant Service as JwtService

    Note over React,Controller: 1. Login Phase
    React->>Controller: POST /api/v1/auth/login { email, password }
    Controller->>Service: Authenticate user & generate JWT
    Service-->>Controller: Return accessToken
    Controller-->>React: 200 OK { accessToken: "eyJhbG..." }

    Note over React,Filter: 2. Subsequent Authenticated Requests
    React->>Filter: GET /api/v1/orders (Header: Authorization: Bearer eyJhbG...)
    Filter->>Service: Validate signature & check expiration
    Service-->>Filter: Valid! (Extract user & roles)
    Filter->>Filter: Populate SecurityContextHolder with user
    Filter->>Controller: Forward request to OrderController
    Controller-->>React: 200 OK [ User's Orders JSON ]
```

---

## 5. Implementing the JWT Service (using `jjwt`)

### Maven Dependency (`pom.xml`):
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

### The `JwtService` Implementation:
```java
package com.career.learning.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Must be at least 256 bits (32 characters) for HS256!
    @Value("${app.jwt.secret:dGhpcy1pcy1hLXZlcnktc2VjdXJlLTI1Ni1iaXQtbG9uZy1rZXktZm9yLWhzMjU2LXNpZ25pbmc=}")
    private String secretKeyString;

    @Value("${app.jwt.expiration-ms:3600000}") // 1 Hour
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Generate Token with Custom Claims
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        extraClaims.put("roles", userDetails.getAuthorities().stream().map(Object::toString).toList());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // Usually email
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // 2. Extract Username (Subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. Extract Specific Claims safely
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Cryptographically verifies signature!
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 4. Validate Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
```

---

## 6. The `JwtAuthenticationFilter` (`OncePerRequestFilter`)

This filter intercepts every incoming request, inspects the `Authorization: Bearer <token>` header, verifies the signature, and loads the user into Spring Security's `SecurityContextHolder`:

```java
package com.career.learning.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Skip to next filter
            return;
        }

        final String jwt = authHeader.substring(7); // Extract token after "Bearer "
        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            // Token signature invalid or expired -> Proceed without authentication
            filterChain.doFilter(request, response);
            return;
        }

        // 2. If user is found and not already authenticated in this ThreadLocal
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 3. Verify token validity
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Populate SecurityContextHolder: The user is now officially authenticated!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continue filter chain to reach controller
        filterChain.doFilter(request, response);
    }
}
```

---

## 7. Short-Lived Access Tokens vs Long-Lived Refresh Tokens

Because JWTs are stateless, **you cannot revoke or delete a JWT from the server before its expiration date**!

If an attacker steals a token that is valid for 30 days, they have access to the account for 30 days.

### The Production Industry Standard:
1. **Access Token (Short-Lived: 15 minutes):** Sent on every API request in the `Authorization: Bearer` header. Even if compromised, it expires in minutes.
2. **Refresh Token (Long-Lived: 7 days):** Stored in a database table (`refresh_tokens`) or secure `HttpOnly` cookie. Used exclusively at `POST /api/v1/auth/refresh` to issue a fresh 15-minute access token.
3. **Revocation:** If a user clicks "Logout" or changes their password, the server simply deletes the **Refresh Token** from the database. The user will be locked out within 15 minutes when their current access token expires.

---

## 8. Common Rookie Mistakes & Anti-Patterns

### Anti-Pattern 1: The Weak Secret Key Crash
Using a short secret key: `secret: "mysecret"`.  
The HMAC-SHA256 algorithm strictly requires at least **256 bits (32 bytes)**. `jjwt` will throw:  
`WeakKeyException: The specified key size is 64 bits. A key of at least 256 bits is required.`

### Anti-Pattern 2: Missing the Space in `Bearer `
```java
// BUG: authHeader.startsWith("Bearer") without space!
// Checks for "Bearer<token>" instead of "Bearer <token>"!
```
The HTTP standard specifies a single space after the `Bearer` scheme.

---

## 9. Guided Hands-On Exercise

### The Scenario
You need to test your JWT generation and validation service in an isolated JUnit 5 test.

### Your Challenge:
Write a test that:
1. Generates a token for username `"john@corp.com"`.
2. Asserts the extracted username equals `"john@corp.com"`.
3. Asserts that `isTokenValid()` returns `true`.

<details>
<summary>💡 Hint: Mock UserDetails</summary>

```java
UserDetails user = User.builder().username("john@corp.com").password("pass").roles("USER").build();
String token = jwtService.generateToken(user, 1L);
assertEquals("john@corp.com", jwtService.extractUsername(token));
```
</details>

<details>
<summary>✅ Solution</summary>

```java
package com.career.learning.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inject 256-bit secret key and 1-hour expiration
        ReflectionTestUtils.setField(jwtService, "secretKeyString", "super-secret-key-that-is-at-least-32-bytes-long-2026");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
    }

    @Test
    @DisplayName("Should generate valid token and extract matching username")
    void shouldGenerateAndValidateJwt() {
        UserDetails user = User.builder()
                .username("sarah@sky.net")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user, 42L);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length, "JWT must have exactly 3 parts separated by dots");

        String extractedUser = jwtService.extractUsername(token);
        assertEquals("sarah@sky.net", extractedUser);

        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid, "Token should be valid for the issued user");
    }
}
```
</details>

---

## 10. Knowledge Check & Interview Defense

### Quick Quiz
1. **What are the three components of a JSON Web Token (JWT)?**
   - [x] A) Header, Payload, and Signature (separated by periods).
   - [ ] B) Username, Password, and Role.
   - [ ] C) IP Address, Timestamp, and MAC Address.
   - [ ] D) Database ID, Session ID, and Cookie.

2. **Is the payload of a standard JWT encrypted?**
   - [x] A) No; it is Base64Url encoded, meaning anyone who inspects the token can decode and read the payload. The signature guarantees integrity, not confidentiality.
   - [ ] B) Yes, it is encrypted with AES-256.
   - [ ] C) Yes, it is encrypted with RSA.
   - [ ] D) Only if the payload contains numbers.

3. **In an HTTP request, how is a JWT standardly transmitted to the backend?**
   - [x] A) In the `Authorization` header with the format `Bearer <token>`.
   - [ ] B) In the URL query parameter `?jwt=<token>`.
   - [ ] C) Inside the SQL database directly.
   - [ ] D) As an XML tag.

---

### Junior Developer Interview Q&A

#### Q: "Why is a short-lived Access Token combined with a long-lived Refresh Token the industry standard for REST API security?"
> **Model Answer:**  
> "Because JWTs are stateless, they cannot be easily invalidated or revoked on the server side prior to their expiration date without introducing stateful blacklists.
>
> If we issued a single token valid for 30 days and the user's laptop was stolen or their token was intercepted, an attacker would have uninterrupted access to the account for a full month.
>
> By using **short-lived Access Tokens (e.g. 15 minutes)**:
> 1. The window of vulnerability if a token is compromised is reduced to at most 15 minutes.
> 2. The client silently requests a new Access Token using the **Refresh Token** in the background.
> 3. The Refresh Token is stored securely (often in a database table with revocation flags). If a user reports their device stolen, clicks 'Sign Out of All Devices', or changes their password, we simply **revoke the Refresh Token in our database**. When the current 15-minute Access Token expires, the attacker cannot obtain a new one and is permanently locked out."

---

## 11. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Diagram the three components of a JWT (Header, Payload, Signature).
- [ ] Explain why Base64Url is not encryption and avoid storing sensitive PII in claims.
- [ ] Implement token generation, claim extraction, and validation using `jjwt`.
- [ ] Implement `OncePerRequestFilter` to populate `SecurityContextHolder`.
- [ ] Explain the dual-token Access Token / Refresh Token security architecture.

👉 **Next Lesson:** **[Topic 11.4: Docker Basics: Writing Dockerfiles & Multi-Stage JRE Builds](04-docker-basics.md)**
