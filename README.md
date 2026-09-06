# Code Execution Platform

A scalable, LeetCode-style **code execution platform** where users write code in the browser, run it securely inside Docker sandboxes, persist source files in **AWS S3**, and track submission history in **MySQL**.

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
- **Monaco Editor** — VS Code–like editing with syntax highlighting
- **Execution history** — browse past submissions and reload code into the editor
- **stdin support** — pass custom input to programs
- **Status classification** — `SUCCESS`, `COMPILATION_ERROR`, `RUNTIME_ERROR`, `TIMEOUT`
- **Hardened sandbox** — CPU, memory, PID, and network limits

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
```

### Request flow

1. **Frontend** sends `code`, `language`, `input`, and `userId` to the API.
2. **CodeExecutionService** writes a unique temp file under `C:\temp\{userId}_{timestamp}\`.
3. **S3Service** uploads the source file and stores the S3 key.
4. **ContainerManagerService** ensures a warm container is running, then executors run via `docker exec`.
5. Output and status are captured; **Submission** metadata is saved to MySQL.
6. A structured response (`stdout` / `stderr` / `status` / `executionTimeMs`) is returned to the UI.
7. Users can open **History**, fetch past code from S3, and reload it into Monaco.

### Why this design?

| Component | Role | Why |
|-----------|------|-----|
| **S3** | File storage | Source files can be large; object storage scales better than stuffing full code into SQL rows. S3 keeps durable blobs; the DB only stores the key. |
| **MySQL** | Metadata & history | Fast queries for status, language, timestamps, and execution time. Ideal for listing history and dashboards. |
| **Docker** | Isolation & security | Untrusted user code never runs on the host JVM. Containers get memory/CPU/PID caps and **no network**. |

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

### Execution
- Docker Desktop
- Warm containers (`java-container`, `python-container`, `cpp-container`)
- Images: `eclipse-temurin:17-jdk`, `python:3.10`, `gcc:latest`

### Cloud & Data
- AWS S3
- MySQL

---

## How It Works

1. User writes code in **Monaco Editor** and optionally provides stdin.
2. Frontend calls `POST /api/code/run` with code, language, input, and userId.
3. Backend creates a unique work folder and saves the source file (`Main.java` / `main.py` / `main.cpp`).
4. File is uploaded to **AWS S3**; the S3 key is recorded.
5. Code runs inside a **warm Docker container** for that language (`docker exec`).
6. stdout / stderr / exit status / timeout are classified.
7. Result metadata is stored in **MySQL**.
8. JSON response is shown in the Output console (stdout white, stderr red).
9. User can browse **History** and reload any past submission into the editor.

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
│   │       └── api.js                  # Axios API client
│   ├── package.json
│   └── vite.config.js                  # Dev server + /api proxy → :8080
│
└── code-executor/                      # Spring Boot backend
    └── src/main/java/com/codeplatform/code_executor/
        ├── controller/                 # REST controllers
        ├── service/                    # Business logic
        │   ├── CodeExecutionService
        │   ├── ContainerManagerService # Warm Docker containers
        │   └── S3Service
        ├── executor/                   # Strategy pattern
        │   ├── CodeExecutor            # Interface
        │   ├── ExecutorRegistry
        │   ├── JavaExecutor
        │   ├── PythonExecutor
        │   └── CppExecutor
        ├── entity/                     # JPA entities (Submission, …)
        ├── repository/                 # Spring Data repositories
        ├── dto/                        # API response models
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
```

Create the database:

```sql
CREATE DATABASE code_executor;
```

### 3. Docker images

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

### 4. Frontend dependencies

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

Execute code.

**Request body:**

```json
{
  "code": "public class Main { public static void main(String[] args) { System.out.println(\"Hi\"); } }",
  "language": "java",
  "input": "",
  "userId": "user1"
}
```

**Response (example):**

```json
{
  "output": "Hi",
  "stdout": "Hi",
  "stderr": "",
  "status": "SUCCESS",
  "executionTimeMs": 420
}
```

Supported `language` values: `java`, `python`, `cpp`.

---

### `GET /api/code/history/{userId}`

List submission history for a user (newest first).

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

---

## Screenshots

> Add UI screenshots here after running the app.

| Editor | Output & History |
|--------|------------------|
| `![Editor](docs/screenshots/editor.png)` | `![Output](docs/screenshots/output.png)` |

Suggested shots:

1. Monaco editor with language dropdown (Java / Python / C++)
2. Output console showing stdout / stderr and execution time
3. History panel with reload into editor

---

## Future Improvements

- [x] **Container reuse** — warm containers + `docker exec` (already implemented)
- [ ] More languages (JavaScript, Go, Rust, …)
- [ ] Judge / problem system with hidden test cases
- [ ] User authentication & multi-tenant isolation
- [ ] Rate limiting and abuse protection
- [ ] Deploy backend, frontend, and workers on AWS (ECS/EKS + RDS + S3)
- [ ] Real-time streaming of stdout while code runs

---

## Key Learnings

- Designing a **safe code sandbox** with Docker resource and network limits
- Separating **blob storage (S3)** from **queryable metadata (MySQL)**
- Applying the **Strategy pattern** for pluggable language executors
- Building a full-stack flow: Monaco UI ↔ Spring REST ↔ Docker ↔ cloud storage
- Optimizing latency with **warm containers** instead of cold `docker run` per request

---

## Resume Blurb

> Built a scalable code execution platform using Spring Boot, Docker, and AWS S3 with multi-language support (Java, Python, C++) and secure sandboxed execution, including Monaco-based editing and submission history.

---

## License

This project is intended for learning and portfolio use. Add a license file (e.g. MIT) if you plan to open-source it publicly.

---

**Happy coding!**
