# Full-Stack Capstone Implementation: React Frontend + Spring Backend

> **Phase 14: Full-Stack Capstone Project & Deployment**  
> *Estimated Reading Time: 90 minutes | Total Practice Effort: ~8 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In the previous lesson, you drafted the architectural blueprints and REST API contracts for the Capstone platform.

Now, you will build the **End-to-End Working Implementation**:
1. A user logs in via React and receives a signed JWT token.
2. The user navigates to the Course Catalog, searches for courses, and views details.
3. The user clicks **"Enroll Now"**:
   - The React frontend makes an authenticated Axios POST request with the Bearer token.
   - Spring Security authenticates the request and extracts the current user ID.
   - The `@Transactional` service validates the course, enforces duplicate enrollment prevention, writes to MySQL, and emits a RabbitMQ event.
   - The frontend receives confirmation and updates the UI state in real time!

This lesson represents the culmination of your full-stack engineering training.

### Prerequisites
- [Topic 10.9: Spring Transaction Management](../10-spring/09-transactional.md)
- [Topic 11.3: Stateless JWT Security](../11-production/03-jwt-auth.md)
- [Topic 13.5: Full-Stack Axios Integration](../13-react/05-axios-spring-integration.md)
- [Topic 14.1: Capstone Architecture & Specifications](01-fullstack-architecture.md)

---

## 2. Plain-English Mental Model

```
[ Connecting the Nervous System to the Muscles ]
- The React Frontend is the hand and eyes (Receives user clicks, displays feedback).
- The Axios HTTP Client is the spinal cord (Transmits electrical signals back and forth).
- The Spring Boot Backend is the brain (Validates rules, calculates logic, coordinates memory).
- The MySQL Database is long-term memory (Saves permanent records).
- All parts working in seamless, synchronous harmony!
```

---

## 3. Backend Implementation: The Enrollment Feature

Let's build the core transactional feature of our platform: **Course Enrollment**.

### 1. The Repository with `JOIN FETCH` (N+1 Immune!):
```java
package com.career.learning.repository;

import com.career.learning.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Fast check to prevent duplicate enrollments
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // JOIN FETCH loads the Course association in 1 single query (Eliminating N+1!)
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.user.id = :userId ORDER BY e.enrolledAt DESC")
    List<Enrollment> findMyEnrollmentsWithCourse(@Param("userId") Long userId);
}
```

---

### 2. The Transactional Service Layer:
```java
package com.career.learning.service;

import com.career.learning.dto.CapstoneContracts.EnrollmentResponse;
import com.career.learning.entity.Course;
import com.career.learning.entity.Enrollment;
import com.career.learning.entity.User;
import com.career.learning.exception.DuplicateResourceException;
import com.career.learning.exception.ResourceNotFoundException;
import com.career.learning.repository.CourseRepository;
import com.career.learning.repository.EnrollmentRepository;
import com.career.learning.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final OrderEventProducer eventProducer;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             OrderEventProducer eventProducer) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional(rollbackFor = Exception.class)
    public EnrollmentResponse enrollStudent(Long userId, Long courseId) {
        log.info("Processing enrollment for userId: {} in courseId: {}", userId, courseId);

        // 1. Guard against duplicate enrollment
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new DuplicateResourceException("You are already enrolled in this course.");
        }

        // 2. Fetch User and Course
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        // 3. Create and persist Enrollment entity
        Enrollment enrollment = new Enrollment(student, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // 4. Publish Asynchronous Event to RabbitMQ (Email confirmation + Invoice)
        eventProducer.publishOrderPlaced(savedEnrollment.getId(), student.getEmail(), course.getPrice());

        log.info(" Enrollment #{} successfully completed for {}", savedEnrollment.getId(), student.getEmail());

        return new EnrollmentResponse(
                savedEnrollment.getId(),
                course.getId(),
                course.getTitle(),
                savedEnrollment.getProgressPercentage(),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(Long userId) {
        return enrollmentRepository.findMyEnrollmentsWithCourse(userId).stream()
                .map(e -> new EnrollmentResponse(
                        e.getId(),
                        e.getCourse().getId(),
                        e.getCourse().getTitle(),
                        e.getProgressPercentage(),
                        e.getEnrolledAt()
                ))
                .toList();
    }
}
```

---

### 3. The REST Controller:
```java
package com.career.learning.controller;

import com.career.learning.dto.CapstoneContracts.EnrollmentResponse;
import com.career.learning.security.UserPrincipal;
import com.career.learning.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // POST /api/v1/enrollments/{courseId}
    @PostMapping("/{courseId}")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentResponse response = enrollmentService.enrollStudent(currentUser.getId(), courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/v1/enrollments/my
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(currentUser.getId());
        return ResponseEntity.ok(enrollments);
    }
}
```

---

## 4. Frontend Implementation: The React SPA

Now let's build the interactive frontend in React 18:

### 1. Global Auth Context (`src/context/AuthContext.jsx`):
```jsx
import React, { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("accessToken"));
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    if (savedUser && token) {
      setUser(JSON.parse(savedUser));
    }
    setIsLoading(false);
  }, [token]);

  const login = (userData, accessToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("user", JSON.stringify(userData));
    setToken(accessToken);
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

---

### 2. The Course Detail & Enrollment Card (`src/pages/CourseDetailPage.jsx`):
```jsx
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import { useAuth } from "../context/AuthContext";

const CourseDetailPage = () => {
  const { slug } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [course, setCourse] = useState(null);
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [isEnrolling, setIsEnrolling] = useState(false);
  const [feedback, setFeedback] = useState(null);

  useEffect(() => {
    const loadCourse = async () => {
      try {
        const res = await apiClient.get(`/courses/slug/${slug}`);
        setCourse(res.data);
      } catch (err) {
        setFeedback({ type: "error", message: "Failed to load course details." });
      }
    };
    loadCourse();
  }, [slug]);

  const handleEnroll = async () => {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: { pathname: `/courses/${slug}` } } });
      return;
    }

    setIsEnrolling(true);
    setFeedback(null);

    try {
      // Authenticated POST request (Axios attaches Bearer token automatically!)
      await apiClient.post(`/enrollments/${course.id}`);

      setIsEnrolled(true);
      setFeedback({ type: "success", message: "🎉 Successfully enrolled! Redirecting to dashboard..." });
      
      setTimeout(() => {
        navigate("/dashboard");
      }, 1500);

    } catch (err) {
      setFeedback({ type: "error", message: err.message || "Enrollment failed." });
    } finally {
      setIsEnrolling(false);
    }
  };

  if (!course) return <div className="spinner">Loading course...</div>;

  return (
    <article className="course-detail-page">
      <header className="course-hero">
        <h1>{course.title}</h1>
        <p className="description">{course.description}</p>
        <span className="instructor">Taught by {course.instructorName}</span>
      </header>

      <div className="enrollment-sidebar">
        <div className="price-box">
          <span className="price-tag">${course.price.toFixed(2)}</span>
          
          {feedback && (
            <div className={`alert alert-${feedback.type}`}>
              {feedback.message}
            </div>
          )}

          <button
            onClick={handleEnroll}
            disabled={isEnrolling || isEnrolled}
            className={`btn-enroll ${isEnrolled ? "enrolled" : ""}`}
          >
            {isEnrolling ? "Processing..." : isEnrolled ? "✓ Enrolled" : `Enroll Now for $${course.price}`}
          </button>
        </div>
      </div>
    </article>
  );
};

export default CourseDetailPage;
```

---

### 3. The Student Dashboard with Live Progress (`src/pages/DashboardPage.jsx`):
```jsx
import React, { useState, useEffect } from "react";
import apiClient from "../api/apiClient";
import { useAuth } from "../context/AuthContext";

const StudentDashboard = () => {
  const { user } = useAuth();
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchEnrollments = async () => {
      try {
        const response = await apiClient.get("/enrollments/my");
        setEnrollments(response.data);
      } catch (err) {
        console.error("Failed to load dashboard:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchEnrollments();
  }, []);

  if (loading) return <div className="spinner">Loading your learning path...</div>;

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1>Welcome back, {user?.fullName}! 👋</h1>
        <p>You have {enrollments.length} active learning track(s).</p>
      </header>

      <section className="enrolled-courses-grid">
        {enrollments.map((item) => (
          <div key={item.enrollmentId} className="enrollment-card">
            <h3>{item.courseTitle}</h3>
            
            <div className="progress-section">
              <div className="progress-bar-bg">
                <div 
                  className="progress-bar-fill" 
                  style={{ width: `${item.progressPercentage}%` }}
                />
              </div>
              <span className="progress-text">{item.progressPercentage}% Completed</span>
            </div>

            <a href={`/learn/${item.courseId}`} className="btn-resume">
              Resume Learning →
            </a>
          </div>
        ))}
      </section>
    </div>
  );
};

export default StudentDashboard;
```

---

## 5. Common Rookie Mistakes & Anti-Patterns

### Anti-Pattern 1: Race Condition on Enrollment Button
If the user double-clicks the "Enroll" button rapidly, two requests hit Spring Boot concurrently.  
*Remediation:*
1. Frontend: Instantly set `disabled={isEnrolling}` on the first click.
2. Backend: Database unique constraint `CONSTRAINT uk_user_course UNIQUE (user_id, course_id)` guarantees that the database engine rejects any simultaneous second insert!

### Anti-Pattern 2: Storing Sensitive User Information in `localStorage`
Never store credit card numbers, passwords, or administrative secret keys in browser `localStorage`.  
`localStorage` is accessible to any script running on the origin (vulnerable to XSS). Only store the stateless JWT `accessToken`.

---

## 6. Guided Hands-On Exercise

### The Scenario
You are writing a unit test verifying `EnrollmentService.enrollStudent()`:
1. When a student is already enrolled, `DuplicateResourceException` is thrown.
2. `enrollmentRepository.save()` is **never called**.
3. `eventProducer.publishOrderPlaced()` is **never called**.

### Your Challenge:
Write the Mockito unit test.

<details>
<summary>💡 Hint: Mock existsByUserIdAndCourseId</summary>

```java
when(enrollmentRepo.existsByUserIdAndCourseId(1L, 10L)).thenReturn(true);
assertThrows(DuplicateResourceException.class, () -> service.enrollStudent(1L, 10L));
verify(enrollmentRepo, never()).save(any());
```
</details>

<details>
<summary>✅ Solution</summary>

```java
package com.career.learning.service;

import com.career.learning.exception.DuplicateResourceException;
import com.career.learning.repository.CourseRepository;
import com.career.learning.repository.EnrollmentRepository;
import com.career.learning.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    @DisplayName("Should reject enrollment and never save or emit event if user is already enrolled")
    void shouldPreventDuplicateEnrollment() {
        Long userId = 42L;
        Long courseId = 101L;

        // Stub repository to report user is already enrolled
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        assertThrows(
            DuplicateResourceException.class,
            () -> enrollmentService.enrollStudent(userId, courseId)
        );

        // Verify zero persistence mutations occurred
        verify(enrollmentRepository, never()).save(any());
        verifyNoInteractions(eventProducer);
    }
}
```
</details>

---

## 7. Knowledge Check & Interview Defense

### Quick Quiz
1. **How does the Capstone application prevent a student from being double-charged or double-enrolled if they click "Enroll" twice in rapid succession?**
   - [x] A) Both by disabling the button in React UI (`isEnrolling`), checking `existsByUserIdAndCourseId` in the `@Transactional` service, and enforcing a unique database constraint `UNIQUE (user_id, course_id)` in MySQL.
   - [ ] B) By shutting down the server.
   - [ ] C) By clearing the browser cache.
   - [ ] D) React automatically prevents all double clicks.

2. **In the Student Dashboard query, why is `JOIN FETCH e.course` used in the repository?**
   - [x] A) To eagerly fetch the associated `Course` entity in the exact same SQL query, completely eliminating the N+1 query performance bottleneck.
   - [ ] B) To sort courses alphabetically.
   - [ ] C) To convert the response to JSON.
   - [ ] D) It is required by Spring Security.

3. **In the React `useAuth` hook, why is `user` stored in React state as well as `localStorage`?**
   - [x] A) React state provides reactive UI updates across all components when the user logs in or out, while `localStorage` persists the session across browser tab refreshes.
   - [ ] B) To bypass Spring Security.
   - [ ] C) `localStorage` is deleted after 5 seconds.
   - [ ] D) It encrypts the user's name.

---

### Junior Developer Interview Q&A

#### Q: "Walk me through how your full-stack capstone handles an end-to-end user enrollment from button click to database commit."
> **Model Answer:**  
> "When the user clicks the 'Enroll Now' button in our React frontend:
> 1. **Client Action:** The `handleEnroll` handler sets `isEnrolling = true` to prevent double-clicks. It dispatches a `POST /api/v1/enrollments/{courseId}` request using our centralized Axios client.
> 2. **Client Interceptor:** The Axios Request Interceptor automatically extracts the JWT token from `localStorage` and injects `Authorization: Bearer <token>` into the HTTP request headers.
> 3. **Spring Security Filter:** Spring Boot's `JwtAuthenticationFilter` intercepts the request at the Servlet level, cryptographically verifies the token's HMAC-SHA256 signature, extracts the user email, and populates the `SecurityContextHolder`.
> 4. **Controller & Service Layer:** `EnrollmentController` injects `@AuthenticationPrincipal` to get the student's ID and delegates to `EnrollmentService.enrollStudent()`.
> 5. **Transactional Business Logic:** Inside a `@Transactional(rollbackFor = Exception.class)` boundary, the service checks `existsByUserIdAndCourseId` to guarantee idempotency, fetches the course, and saves the new `Enrollment` entity to MySQL.
> 6. **Asynchronous Decoupling:** The service publishes an `OrderPlacedEvent` to RabbitMQ, offloading PDF invoice generation and confirmation email delivery to background worker consumers.
> 7. **Response & UI Update:** The controller returns `201 Created` with the enrollment DTO. React catches the response, displays a success toast notification, and navigates the user to their learning dashboard, where their new course appears with real-time progress tracking."

---

## 8. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Implement robust transactional service logic with duplicate prevention.
- [ ] Eliminate N+1 query cascades in dashboard list queries using `JOIN FETCH`.
- [ ] Build global authentication state in React using Context and Custom Hooks (`useAuth`).
- [ ] Build interactive React components that handle Loading, Success, and Error states.
- [ ] Connect React forms to Spring Boot endpoints via authenticated Axios calls.

👉 **Next Lesson:** **[Topic 14.3: Full-Stack Orchestration: Dockerizing React (Nginx) & Spring Boot](03-docker-deployment.md)**
