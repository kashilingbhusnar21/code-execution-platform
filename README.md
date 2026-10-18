# CodeArena — Distributed Code Execution & Online Judge Microservices Platform

A high-performance, scalable, LeetCode-style **online judge and distributed code execution platform**. Users solve Data Structures & Algorithms (DSA) problems or test arbitrary code in the browser, executed inside isolated **Docker sandboxes**, decoupled asynchronously via **RabbitMQ**, with source persistence in **AWS S3**, relational data in **MySQL**, and an interactive UI powered by **React & Monaco Editor**.

Built using a **Microservices Architecture** with a dedicated web API service (`code-api`), a headless asynchronous execution worker (`code-worker`), and a modern React frontend (`frontend`).

---

## Table of Contents

- [System Architecture](#system-architecture)
- [Key Features](#key-features)
- [Microservices Overview](#microservices-overview)
- [Execution Modes & Judge Engine](#execution-modes--judge-engine)
- [Security & Sandbox Hardening](#security--sandbox-hardening)
- [Tech Stack](#tech-stack)
- [Project Directory Structure](#project-directory-structure)
- [API Reference](#api-reference)
- [Setup & Running Instructions](#setup--running-instructions)
- [Database & Storage Strategy](#database--storage-strategy)
- [Interview & System Design Talking Points](#interview--system-design-talking-points)
- [Resume Blurb](#resume-blurb)

---

## System Architecture

```
                      ┌───────────────────────────────────────────────┐
                      │          React Frontend (Vite)                │
                      │  • Monaco Code Editor  • Problem Catalog UI   │
                      │  • Test Case Console   • Submission History   │
                      └───────────────────────┬───────────────────────┘
                                              │ HTTP REST (JWT Auth)
                                              ▼
                      ┌───────────────────────────────────────────────┐
                      │          code-api (Port 8080)                 │
                      │  • Spring Boot REST Controller & JWT Filter   │
                      │  • Problem Catalog & User Streak Service      │
                      │  • Creates PENDING Submission in MySQL        │
                      └──────────────┬─────────────────┬──────────────┘
                                     │                 │
                Publishes Job to AMQP│                 │ Consumes Finished Result
                                     ▼                 ▲
                ┌───────────────────────────────────────────────┐
                │          RabbitMQ Message Broker              │
                │  • Exchange: code.exchange (Topic Exchange)   │
                │  • Queue:    code.submission.queue            │
                │  • Queue:    code.result.queue                │
                └──────────────┬────────────────────────────────┘
                               │
            Pulls Execution Job│
                               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       code-worker (Headless Service)                        │
│  • Spring AMQP Listener (No HTTP Server, purely compute)                    │
│  • Dynamic DSA Harness Generator (Main wrapper for Solution classes)        │
│  • Warm Container Manager (`docker exec`, sub-second execution)             │
│  • Test Case Evaluation & Output Normalizer (Early exit on TLE/WA)          │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                      ┌────────────────┴────────────────┐
                      │                                 │
                      ▼                                 ▼
         ┌─────────────────────────┐       ┌─────────────────────────┐
         │       MySQL 8.0         │       │         AWS S3          │
         │ • Users & Streak stats  │       │ • Stored source blobs   │
         │ • Problems & Test Cases │       │ • Presigned upload URLs │
         │ • Submissions & Verdicts│       │ • Immutable code files  │
         └─────────────────────────┘       └─────────────────────────┘
```

### End-to-End Request Flow (Async Request-Reply Pattern)

1. **User Request**: The user clicks **Run** (custom stdin) or **Submit** (grading against test cases) in the Monaco editor.
2. **Ingestion & Non-Blocking Response (`code-api`)**:
   - `code-api` validates JWT auth and saves a `Submission` record with status `PENDING` in MySQL.
   - For `/submit`, test cases (public samples & private hidden tests) are loaded from MySQL.
   - A `CodeSubmissionMessage` is published to RabbitMQ (`code.submission.queue`).
   - The API immediately returns HTTP `200 OK` with `{ submissionId, status: "PENDING" }` in **< 50ms**, preventing web thread exhaustion.
3. **Frontend Polling**: The React client starts polling `GET /api/code/status/{id}` every 2 seconds.
4. **Asynchronous Execution (`code-worker`)**:
   - `code-worker` consumes the job from `code.submission.queue`.
   - Prepares an ephemeral sandbox folder (`{userId}_{submissionId}_{timestamp}`).
   - If LeetCode-style code (`class Solution`) is detected, `DsaHarnessGenerator` injects the language-specific driver (`Main.java`, `main.py`, `solution.h` + `main.cpp`).
   - Executes commands inside long-running **warm Docker containers** via `docker exec`.
   - `OutputNormalizer` grades results against test cases with **early-exit optimization** on the first failure.
   - Cleanups the ephemeral directory in a `finally` block.
   - Publishes `CodeResultMessage` to `code.result.queue`.
5. **Result Processing & Streak Calculation (`code-api`)**:
   - `RabbitMQResultConsumerService` in `code-api` consumes the result.
   - Updates the submission in MySQL with status (`ACCEPTED`, `WRONG_ANSWER`, `COMPILE_ERROR`, `TIME_LIMIT_EXCEEDED`, `RUNTIME_ERROR`), execution time, and passed test-case counts.
   - On `ACCEPTED` verdicts, automatically calculates and increments the user's daily problem-solving streak.
   - The frontend's next poll picks up the verdict and renders the outcome.

---

## Key Features

- **Microservices Architecture**: Cleanly decoupled API gateway (`code-api`) and background compute engine (`code-worker`).
- **Multi-Language Support**: Java 17 (`eclipse-temurin:17-jdk`), Python 3.10 (`python:3.10`), and C++ (`gcc:latest`).
- **Sub-Second Execution (Warm Containers)**: Pre-warmed containers with `docker exec` eliminate the 3–5s cold-start overhead of traditional `docker run --rm`.
- **LeetCode-Style Online Judge**:
  - Supports `class Solution` without requiring users to write boilerplate `main()` or input parsers.
  - Test harness generation for DSA problem types (Two Sum, Valid Parentheses, Binary Search, Reverse String, Max Subarray Sum).
  - Public sample test cases vs. hidden regression test cases.
- **Strict Sandbox Security**: Enforces zero network access (`--network=none`), memory limits (`256MB`), CPU caps (`1 CPU`), process limits (`50 PIDs`), and hard 30-second timeouts.
- **Output Normalization**: Tolerates whitespace differences, CRLF/LF line endings, boolean casing (`true`/`True`), quote variations, alternative outputs (`|`), and unordered integer pairs for problems like Two Sum.
- **Daily Streak & User Progress**: Tracks solved problem count, difficulty breakdown (Easy, Medium, Hard), topic stats, and consecutive active days.
- **Dual Storage Segregation**: Structured metadata and queryable history in MySQL; durable, immutable code files in AWS S3.
- **Monaco Editor UI**: VS Code editing experience with syntax highlighting, language templates, stdin console, and submission history review.

---

## Microservices Overview

### 1. `code-api` (Port 8080)
- **Role**: Public API Gateway and business orchestrator.
- **Responsibilities**:
  - User registration, login, and stateless JWT token authentication with BCrypt password hashing.
  - DSA Problem catalog management, filtering by difficulty and topic, keyword search.
  - User progress & streak calculation engine.
  - Ingests execution requests, records `PENDING` states, and pushes tasks to RabbitMQ.
  - Consumes execution results from RabbitMQ and updates MySQL.
  - AWS S3 presigned URL generation and code retrieval.

### 2. `code-worker` (Headless Compute Engine)
- **Role**: Isolated, asynchronous execution and grading worker.
- **Responsibilities**:
  - Runs in non-web mode (`spring.main.web-application-type=none`) — no HTTP server, no database connection.
  - Listens to RabbitMQ `code.submission.queue`.
  - Manages warm Docker containers (`ContainerManagerService`) using `ReentrantLock` for thread safety.
  - Injects problem harnesses (`DsaHarnessGenerator`) and executes user code with `docker exec`.
  - Grades multiple test cases sequentially, normalizing outputs and applying early termination.
  - Emits graded verdicts (`CodeResultMessage`) to `code.result.queue`.

---

## Execution Modes & Judge Engine

| Feature | Ad-Hoc Run (`/api/code/run`) | Problem Submission (`/api/code/submit`) |
| :--- | :--- | :--- |
| **Trigger** | Clicking "Run Code" | Clicking "Submit" |
| **Input** | Custom user stdin or single sample input | Full suite of test cases (sample + hidden) |
| **Harness** | Direct execution (or harness if `class Solution` used) | Injected harness (`Main` driver calls `Solution`) |
| **Grading** | Returns stdout / stderr / status | Grades each test case; computes pass ratio |
| **Verdict** | `SUCCESS`, `COMPILATION_ERROR`, `RUNTIME_ERROR`, `TIMEOUT` | `ACCEPTED`, `WRONG_ANSWER`, `COMPILE_ERROR`, `TIME_LIMIT_EXCEEDED`, `RUNTIME_ERROR` |
| **Streak** | Updates streak if standalone success | Increments user problem streak on `ACCEPTED` |
| **Test Output** | Shows raw stdout/stderr | Displays pass/fail per sample; masks hidden test inputs/outputs |

### Status Verifications

- **`ACCEPTED`**: All test cases passed within time and memory constraints.
- **`WRONG_ANSWER`**: Output did not match expected output on one or more test cases.
- **`COMPILE_ERROR`**: Code failed compilation (`javac`, `g++`, or Python syntax/indentation checks).
- **`TIME_LIMIT_EXCEEDED`**: Execution exceeded the allowed execution threshold (30 seconds).
- **`RUNTIME_ERROR`**: Non-zero process exit code (e.g. `NullPointerException`, `IndexOutOfBoundsException`, segmentation fault).
- **`PENDING`**: Request enqueued in RabbitMQ waiting for or undergoing execution.

---

## Security & Sandbox Hardening

Running untrusted user code poses major security challenges. The platform uses multi-layer isolation:

```
┌─────────────────────────────────────────────────────────────┐
│  Host Operating System / JVM                                │
│                                                             │
│   ┌──────────────────────────────────────────────────────┐  │
│   │  Docker Warm Container Sandbox                      │  │
│   │                                                      │  │
│   │   • --network=none    (No inbound/outbound traffic)  │  │
│   │   • --memory=256m     (OOM protection for host)      │  │
│   │   • --cpus=1          (CPU starvation prevention)    │  │
│   │   • --pids-limit=50   (Fork bomb immunity)           │  │
│   │   • 30s Timeout       (Forcible process kill)        │  │
│   │                                                      │  │
│   │   ┌──────────────────────────────────────────────┐   │  │
│   │   │ Mounted Workspace (/app/{unique_ephemeral})  │   │  │
│   │   │ User Code (Solution.java, main.py, etc.)     │   │  │
│   │   └──────────────────────────────────────────────┘   │  │
│   └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

1. **Network Disabled (`--network=none`)**: Blocks socket connections, outbound HTTP requests, cryptomining pools, reverse shells, and SSRF attacks into the local network.
2. **Process Limit (`--pids-limit=50`)**: Neutralizes fork bombs like `:(){ :|:& };:`.
3. **Memory Quota (`--memory=256m`)**: Caps memory allocation, preventing memory exhaustion on the host node.
4. **CPU Ceiling (`--cpus=1`)**: Prevents infinite loops from consuming 100% of all host CPU cores.
5. **Strict Timeout**: Process execution terminates forcibly via `Process.destroyForcibly()` after 30 seconds.
6. **Ephemeral Filesystem Mount**: Only a specific temporary folder is mounted as `/app`. Each run gets an isolated subfolder (`{userId}_{timestamp}`), wiped in a `finally` block immediately after execution.

---

## Tech Stack

| Domain | Technology |
| :--- | :--- |
| **Frontend** | React 18 (Vite), Monaco Editor (`@monaco-editor/react`), Axios, Tailwind CSS |
| **API Backend** | Java 17, Spring Boot 3, Spring Security, JWT, Spring Data JPA |
| **Execution Worker** | Spring Boot (Headless, `web-application-type=none`), Java ProcessBuilder, Docker CLI |
| **Message Broker** | RabbitMQ 3 (Topic Exchange, Jackson JSON Serializer) |
| **Database** | MySQL 8.0 (Users, Problems, Topics, TestCases, Submissions) |
| **Object Storage** | AWS S3 (AWS SDK v2 S3Client & S3Presigner) |
| **Containerization** | Docker (`eclipse-temurin:17-jdk`, `python:3.10`, `gcc:latest`) |

---

## Project Directory Structure

```
code-execution-microservices-test/
├── frontend/                                   # React + Vite UI
│   ├── src/
│   │   ├── pages/
│   │   │   ├── EditorPage.jsx                  # Standalone playground code runner
│   │   │   ├── ProblemListPage.jsx             # Problem catalog & user streak stats
│   │   │   ├── ProblemDetailPage.jsx           # Split-screen LeetCode problem editor
│   │   │   └── LoginPage.jsx                   # User login & registration
│   │   ├── components/
│   │   │   ├── CodeEditor.jsx                  # Monaco editor component
│   │   │   ├── ProblemStatement.jsx            # Markdown description & testcase samples
│   │   │   ├── OutputConsole.jsx               # Execution console & test results
│   │   │   └── HistoryPanel.jsx                # Submission logs & past code reload
│   │   └── services/
│   │       └── api.js                          # Axios client with JWT interceptor & polling
│   └── package.json
│
├── code-api/                                   # Spring Boot API Gateway (Port 8080)
│   ├── src/main/java/com/codeplatform/code_executor/
│   │   ├── config/RabbitMQConfig.java          # Exchanges, Queues, Routing Keys
│   │   ├── controller/
│   │   │   ├── AuthController.java             # /auth/register, /auth/login, /auth/me
│   │   │   ├── CodeExecutionController.java    # /api/code/run, /api/code/submit, /status, /history
│   │   │   ├── ProblemController.java          # /api/problems CRUD & /progress
│   │   │   └── S3Controller.java               # Presigned upload URLs
│   │   ├── entity/                             # User, Problem, Topic, TestCase, Submission
│   │   ├── repository/                         # Spring Data JPA Repositories
│   │   ├── security/                           # JwtFilter, JwtUtil, SecurityConfig
│   │   └── service/
│   │       ├── AuthService.java                # Authentication logic & BCrypt hashing
│   │       ├── ProblemService.java             # Problem filtering & user progress metrics
│   │       ├── RabbitMQProducerService.java    # Sends CodeSubmissionMessage to RabbitMQ
│   │       ├── RabbitMQResultConsumerService.java # Consumes results & updates user streak
│   │       └── S3Service.java                  # S3 upload, download, and presigning
│   └── pom.xml
│
└── code-worker/                                # Headless Execution Worker (No HTTP)
    ├── src/main/java/com/codeplatform/code_executor/
    │   ├── config/RabbitMQConfig.java          # RabbitMQ Queue configuration
    │   ├── executor/
    │   │   ├── CodeExecutor.java               # Strategy interface
    │   │   ├── AbstractDockerExecutor.java     # Base class with warm container integration
    │   │   ├── JavaExecutor.java               # Java compiler & runner
    │   │   ├── PythonExecutor.java             # Python runner
    │   │   ├── CppExecutor.java                # G++ compilation & runner
    │   │   ├── DsaHarnessGenerator.java        # Synthesizes Main driver for Solution classes
    │   │   ├── DsaProblemType.java             # Problem type detection & classifier
    │   │   ├── OutputNormalizer.java           # Flexible test output comparator
    │   │   └── ExecutorRegistry.java           # Dynamic language executor lookup
    │   └── service/
    │       ├── ContainerManagerService.java    # Warm Docker container lifecycle & `docker exec`
    │       ├── CodeExecutionService.java       # Grading loop & test-case evaluation
    │       ├── RabbitMQConsumerService.java    # AMQP listener for code submissions
    │       └── RabbitMQProducerService.java    # Sends CodeResultMessage back to API
    └── pom.xml
```

---

## API Reference

### Authentication (`/auth`)

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register new user (username, email, password) | No |
| `POST` | `/auth/login` | Login and receive Bearer JWT token | No |
| `GET` | `/auth/me` | Fetch authenticated user profile | Yes (JWT) |

### Code Execution (`/api/code`)

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/code/run` | Execute ad-hoc code with optional custom stdin | Yes (JWT) |
| `POST` | `/api/code/submit` | Submit solution for evaluation against test cases | Yes (JWT) |
| `GET` | `/api/code/status/{id}` | Poll submission verdict, output, and execution time | Yes (JWT) |
| `GET` | `/api/code/history` | List submission history (supports `?problemId=...`) | Yes (JWT) |
| `GET` | `/api/code/{id}` | Fetch original source code from AWS S3 | Yes (JWT) |

### Problem Catalog (`/api/problems`)

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/problems` | List all DSA problems | No |
| `GET` | `/api/problems/{id}` | Get problem description, sample test cases, and starter code | No |
| `GET` | `/api/problems/progress` | Get user solve count, difficulty breakdown, and streak | Yes (JWT) |
| `GET` | `/api/problems/filter` | Filter by `difficulty` (`EASY`, `MEDIUM`, `HARD`) or `topicId` | No |
| `GET` | `/api/problems/search` | Search problems by keyword | No |
| `GET` | `/api/problems/topics/all`| List all topics (Arrays, Strings, Dynamic Programming, etc.) | No |

---

## Setup & Running Instructions

### Prerequisites

- **Java 17+** & **Maven 3.8+**
- **Node.js 18+** & **npm**
- **Docker Desktop** (running)
- **MySQL 8.0+**
- **RabbitMQ 3+**
- **AWS S3 Bucket** (with IAM credentials)

### 1. Start Infrastructure Dependencies

**RabbitMQ:**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

**Docker Images for Warm Sandboxes:**
```bash
docker pull eclipse-temurin:17-jdk
docker pull python:3.10
docker pull gcc:latest
```

**MySQL Database:**
```sql
CREATE DATABASE code_executor;
```

---

### 2. Configure Environment Variables

Create or set the following environment variables (or update `application.properties`):

```properties
# MySQL
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/code_executor
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# AWS S3
AWS_REGION=us-east-1
AWS_BUCKET_NAME=your-bucket-name
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key

# JWT
JWT_SECRET=your-256-bit-secret-key
```

---

### 3. Start the Microservices

**Terminal 1 — API Gateway (`code-api` on port `8080`):**
```bash
cd code-api
mvn spring-boot:run
```

**Terminal 2 — Execution Worker (`code-worker` headless):**
```bash
cd code-worker
mvn spring-boot:run
```
*(On startup, `code-worker` will verify and initialize `java-container`, `python-container`, and `cpp-container` automatically).*

**Terminal 3 — Frontend (`frontend` on port `3000` / `5173`):**
```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## Database & Storage Strategy

### Relational Schema (MySQL)
- `users`: User credentials, BCrypt hashes, and streak tracking (`streak_count`, `last_submission_date`).
- `problems`: Problem statements, difficulty enums (`EASY`, `MEDIUM`, `HARD`), time/memory limits, constraints, and JSON starter templates.
- `topics`: Problem categories (e.g. Arrays, Hash Tables, Two Pointers).
- `problem_topics`: Many-to-many relationship join table.
- `test_cases`: Test inputs, expected outputs, sample flags (`is_sample`), and hidden flags (`is_hidden`).
- `submissions`: Execution records, status enums, execution time (ms), passed count, total count, error details, and S3 object keys.

### Object Storage (AWS S3)
- Stored Key Format: `{userId}_{timestamp}_{fileName}`
- Rather than storing large code strings in MySQL rows (which causes table fragmentation, heavy memory buffer usage, and slow index scans), code blobs are stored as immutable files in AWS S3. MySQL stores only the metadata and the S3 key reference.

---

## Interview & System Design Talking Points

### 1. Why use RabbitMQ instead of synchronous REST calls between API and Worker?
> **Answer**: Code compilation and multi-test execution take variable time (500ms to several seconds). If done synchronously, Tomcat's HTTP worker threads would remain blocked, causing thread starvation, dropped incoming connections, and client timeouts during traffic surges. With RabbitMQ, the API responds in under 50ms with a `PENDING` status. RabbitMQ absorbs sudden spikes, guarantees message persistence, and allows worker nodes to scale horizontally without altering the web layer.

### 2. What is the performance benefit of Warm Containers over Cold Containers?
> **Answer**: Creating and tearing down containers on every request (`docker run --rm`) introduces a **3 to 5 second latency penalty** due to namespace allocation, cgroup setup, and daemon negotiations. By maintaining pre-spawned warm containers running `sleep infinity` and invoking commands via `docker exec`, execution begins almost instantaneously, dropping execution overhead down to **sub-second speeds**.

### 3. How is the system secured against malicious user code?
> **Answer**: Multi-layered defense-in-depth:
> - **Network isolation (`--network=none`)**: Prevents outbound attacks, SSRF, reverse shells, or cryptomining.
> - **Process cap (`--pids-limit=50`)**: Neutralizes fork bomb denial-of-service attempts.
> - **Resource caps (`--memory=256m`, `--cpus=1`)**: Prevents noisy-neighbor host starvation.
> - **Execution timeouts (30s)**: Force-terminates infinite loops.
> - **Ephemeral workspaces**: Files are namespaced per submission and purged in `finally` blocks.

### 4. How does the LeetCode test harness generator work?
> **Answer**: Users write only a `class Solution` method. The `DsaHarnessGenerator` inspects the problem type and generates a language-specific driver program (`Main.java`, `main.py`, `solution.h` + `main.cpp`). This synthetic driver parses standard input into types (such as `int[]` or `String`), invokes the user's method, formats the return value, and writes to stdout. The `OutputNormalizer` then performs robust grading against test cases.

### 5. How would you scale this platform to 50,000 active users?
> **Answer**:
> 1. **Worker Autoscaling**: Deploy `code-worker` pods on Kubernetes using **KEDA** (Kubernetes Event-driven Autoscaling) to scale worker pods dynamically based on RabbitMQ queue depth.
> 2. **Push Notifications (WebSockets / SSE)**: Replace 2-second HTTP polling with WebSockets or Server-Sent Events, where `code-api` pushes results directly to the browser upon receiving the RabbitMQ message.
> 3. **MicroVM Sandboxes**: Replace local Docker daemon with microVMs such as **AWS Firecracker** or **gVisor (`runsc`)** for hardware-assisted virtualization and sub-millisecond isolation boundaries.
> 4. **Caching Layer**: Cache problem metadata, starter templates, and test cases in Redis to minimize database read traffic.

---

## Resume Blurb

> **CodeArena — Distributed Code Execution Platform**  
> *Java, Spring Boot, React, Docker, RabbitMQ, MySQL, AWS S3, Monaco Editor*
> - Architected a distributed code execution and online judge platform supporting Java, Python, and C++ using an asynchronous microservices architecture.
> - Designed a secure sandbox using warm Docker containers with CPU, memory, PID, and network limits (`--network=none`), cutting container spin-up overhead from 4s to sub-second execution speeds via `docker exec`.
> - Decoupled web ingestion from CPU-bound code execution using RabbitMQ topic exchanges and non-blocking polling, preventing web thread exhaustion during traffic spikes.
> - Built an automated DSA test-case grading engine with dynamic harness generation for `Solution` classes, flexible output normalization, and early-exit optimization on test failures.
> - Implemented dual-storage architecture segregating queryable relational submission metadata in MySQL from durable source code files in AWS S3.