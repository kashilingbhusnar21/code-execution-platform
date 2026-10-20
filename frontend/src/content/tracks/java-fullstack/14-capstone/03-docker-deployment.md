# Full-Stack Orchestration: Dockerizing React (Nginx) & Spring Boot

> **Phase 14: Full-Stack Capstone Project & Deployment**  
> *Estimated Reading Time: 60 minutes | Total Practice Effort: ~4.5 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In local development, you run:
- React on a local Vite development server (`http://localhost:5173`).
- Spring Boot on your local machine (`http://localhost:8080`).
- MySQL running locally or via standalone Docker.

**You cannot deploy a development server (`npm run dev`) to production!**  
Vite's development server is not optimized, does not compress assets, is memory-heavy, and lacks production caching.

In production:
1. **React** must be compiled into optimized static HTML, CSS, and JS bundles, and served by a high-performance **Nginx** web server.
2. **Nginx** acts as a **Reverse Proxy**: it serves React static files directly, while seamlessly forwarding any `/api/*` requests internally to the Spring Boot backend container.
3. Because both React and Spring Boot are served behind Nginx on the same domain and port, **CORS is completely eliminated in production!**

In this lesson, you will master **Multi-Stage React Dockerfiles**, **Nginx Reverse Proxy configuration (`try_files`)**, and orchestrate the **Complete Full-Stack Docker Compose Stack**.

### Prerequisites
- [Topic 11.4: Docker Basics & Multi-Stage Builds](../11-production/04-docker-basics.md)
- [Topic 11.5: Docker Compose Multi-Service Orchestration](../11-production/05-docker-compose.md)
- [Topic 13.5: Full-Stack Axios Integration](../13-react/05-axios-spring-integration.md)

---

## 2. Plain-English Mental Model

```
[ Local Development: Two Separate Food Trucks ]
- Truck 1 (Vite on port 5173): Sells appetizers.
- Truck 2 (Spring Boot on port 8080): Sells main courses.
- The customer has to walk back and forth between two different trucks across the street (CORS!).

[ Production Deployment: A Luxury Restaurant with a Maitre D' (Nginx) ]
- The customer walks into ONE entrance on Port 80 (Nginx).
- If the customer asks for a menu or decor (HTML/CSS/JS) -> The Maitre D' hands it to them instantly from the front desk (Nginx static files).
- If the customer asks for food (/api/*) -> The Maitre D' whispers to the kitchen in the back (Spring Boot container).
- The customer only ever talks to ONE door! Zero CORS issues!
```

---

## 3. Step 1: Multi-Stage Dockerfile for React 18

We use a **Multi-Stage Build**:
- **Stage 1 (Build):** Uses `node:20-alpine` to install dependencies and run `npm run build` (creating the optimized static `/dist` directory).
- **Stage 2 (Runtime):** Uses lightweight `nginx:alpine` (under 25MB!). It copies **only the static `/dist` files** into Nginx's web root and serves them at blazingly fast speeds!

```dockerfile
# ==========================================
# STAGE 1: Compile React Application
# ==========================================
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package descriptors first to leverage Docker layer caching
COPY package.json package-lock.json ./
RUN npm ci

# Copy frontend source code
COPY . .

# Compile optimized static bundle (Vite outputs to /app/dist)
RUN npm run build

# ==========================================
# STAGE 2: Production Nginx Web Server
# ==========================================
FROM nginx:alpine

# Remove default Nginx welcome page
RUN rm -rf /usr/share/nginx/html/*

# Copy compiled static files from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration (Essential for React Router & Reverse Proxy!)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose standard HTTP port
EXPOSE 80

# Launch Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
```

**Result:** The final production frontend container image is **only 24 Megabytes**!

---

## 4. Step 2: The Critical `nginx.conf` (`try_files` & Reverse Proxy)

In a React SPA, the user might bookmark or refresh `/courses/42`.  
If Nginx searches its physical file system for a file named `/courses/42`, **it finds nothing and returns a 404 Not Found error!**

### The Solution: `try_files $uri $uri/ /index.html`
We configure Nginx to fall back to `/index.html` for any route that doesn't match a static file, allowing **React Router to handle the URL client-side**:

```nginx
server {
    listen 80;
    server_name localhost;

    # 1. Root directory for compiled React static assets
    root /usr/share/nginx/html;
    index index.html index.htm;

    # 2. Gzip Compression for fast delivery
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml image/svg+xml;

    # 3. Static Asset Caching (Vite adds content hashes to filenames!)
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, no-transform, immutable";
    }

    # 4. CRITICAL: React Router Single Page Application Fallback!
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 5. Reverse Proxy: Forward all API requests directly to Spring Boot backend!
    location /api/ {
        proxy_pass http://backend-api:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts for heavy backend requests
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

---

## 5. Step 3: Complete Unified Full-Stack `docker-compose.yml`

This single file orchestrates the entire production ecosystem:

```yaml
version: '3.8'

services:
  # ==========================================
  # 1. FRONTEND: React 18 SPA + Nginx Proxy
  # ==========================================
  frontend-ui:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: capstone-frontend
    restart: always
    ports:
      - "80:80" # Public entry point to the entire platform!
    depends_on:
      - backend-api
    networks:
      - fullstack-network

  # ==========================================
  # 2. BACKEND: Spring Boot 3 REST Microservice
  # ==========================================
  backend-api:
    build:
      context: ./code-api
      dockerfile: Dockerfile
    container_name: capstone-backend
    restart: always
    depends_on:
      mysql-db:
        condition: service_healthy
      redis-cache:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/learning_platform?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: appuser
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-secretpassword}
      SPRING_DATA_REDIS_HOST: redis-cache
      SPRING_DATA_REDIS_PORT: 6379
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: 8080
    networks:
      - fullstack-network

  # ==========================================
  # 3. DATABASE: MySQL 8.3 Relational OLTP
  # ==========================================
  mysql-db:
    image: mysql:8.3
    container_name: capstone-mysql
    restart: always
    environment:
      MYSQL_DATABASE: learning_platform
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-rootpass}
      MYSQL_USER: appuser
      MYSQL_PASSWORD: ${DB_PASSWORD:-secretpassword}
    volumes:
      - mysql_production_data:/var/lib/mysql
    networks:
      - fullstack-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "appuser", "-psecretpassword"]
      interval: 5s
      timeout: 5s
      retries: 10

  # ==========================================
  # 4. CACHE: Redis 7 In-Memory Store
  # ==========================================
  redis-cache:
    image: redis:7-alpine
    container_name: capstone-redis
    restart: always
    networks:
      - fullstack-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

  # ==========================================
  # 5. BROKER: RabbitMQ 3.12 Message Broker
  # ==========================================
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: capstone-rabbitmq
    restart: always
    ports:
      - "15672:15672" # RabbitMQ Management Web Dashboard
    networks:
      - fullstack-network
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

# Persistent Data Storage
volumes:
  mysql_production_data:
    driver: local

# Unified Internal Bridge Network
networks:
  fullstack-network:
    driver: bridge
```

---

## 6. Launching and Verifying the Full Stack

### 1. Build and Start All Containers:
```bash
docker compose up -d --build
```

### 2. Verify Container Health:
```bash
docker compose ps
```
All containers should report **`Up (healthy)`**!

### 3. Verification in Browser:
- Open `http://localhost`: The React frontend loads instantly via Nginx!
- Click `/courses`: React Router renders without page reload.
- Refresh page on `http://localhost/courses/1`: Nginx's `try_files` cleanly serves `/index.html`, and React loads the course details.
- Open `http://localhost:15672`: The RabbitMQ management console displays active queues!

---

## 7. Common Rookie Mistakes & Anti-Patterns

### Anti-Pattern 1: The Refresh 404 Bug
Deploying React to Nginx without `try_files $uri $uri/ /index.html;`.  
Navigating via links works, but the moment a user presses `F5` (Refresh) on any sub-page, Nginx returns a raw `404 Not Found`!

### Anti-Pattern 2: Pointing Axios to `http://localhost:8080` in Production
In production, React code executes inside the **user's client browser** on their phone or laptop. If your code calls `http://localhost:8080`, the phone tries to connect to port 8080 on *the phone itself*, failing immediately!  
*Fix:* With Nginx reverse proxying, set Axios `baseURL: "/api/v1"`. It automatically routes to the host that served the frontend!

---

## 8. Guided Hands-On Exercise

### The Scenario
You need to verify that your Nginx reverse proxy correctly forwards requests to the Spring Boot backend without leaking port 8080 to the public internet.

### Your Challenge:
Write a `curl` terminal command that queries the backend course catalog through Nginx on port 80.

<details>
<summary>💡 Hint: Query http://localhost/api/v1/courses</summary>

```bash
curl -I http://localhost/api/v1/courses
```
</details>

<details>
<summary>✅ Solution</summary>

```bash
# Query the API through Nginx port 80
curl -i http://localhost/api/v1/courses

# Expected Response Headers from Nginx:
# HTTP/1.1 200 OK
# Server: nginx/1.25.4
# Content-Type: application/json
# Connection: keep-alive
```
Notice that the client communicated purely over port 80, and Nginx transparently proxy-passed the request to `backend-api:8080` on the internal bridge network!
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **Why is `try_files $uri $uri/ /index.html;` required in an Nginx configuration serving a React SPA?**
   - [x] A) It instructs Nginx to fall back to `/index.html` when a requested URL path does not match a physical file on disk, allowing client-side React Router to intercept the URL and render the correct view.
   - [ ] B) It automatically encrypts passwords.
   - [ ] C) It connects Nginx to MySQL.
   - [ ] D) It compiles TypeScript code.

2. **How does an Nginx Reverse Proxy eliminate Cross-Origin Resource Sharing (CORS) errors in production?**
   - [x] A) Both the React static assets and the `/api/*` endpoints are served through the same single domain and port (Port 80/443), satisfying the browser's Same-Origin Policy.
   - [ ] B) Nginx disables all browser security.
   - [ ] C) Nginx converts HTTP into UDP.
   - [ ] D) By deleting cookies.

3. **In the production React Dockerfile, why do we compile the bundle in a Node stage and then copy the `/dist` directory to an Nginx stage?**
   - [x] A) Multi-stage builds discard the heavy Node.js runtime and build tools, resulting in a tiny, secure Nginx production image (< 25MB) that serves pre-built static files with maximum efficiency.
   - [ ] B) Node.js cannot serve HTML.
   - [ ] C) Nginx cannot run JavaScript.
   - [ ] D) It is required by Vite.

---

### Junior Developer Interview Q&A

#### Q: "How do you deploy a modern full-stack application connecting React and Spring Boot in a production containerized environment?"
> **Model Answer:**  
> "In production, we containerize and orchestrate the full stack using a decoupled multi-container Docker Compose architecture:
>
> 1. **Frontend Container:** We compile the React 18 SPA using a multi-stage Dockerfile. Stage 1 uses Node.js to build the static `/dist` bundle. Stage 2 copies only the compiled assets into a minimal `nginx:alpine` image.
> 2. **Reverse Proxying & Routing:** Nginx is configured with `try_files $uri $uri/ /index.html` to support client-side React Router deep-linking. It acts as a Reverse Proxy, forwarding all `/api/*` calls internally to the Spring Boot backend container. This eliminates CORS issues entirely because the browser communicates with a single origin on Port 80.
> 3. **Backend Container:** The Spring Boot API is packaged using a multi-stage Dockerfile that builds the JAR with Maven in Stage 1 and runs it using an `eclipse-temurin:21-jre-alpine` runtime under a non-root user with container-aware memory flags (`-XX:MaxRAMPercentage=75.0`).
> 4. **Persistence & Messaging:** MySQL 8 is mounted with persistent named volumes to guarantee data survivability across container lifecycles, paired with a Redis cache and RabbitMQ message broker.
> 5. **Orchestration:** Docker Compose coordinates all services on an internal bridge network, using Docker Healthchecks to prevent race conditions during startup."

---

## 10. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Build a multi-stage React Dockerfile resulting in a sub-25MB Nginx image.
- [ ] Configure `nginx.conf` with `try_files` for React Router and reverse proxy `/api/` traffic.
- [ ] Eliminate production CORS issues using an Nginx reverse proxy.
- [ ] Orchestrate Frontend, Backend, MySQL, Redis, and RabbitMQ using Docker Compose.
- [ ] Deploy and verify the full-stack ecosystem with `docker compose up -d --build`.

👉 **Next Lesson:** **[Topic 14.4: Full-Stack Technical Interview Mastery: Explaining the Architecture](04-interview-readiness.md)**
