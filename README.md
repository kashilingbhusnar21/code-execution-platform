# Code Execution Platform

A scalable, LeetCode-style **code execution platform** where users write code in the browser, run it securely inside Docker sandboxes, persist source files in **AWS S3**, track submission history in **MySQL**, and process code asynchronously using **RabbitMQ**.

Built with **React + Monaco Editor** on the frontend and **Spring Boot** on the backend.

---

## Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [How It Works](#-how-it-works)
- [Project Structure](#-project-structure)
- [Setup Instructions](#-setup-instructions)
- [Run the Project](#-run-the-project)
- [API Endpoints](#-api-endpoints)
- [Security Features](#-security-features)
- [Screenshots](#-screenshots)
- [Future Improvements](#-future-improvements)
- [Key Learnings](#-key-learnings)
- [Resume Blurb](#-resume-blurb)
- [License](#-license)

---

## Features

- **Multi-language support** — Java, Python, and C++
- **Docker-based isolated execution** — warm containers with `docker exec` for fast runs
- **AWS S3** — durable storage for submitted source files
- **MySQL** — metadata, status, output snippets, and execution time
- **RabbitMQ** — asynchronous message queue for scalable code execution
- **Monaco Editor** — VS Code–like editing with syntax highlighting
- **Execution history** — browse past submissions and reload code into the editor
- **stdin support** — pass custom input to programs
- **Status classification** — `SUCCESS`, `COMPILATION_ERROR`, `RUNTIME_ERROR`, `TIMEOUT`, `PENDING`
- **Hardened sandbox** — CPU, memory, PID, and network limits
- **Async processing** — submissions queued and processed in background with polling

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  Frontend (React + Monaco Editor)                           │
│  • Code editor  • Language selector  • stdin  • History UI  │
└────────────────────────────┬────────────────────────────────┘
                             │  REST (HTTP / JSON)
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Spring Boot Backend                                        │
│  Controllers → CodeExecutionService                         │
└───────┬───────────────────┬───────────────────┬─────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌───────────────┐  ┌─────────────────┐  ┌────────────────────┐
│  AWS S3       │  │  Docker         │  │  MySQL             │
│  Store source │  │  Warm language  │  │  Submission meta:  │
│  files        │  │  containers     │  │  status, time, out │
└───────────────┘  └─────────────────┘  └────────────────────┘
        │                   │                   │
        └───────────────────┴───────────────────┘
                            │
                            ▼
                  ┌─────────────────┐
                  │  RabbitMQ       │
                  │  Message Queue  │
                  └─────────────────┘
```

### Request flow (Async with RabbitMQ)

1. **Frontend** sends `code`, `language`, `input`, and `userId` to the API.
2. **Controller** creates a submission with `PENDING` status and saves to MySQL.
3. **Producer** sends message to RabbitMQ `code.submission.queue`.
4. **Controller returns immediately** with `submissionId` (async response).
5. **Frontend polls** `/api/code/status/{id}` every 2 seconds for status updates.
6. **Consumer** receives message from queue and executes code using Docker.
7. **Result Consumer** receives execution result and updates MySQL with final status.
8. **Frontend detects status change** from PENDING to SUCCESS/ERROR and updates UI.
9. Users can open **History**, fetch past code from S3, and reload it into Monaco.

### Why this design?

| Component | Role | Why |
|-----------|------|-----|
| **S3** | File storage | Source files can be large; object storage scales better than stuffing full code into SQL rows. S3 keeps durable blobs; the DB only stores the key. |
| **MySQL** | Metadata & history | Fast queries for status, language, timestamps, and execution time. Ideal for listing history and dashboards. |
| **Docker** | Isolation & security | Untrusted user code never runs on the host JVM. Containers get memory/CPU/PID caps and **no network**. |
| **RabbitMQ** | Async processing | Decouples submission from execution, improves scalability, allows horizontal scaling of consumers. |

---

## Tech Stack

### Frontend
- React.js (Vite)
- Monaco Editor (`@monaco-editor/react`)
- Axios

### Backend
- Spring Boot
- Java 17
- REST APIs
- Spring Data JPA
- Spring AMQP (RabbitMQ)

### Execution
- Docker Desktop
- Warm containers (`java-container`, `python-container`, `cpp-container`)
- Images: `eclipse-temurin:17-jdk`, `python:3.10`, `gcc:latest`

### Cloud & Data
- AWS S3
- MySQL
- RabbitMQ

---

## How It Works

1. User writes code in **Monaco Editor** and optionally provides stdin.
2. Frontend calls `POST /api/code/run` with code, language, input, and userId.
3. Backend creates a submission with **PENDING** status and saves to MySQL.
4. Backend sends message to **RabbitMQ** queue with code details.
5. Backend returns **immediately** with `submissionId` (async pattern).
6. Frontend shows **PENDING** status and starts polling for updates.
7. **RabbitMQ Consumer** receives message and executes code in Docker container.
8. Consumer sends result to result queue.
9. **Result Consumer** updates MySQL with final status and output.
10. Frontend polling detects completion and displays results.
11. User can browse **History** and reload any past submission into the editor.

---

## Project Structure

```
code-executor/                          # Monorepo root
├── frontend/                           # React + Vite UI
│   ├── src/
│   │   ├── App.jsx                     # App state, run + history wiring
│   │   ├── components/
│   │   │   ├── CodeEditor.jsx          # Monaco + language dropdown
│   │   │   ├── OutputConsole.jsx       # stdout / stderr / timing
│   │   │   └── HistoryPanel.jsx        # Submission list + reload
│   │   └── services/
│   │       └── api.js                  # Axios API client with polling
│   ├── package.json
│   └── vite.config.js                  # Dev server + /api proxy → :8080
│
└── code-executor/                      # Spring Boot backend
    └── src/main/java/com/codeplatform/code_executor/
        ├── config/                     # Configuration classes
        │   └── RabbitMQConfig          # RabbitMQ queues, exchanges, bindings
        ├── controller/                 # REST controllers
        ├── service/                    # Business logic
        │   ├── CodeExecutionService
        │   ├── ContainerManagerService # Warm Docker containers
        │   ├── S3Service
        │   ├── RabbitMQProducerService # Send messages to RabbitMQ
        │   ├── RabbitMQConsumerService # Process code submissions
        │   └── RabbitMQResultConsumerService # Update DB with results
        ├── executor/                   # Strategy pattern
        │   ├── CodeExecutor            # Interface
        │   ├── ExecutorRegistry
        │   ├── JavaExecutor
        │   ├── PythonExecutor
        │   └── CppExecutor
        ├── entity/                     # JPA entities (Submission, …)
        ├── repository/                 # Spring Data repositories
        ├── dto/                        # API response models
        │   ├── CodeSubmissionMessage    # RabbitMQ message for submissions
        │   ├── CodeResultMessage        # RabbitMQ message for results
        │   └── RunResponse             # API response with submissionId
        └── model/                      # Request models (CodeRequest)
```

---

## Setup Instructions

### Prerequisites

- Java **17+**
- Maven **3.8+**
- Node.js **18+** and npm
- Docker Desktop (running)
- MySQL **8+**
- RabbitMQ (running on localhost:5672)
- AWS account with an S3 bucket

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd code-executor
```

### 2. Backend configuration

Create / edit `code-executor/src/main/resources/application.properties`:

```properties
spring.application.name=code-executor

# AWS S3 (use env vars or a secrets manager in production — do not commit real keys)
aws.accessKey=YOUR_AWS_ACCESS_KEY
aws.secretKey=YOUR_AWS_SECRET_KEY
aws.region=YOUR_AWS_REGION
aws.bucketName=YOUR_BUCKET_NAME

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/code_executor
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.listener.simple.concurrency=3
spring.rabbitmq.listener.simple.max-concurrency=10
```

Create the database:

```sql
CREATE DATABASE code_executor;
```

### 3. RabbitMQ Installation

Install RabbitMQ using one of these methods:

**Option 1: Docker**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

**Option 2: Windows Installer**
Download from https://www.rabbitmq.com/download.html

**Option 3: Chocolatey**
```bash
choco install rabbitmq
```

### 4. Docker images

Install **Docker Desktop**, then pull images:

```bash
docker pull eclipse-temurin:17-jdk
docker pull python:3.10
docker pull gcc:latest
```

Ensure `C:\temp` exists on Windows (the backend mounts it into containers as `/app`).

On first backend startup, warm containers are created automatically:

- `java-container`
- `python-container`
- `cpp-container`

### 5. Frontend dependencies

```bash
cd frontend
npm install
```

---

## Run the Project

### Backend (port `8080`)

```bash
cd code-executor
mvn spring-boot:run
```

### Frontend (port `3000`)

```bash
cd frontend
npm run dev
```

Open: [http://localhost:3000](http://localhost:3000)

> The Vite dev server proxies `/api` → `http://localhost:8080`.

---

## API Endpoints

### `POST /api/code/run`

Execute code asynchronously.

**Request body:**

```json
{
  "code": "public class Main { public static void main(String[] args) { System.out.println(\"Hi\"); } }",
  "language": "java",
  "input": "",
  "userId": "user1"
}
```

**Response (immediate):**

```json
{
  "output": "Submission queued for execution. Use the submission ID to check status.",
  "status": "PENDING",
  "executionTimeMs": 0,
  "submissionId": 50
}
```

Supported `language` values: `java`, `python`, `cpp`.

---

### `GET /api/code/status/{id}`

Check submission status (for polling).

**Response (when complete):**

```json
{
  "id": 50,
  "language": "java",
  "output": "Hi",
  "status": "SUCCESS",
  "createdAt": "2024-09-20T10:30:00",
  "executionTime": 420
}
```

---

### `GET /api/code/history`

List submission history for authenticated user (newest first).

---

### `GET /api/code/{id}`

Fetch the source code for a submission (downloaded from S3 using the stored key).

---

## Security Features

| Control | Detail |
|---------|--------|
| **Container isolation** | User code runs only inside Docker, not on the host JVM |
| **Memory limit** | `--memory=256m` |
| **CPU limit** | `--cpus=1` |
| **Process limit** | `--pids-limit=50` |
| **Network** | `--network=none` (no outbound/inbound network) |
| **Timeouts** | Long-running processes are killed after a configured timeout |
| **Ephemeral workdirs** | Unique `C:\temp\{user}_{ts}` folders deleted after each run |
| **Warm containers** | Long-lived sandboxes; only temp files are cleaned, not the host |
| **Async processing** | RabbitMQ decouples submission from execution, improving scalability |

---

## Screenshots

> Add UI screenshots here after running the app.

| Editor | Output & History |
|--------|------------------|
| `![Editor](docs/screenshots/editor.png)` | `![Output](docs/screenshots/output.png)` |

Suggested shots:

1. Monaco editor with language dropdown (Java / Python / C++)
2. Output console showing PENDING status and then SUCCESS with execution time
3. History panel with reload into editor

---

## Future Improvements

- [x] **Container reuse** — warm containers + `docker exec` (already implemented)
- [x] **Async processing** — RabbitMQ for scalable code execution (already implemented)
- [ ] More languages (JavaScript, Go, Rust, …)
- [ ] Judge / problem system with hidden test cases
- [ ] User authentication & multi-tenant isolation
- [ ] Rate limiting and abuse protection
- [ ] Deploy backend, frontend, and workers on AWS (ECS/EKS + RDS + S3 + RabbitMQ)
- [ ] Real-time streaming of stdout while code runs (WebSocket)
- [ ] Webhook notifications for completion instead of polling

---

## Key Learnings

- Designing a **safe code sandbox** with Docker resource and network limits
- Separating **blob storage (S3)** from **queryable metadata (MySQL)**
- Applying the **Strategy pattern** for pluggable language executors
- Building a full-stack flow: Monaco UI ↔ Spring REST ↔ Docker ↔ cloud storage
- Optimizing latency with **warm containers** instead of cold `docker run` per request
- Implementing **async processing** with RabbitMQ for scalability
- Using **polling pattern** for frontend status updates
- Designing **message-driven architecture** with producer-consumer pattern

---

## Resume Blurb

> Built a scalable code execution platform using Spring Boot, Docker, AWS S3, and RabbitMQ with multi-language support (Java, Python, C++), secure sandboxed execution, and asynchronous message processing for improved scalability, including Monaco-based editing and submission history.

---

## License

This project is intended for learning and portfolio use. Add a license file (e.g. MIT) if you plan to open-source it publicly.

---

**Happy coding!**
