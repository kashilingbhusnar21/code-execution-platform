# Docker-Based Secure Code Execution in Cloud
## MCA Project Presentation

---

### Slide 1: Title Slide

<div align="center">

# Docker-Based Secure Code Execution in Cloud

<br>

**Progressive Education Society's**  
**Modern College of Engineering, Pune**  
**MCA Department**

<br>

**Prepared by:**  
**Kashiling Bhusnar**  
**Roll No: 52107**

</div>

---

### Slide 2: Background Information

## Background

- **Online coding platforms** require secure execution environments
- **Running user code directly** on server is unsafe
- **Need isolation** for multi-language execution
- **Cloud-based systems** improve scalability and accessibility

<br>

### Solution
**Docker containers + Cloud services**

---

### Slide 3: Problem Statement

## Problem Statement

- **Unsafe code execution** can harm server
- **Multiple languages** require different environments
- **Difficult to manage** execution securely

<br>

### Need
System for **code execution**, **output tracking**, and **user management**

---

### Slide 4: Challenges – Analysis – Solution

## Challenges → Analysis → Solution

| Aspect | Details |
|--------|---------|
| **Challenges** | Security risks, multi-language support, execution isolation, data storage |
| **Analysis** | Direct execution is unsafe, virtual machines are heavy |
| **Solution** | Docker containers for isolation, AWS S3 for storage, JWT for secure access |

---

### Slide 5: System Overview

## System Overview

<div align="center">

```
User → Login → Write Code → Backend → Docker → Output
                                    ↓
                              Database + S3
```

</div>

<br>

**Flow:**
1. User logs in and writes code
2. Backend processes request
3. Docker executes code securely
4. Output stored and displayed

---

### Slide 6: System Architecture + Tech Stack

## System Architecture

<div align="center">

[INSERT ARCHITECTURE DIAGRAM IMAGE HERE]

</div>

<br>

**Architecture Layers:**

- **Frontend:** React + Monaco Editor (code editing, language selector, history)
- **Backend:** Spring Boot REST API (orchestrates execution)
- **Execution:** Docker warm containers (isolated sandbox)
- **Storage:** AWS S3 (files) + MySQL (metadata)

<br>

## Tech Stack

- **Frontend:** React.js, Monaco Editor, Axios
- **Backend:** Spring Boot, Java 17, REST APIs
- **Execution:** Docker (warm containers)
- **Cloud:** AWS S3, MySQL
- **Security:** JWT, BCrypt

---

### Slide 7: How Docker Works

## How Docker Works

<div align="center">

```
Image = Pre-configured environment (Java, Python, C++)
Container = Running instance
Warm Container = Long-running, ready for instant execution
```

</div>

<br>

**Warm Containers vs Traditional:**

- **Traditional (docker run):** Creates new container each time (3-5 sec latency)
- **Our approach (docker exec):** Reuses running container (sub-second)

<br>

**Execution Flow:**
```
Code → Backend → Warm Container → Output
```

<br>

**Images Used:** eclipse-temurin:17-jdk, python:3.10, gcc:latest

---

### Slide 8: Docker Implementation

## Docker Implementation

**Container Creation (Startup):**
- Creates 3 warm containers: java-container, python-container, cpp-container
- Resource limits: 256MB memory, 1 CPU, 50 PIDs, no network
- Mounts C:\temp as /app volume

<br>

**Execution Steps:**
1. Receive code → Create unique folder → Save file
2. Upload to S3 → Execute via docker exec → Capture output
3. Save to MySQL → Cleanup temp folder

<br>

**Security:** Memory/CPU limits, no network, 30s timeout

---

### Slide 9: Storage & User Management

## Storage & User Management

**Authentication:**
- JWT tokens for secure access
- BCrypt for password hashing

<br>

**Storage Strategy:**
- **S3:** Stores source code files (scalable, durable)
- **MySQL:** Stores metadata, users, history (fast queries)

<br>

**Data Flow:**
Code → S3 (file) + MySQL (metadata) → History retrieval

---

### Slide 10: Security Features

## Security Features

- **Docker isolation:** No host access
- **Resource limits:** 256MB memory, 1 CPU, 50 PIDs
- **Network disabled:** No internet access
- **JWT authentication:** Token-based access
- **BCrypt hashing:** Encrypted passwords
- **Execution timeout:** 30 seconds
- **Ephemeral folders:** Auto-deleted after execution
- **Volume restriction:** Only C:\temp mounted

---

### Slide 11: Conclusion

## Conclusion

### Key Achievements

✅ **Secure and scalable** system for code execution  
✅ **Multi-language support** (Java, Python, C++)  
✅ **Docker ensures** complete isolation  
✅ **AWS S3 + MySQL** for reliable storage  
✅ **JWT authentication** for secure access  

<br>

### Applications

- **Coding platforms** like LeetCode, HackerRank
- **SaaS systems** requiring code execution
- **Educational tools** for programming practice
- **Interview platforms** for technical assessments

<br>

<div align="center">

**Thank You**

</div>
