# Race Conditions, Critical Sections & The synchronized Monitor

> **Phase 6: Multithreading & Concurrent Programming**  
> *Estimated Reading Time: 50 minutes | Total Practice Effort: ~3.5 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In Topic 6.1, you learned that all threads in a process share the same Java Heap.
Consider a banking service where Account #101 holds $500. Two concurrent requests arrive at the exact same millisecond:
- Request A: Withdraw $400.
- Request B: Withdraw $300.

If both threads inspect the balance at the same time, both see $500. Both approve the withdrawal, and the account balance ends up corrupted at -$200! This is a **Race Condition**—the most notorious category of concurrency bugs.

To build secure, reliable backends, you must identify **Critical Sections** and enforce **Mutual Exclusion** using Java's intrinsic **`synchronized`** monitor locks.

### Prerequisites
- [Topic 5.2: JVM Memory Architecture: Stack Frames, Heap & Metaspace](../05-jvm/02-memory-model.md)
- [Topic 6.1: Threads, The Thread Class & Runnable Lifecycle](01-thread-basics.md)

---

## 2. Plain-English Mental Model

Think of an object's intrinsic lock like a single-occupancy airplane restroom:

```
[ Airplane Restroom Door: Intrinsic Monitor Lock (mutex) ]
                      |
           ( Passenger Alice arrives )
                      |
                      v
[ Alice turns the lock to OCCUPIED ]
- Alice enters the restroom (The Critical Section).
- Passenger Bob arrives at the door.
- Bob sees "OCCUPIED" -> Bob waits patiently in the aisle (BLOCKED state).
                      |
                      v
[ Alice finishes and unlocks the door ]
- Alice leaves.
- Bob is automatically notified, acquires the lock, and enters.
```

Only **one thread** can hold the monitor lock at any given time. Any other thread attempting to execute that code is placed into the `BLOCKED` state until the lock is released.

---

## 3. Deconstructing a Race Condition: Why `count++` is NOT Atomic

Beginners often assume `count++` is a single atomic instruction. In compiled bytecode, `count++` is actually **three separate CPU steps**:

```mermaid
flowchart LR
    S1["Step 1: Read<br/>Read count from Heap into CPU register (e.g. 10)"] 
    --> S2["Step 2: Modify<br/>Add 1 inside CPU register (10 + 1 = 11)"]
    --> S3["Step 3: Write<br/>Write 11 back to Heap memory"]
```

### The Disaster of Thread Interleaving:
1. Thread A reads `count` (10).
2. *Context Switch!* The OS CPU scheduler pauses Thread A and switches to Thread B.
3. Thread B reads `count` (10), increments to `11`, and writes `11` back to Heap.
4. *Context Switch!* Thread A resumes with its stale register value (`10`), increments to `11`, and writes `11` back to Heap!
5. **Result:** Two increments executed, but `count` only increased by 1! This is a **Lost Update Bug**.

---

## 4. The `synchronized` Keyword: Methods vs Blocks

Java allows locking via the `synchronized` keyword in two ways:

### 1. Synchronized Methods (Coarse-Grained Locking)
- **Instance Method:** Locks on the instance itself (`this`):
  ```java
  public synchronized void deposit(double amount) {
      this.balance += amount;
  }
  ```
- **Static Method:** Locks on the Class mirror object in Metaspace (`ClassName.class`):
  ```java
  public static synchronized void incrementGlobalCounter() {
      totalUsers++;
  }
  ```

### 2. Synchronized Blocks (Fine-Grained Locking - Preferred!)
Instead of locking the entire method, lock **only the specific lines** that mutate shared state. This minimizes lock holding time and maximizes application throughput:

```java
public class OrderService {
    private final Object balanceLock = new Object(); // Dedicated lock object
    private double balance = 1000.0;

    public void withdraw(double amount) {
        // Step 1: External I/O outside synchronized block (Zero contention!)
        auditLogger.log("Withdrawal requested");

        // Step 2: Critical Section protected by lock
        synchronized (balanceLock) {
            if (balance >= amount) {
                balance -= amount;
            }
        }

        // Step 3: Unlocked notification
        sendEmailReceipt();
    }
}
```

---

## 5. Working Code Example: Proving and Fixing a Race Condition

```java
// File: RaceConditionProofDemo.java

class UnsafeCounter {
    public int count = 0;

    public void increment() {
        count++; // Non-atomic: 3 bytecode steps!
    }
}

class SafeCounter {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) { // Guarantees Mutual Exclusion
            count++;
        }
    }

    public int getCount() { return count; }
}

public class RaceConditionProofDemo {
    public static void main(String[] args) throws InterruptedException {
        int iterations = 10_000;

        // 1. Testing Unsafe Counter
        UnsafeCounter unsafe = new UnsafeCounter();
        Thread t1 = new Thread(() -> { for (int i = 0; i < iterations; i++) unsafe.increment(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < iterations; i++) unsafe.increment(); });

        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("Expected count: " + (iterations * 2));
        System.out.println("UNSAFE Count (Lost Updates!): " + unsafe.count);

        // 2. Testing Safe Counter
        SafeCounter safe = new SafeCounter();
        Thread t3 = new Thread(() -> { for (int i = 0; i < iterations; i++) safe.increment(); });
        Thread t4 = new Thread(() -> { for (int i = 0; i < iterations; i++) safe.increment(); });

        t3.start(); t4.start();
        t3.join();  t4.join();

        System.out.println("SAFE Count (Synchronized):    " + safe.getCount());
    }
}
```

### Exact Output:
```text
Expected count: 20000
UNSAFE Count (Lost Updates!): 13421
SAFE Count (Synchronized):    20000
```
*Notice how the unsafe counter lost over 6,500 increments due to thread interleaving, while the synchronized counter is 100% accurate!*

---

## 6. Common Rookie Mistakes & Synchronization Traps

### Trap 1: Locking on a New Object Every Time
```java
public void process() {
    synchronized (new Object()) { // DISASTER: Creates a new lock every time!
        // Every thread locks on its OWN independent lock -> ZERO mutual exclusion!
    }
}
```
> [!CAUTION]
> Always lock on a `private final Object lock = new Object();` field that is shared across invocations.

### Trap 2: Locking on a String Literal
```java
synchronized ("SHARED_LOCK") { ... } // ANTI-PATTERN!
```
Because String literals live in the String Constant Pool, any other class or third-party library in your entire JVM that locks on `"SHARED_LOCK"` will inadvertently block your threads!

### Trap 3: Locking on an Autoboxed Wrapper Object
```java
private Integer counter = 0;

public void increment() {
    synchronized (counter) {
        counter++; // Reassigns 'counter' to a brand-new Integer object on the Heap!
    }
}
```
The lock reference changes on every iteration, destroying thread safety!

---

## 7. Guided Hands-On Exercise

### Challenge: Thread-Safe Bank Account Ledger
Create a program named `BankLedgerDemo.java`:
1. Class `SecureAccount`:
   - Private field: `double balance = 500.0;`
   - Private final lock: `private final Object lock = new Object();`
   - Method `public boolean withdraw(double amount)`:
     - Protect the balance check and deduction inside a `synchronized(lock)` block.
     - If balance is `>= amount`, deduct and return `true`.
     - Otherwise return `false`.
2. In `main`:
   - Spawn Thread A attempting to withdraw `$400.0`.
   - Spawn Thread B attempting to withdraw `$300.0`.
   - Launch both threads simultaneously.
   - Assert that exactly one withdrawal succeeds and one is declined, preventing the account from going negative.

<details>
<summary>💡 Hint: Synchronized withdrawal</summary>

```java
synchronized (lock) {
    if (balance >= amount) {
        balance -= amount;
        return true;
    }
    return false;
}
```
</details>

<details>
<summary>✅ Solution</summary>

```java
class SecureAccount {
    private double balance = 500.0;
    private final Object lock = new Object();

    public boolean withdraw(String threadName, double amount) {
        synchronized (lock) {
            System.out.println(threadName + " checking balance: $" + balance);
            if (balance >= amount) {
                try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate processing
                balance -= amount;
                System.out.println(" -> " + threadName + " successfully withdrew $" + amount + ". New balance: $" + balance);
                return true;
            } else {
                System.out.println(" -> " + threadName + " DECLINED: Insufficient balance ($" + balance + ") for $" + amount);
                return false;
            }
        }
    }

    public double getBalance() {
        synchronized (lock) {
            return balance;
        }
    }
}

public class BankLedgerDemo {
    public static void main(String[] args) throws InterruptedException {
        SecureAccount account = new SecureAccount();

        Thread customerA = new Thread(() -> account.withdraw("Customer-Alice", 400.0), "Alice-Thread");
        Thread customerB = new Thread(() -> account.withdraw("Customer-Bob",   300.0), "Bob-Thread");

        customerA.start();
        customerB.start();

        customerA.join();
        customerB.join();

        System.out.println("Final Verified Account Balance: $" + account.getBalance());
    }
}
```

**Terminal Output (Representative):**
```text
Customer-Alice checking balance: $500.0
 -> Customer-Alice successfully withdrew $400.0. New balance: $100.0
Customer-Bob checking balance: $100.0
 -> Customer-Bob DECLINED: Insufficient balance ($100.0) for $300.0
Final Verified Account Balance: $100.0
```
</details>

---

## 8. Knowledge Check & Interview Defense

### Quick Quiz
1. **What is a Race Condition in concurrent programming?**
   - [x] A) A situation where multiple threads read and write shared mutable state simultaneously, causing final results to depend unpredictably on thread scheduling order.
   - [ ] B) When one thread executes faster than another.
   - [ ] C) An exception thrown when arrays are full.
   - [ ] D) When CPU clock speeds are mismatched.

2. **What does an instance method marked `public synchronized void update()` lock on?**
   - [ ] A) The current thread.
   - [x] B) The `this` instance reference of the calling object.
   - [ ] C) The `java.lang.Object.class`.
   - [ ] D) The operating system kernel.

3. **Why should developers declare lock objects as `private final Object lock = new Object();`?**
   - [x] A) `private` prevents external classes from tampering with the lock; `final` guarantees the lock memory address cannot be reassigned.
   - [ ] B) `final` objects run 10x faster.
   - [ ] C) Java requires all locks to be Object subclasses.
   - [ ] D) To prevent serialization.

---

### Junior Developer Interview Q&A

#### Q: "What is an Intrinsic Lock (Monitor) in Java, and is it Reentrant?"
> **Model Answer:**  
> "In Java, every single object in the Heap has an **Intrinsic Lock (also called a Monitor)** embedded inside its object header (the 'Mark Word'). When a thread enters a `synchronized` block or method, it must acquire that object's monitor. Any other thread attempting to acquire the same monitor is suspended in the `BLOCKED` state.  
> 
> Java's intrinsic locks are **Reentrant**. This means if a thread already holds the lock on Object A, it can freely enter other `synchronized` blocks that require the exact same lock on Object A without deadlocking itself. The JVM maintains an internal lock acquisition counter: every re-entry increments the counter, and exiting decrements it. The lock is only released when the counter returns to zero."

---

## 9. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Deconstruct why `count++` is not atomic and causes race conditions.
- [ ] Enforce Mutual Exclusion using `synchronized` methods and synchronized blocks.
- [ ] Choose fine-grained lock blocks over coarse-grained method locks to optimize throughput.
- [ ] Avoid dangerous synchronization traps (String literals, new objects, autoboxed wrappers).

👉 **Next Lesson:** **[Topic 6.3: Memory Visibility, volatile & Atomic Primitives (AtomicInteger)](03-visibility-atomics.md)**
