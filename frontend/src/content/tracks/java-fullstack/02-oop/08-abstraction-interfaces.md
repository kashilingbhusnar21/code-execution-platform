# Abstract Classes vs Interfaces & Multiple Inheritance of Type

> **Phase 2: OOP & Class Design**  
> *Estimated Reading Time: 55 minutes | Total Practice Effort: ~4.0 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In large software systems, high-level business logic should not depend on low-level implementation details.  
- A checkout service shouldn't care whether data is saved to PostgreSQL, MongoDB, or an in-memory cache; it only cares that a `PaymentRepository` contract exists with a `.save()` method.
- A PDF export controller shouldn't care what type of document it is printing; it only cares that the object implements an `Exportable` interface.

**Abstraction** separates *what* an object does from *how* it achieves it. Mastering the distinctions between **Abstract Classes** and **Interfaces**—including modern Java 8+ `default` methods and multiple inheritance of type—is the key to writing modular, loosely coupled, and highly maintainable software.

### Prerequisites
- [Topic 2.7: Polymorphism: Compile-Time vs Dynamic Method Dispatch](07-polymorphism.md)

---

## 2. Plain-English Mental Model

Think of an Abstract Class and an Interface like a house builder and a standard electrical socket:

```
[ Abstract Class: BaseResidence ]                [ Interface: ElectricDevice ]
- Has concrete foundation & plumbing (Shared state) - Pure specification: Must fit 2-prong plug!
- Has abstract spec: layoutFloorplan()              - Any manufacturer can implement it!
                 |                                                   |
                 v                                                   v
   [ Concrete Class: ModernVilla ]                   [ Implemented by: Lamp, Laptop, Blender ]
```

- **Abstract Class:** Represents an unfinished identity (**IS-A** relationship). It provides shared structural foundation (fields, constructors, partial methods), but leaves specific details for child classes to finish.
- **Interface:** Represents a capability or contract (**CAN-DO** relationship). Any class, regardless of where it lives in an inheritance tree, can implement an interface by agreeing to fulfill its method contracts.

---

## 3. Abstract Classes in Detail

An abstract class is declared using the `abstract` keyword:
1. **Cannot be instantiated directly:** Writing `new AbstractClass()` causes a compile-time error.
2. **May contain abstract methods:** Methods with no body (`public abstract void execute();`) that subclasses **must** implement.
3. **Can contain state & constructors:** Unlike interfaces, an abstract class can declare private instance fields, constructors, and fully implemented concrete methods.

```java
public abstract class DatabaseConnector {
    protected String connectionUrl; // Shared state

    public DatabaseConnector(String connectionUrl) {
        this.connectionUrl = connectionUrl; // Constructor for subclasses
    }

    // Concrete method shared by all connectors
    public void logConnection() {
        System.out.println("Connecting to: " + connectionUrl);
    }

    // Abstract method: Child classes must implement their own driver logic
    public abstract void openConnection();
}
```

---

## 4. Interfaces & Multiple Inheritance of Type

An interface is a contract. A class uses `implements` to promise that it provides the required behaviors:

```java
public interface Auditable {
    void recordAuditLog(String action);
}

public interface Encryptable {
    byte[] encryptData(byte[] payload);
}

// MULTIPLE INHERITANCE OF TYPE: A class can implement as many interfaces as needed!
public class SecureFinancialLedger implements Auditable, Encryptable {
    @Override
    public void recordAuditLog(String action) { ... }

    @Override
    public byte[] encryptData(byte[] payload) { ... }
}
```

### Modern Interface Features (Java 8+ and 9+)
Historically, interfaces could only contain abstract methods. Modern Java enhances interfaces with:
1. **`default` methods (Java 8+):** Allows adding new methods to interfaces with a default implementation without breaking existing implementing classes.
2. **`static` methods (Java 8+):** Utility methods belonging directly to the interface (e.g. `Comparator.naturalOrder()`).
3. **`private` methods (Java 9+):** Helper methods used internally by default methods to avoid code duplication.

---

## 5. Architectural Comparison: Abstract Class vs Interface

| Dimension | Abstract Class | Interface |
| :--- | :--- | :--- |
| **Relationship** | **IS-A** (Identity: closely related hierarchy) | **CAN-DO** (Capability: unrelated classes can share) |
| **Multiple Inheritance**| ❌ Single inheritance only (`extends OneClass`) | ✅ Multiple inheritance of type (`implements A, B, C`) |
| **Instance Fields** | ✅ Can have `private`, `protected`, mutable fields | ❌ Only `public static final` constants |
| **Constructors** | ✅ Yes (invoked via `super(...)`) | ❌ No constructors allowed |
| **Access Modifiers** | Can have `private`, `protected`, `public` methods | Methods are `public` by default |
| **Speed / vtable** | Uses fast standard vtable lookup | Uses `invokeinterface` (slightly different lookup table) |

---

## 6. Working Code Example: Cloud Storage & Multi-Interface System

```java
// File: StorageSystemDemo.java

// 1. Behavioral Interfaces (Capabilities)
interface Archivable {
    void archive(String retentionPolicy);
}

interface Encryptable {
    String getEncryptionAlgorithm();

    // Java 8+ Default Method
    default void logEncryptionStatus() {
        System.out.println("Security Notice: Assets protected using " + getEncryptionAlgorithm());
    }
}

// 2. Abstract Base Class (Shared Structural State & Foundation)
abstract class CloudStorageEngine {
    private String bucketName;
    private long storageQuotaBytes;

    public CloudStorageEngine(String bucketName, long storageQuotaBytes) {
        this.bucketName = bucketName;
        this.storageQuotaBytes = storageQuotaBytes;
    }

    public String getBucketName() { return bucketName; }
    public long getStorageQuotaBytes() { return storageQuotaBytes; }

    // Concrete shared logic
    public void printCapacity() {
        System.out.println("Bucket [" + bucketName + "] Quota: " + (storageQuotaBytes / 1_000_000) + " MB");
    }

    // Abstract contract: How bytes are stored depends on cloud provider
    public abstract void uploadFile(String fileName, byte[] data);
}

// 3. Concrete Implementation: Extends 1 Class, Implements 2 Interfaces!
class AwsS3StorageEngine extends CloudStorageEngine implements Archivable, Encryptable {
    private String awsRegion;

    public AwsS3StorageEngine(String bucketName, long quota, String awsRegion) {
        super(bucketName, quota);
        this.awsRegion = awsRegion;
    }

    @Override
    public void uploadFile(String fileName, byte[] data) {
        System.out.println("[AWS S3 " + awsRegion + "] Uploaded '" + fileName 
                           + "' (" + data.length + " bytes) to bucket: " + getBucketName());
    }

    @Override
    public void archive(String retentionPolicy) {
        System.out.println("[AWS Glacier] Applied retention policy: " + retentionPolicy);
    }

    @Override
    public String getEncryptionAlgorithm() {
        return "AES-256-GCM (KMS Managed)";
    }
}

public class StorageSystemDemo {
    public static void main(String[] args) {
        AwsS3StorageEngine s3 = new AwsS3StorageEngine("production-media-store", 500_000_000, "us-east-1");

        s3.printCapacity();
        s3.logEncryptionStatus(); // Invoking default method from Encryptable interface

        byte[] samplePayload = "Sample image binary data".getBytes();
        s3.uploadFile("banner.png", samplePayload);
        s3.archive("7-YEAR-COMPLIANCE-LOCK");
    }
}
```

### Exact Output:
```text
Bucket [production-media-store] Quota: 500 MB
Security Notice: Assets protected using AES-256-GCM (KMS Managed)
[AWS S3 us-east-1] Uploaded 'banner.png' (24 bytes) to bucket: production-media-store
[AWS Glacier] Applied retention policy: 7-YEAR-COMPLIANCE-LOCK
```

---

## 7. The Default Method Diamond Conflict & Resolution

What happens when two interfaces define the exact same `default` method signature, and a class implements both?

```java
interface InterfaceA {
    default void ping() { System.out.println("Ping from A"); }
}

interface InterfaceB {
    default void ping() { System.out.println("Ping from B"); }
}

// COMPILER ERROR: Duplicate default methods named ping with parameters ()
class Service implements InterfaceA, InterfaceB {
    // The compiler FORCES the developer to override and resolve the conflict!
    @Override
    public void ping() {
        InterfaceA.super.ping(); // Explicitly choose A's version, or write custom logic!
    }
}
```

---

## 8. Guided Hands-On Exercise

### Challenge: Report Exporter & Data Formatter
Create a program named `ReportExporterDemo.java`:
1. Interface `ReportExporter`:
   - Abstract method: `String export(String reportTitle, String[] rows)`.
   - Default method: `void printSuccessBanner()` that prints:  
     `"SUCCESS: Report export completed successfully."`
2. Subclasses implementing `ReportExporter`:
   - `JsonReportExporter`: Formats rows into a simulated JSON array:
     ```text
     { "report": "title", "data": ["row1", "row2"] }
     ```
   - `CsvReportExporter`: Formats rows into comma-separated values:
     ```text
     --- title ---
     row1,row2
     ```
3. In `main`:
   - Declare an interface reference `ReportExporter exporter;`.
   - Test both exporters with title `"Quarterly Revenue"` and rows `{"Jan: $10k", "Feb: $12k", "Mar: $15k"}`.
   - Print the generated strings and call `printSuccessBanner()`.

<details>
<summary>💡 Hint: JSON formatting</summary>

```java
return "{ \"report\": \"" + reportTitle + "\", \"data\": " + Arrays.toString(rows) + " }";
```
</details>

<details>
<summary>✅ Solution</summary>

```java
import java.util.Arrays;

interface ReportExporter {
    String export(String reportTitle, String[] rows);

    default void printSuccessBanner() {
        System.out.println("SUCCESS: Report export completed successfully.\n");
    }
}

class JsonReportExporter implements ReportExporter {
    @Override
    public String export(String reportTitle, String[] rows) {
        return "{\n  \"report\": \"" + reportTitle + "\",\n  \"data\": " 
               + Arrays.toString(rows) + "\n}";
    }
}

class CsvReportExporter implements ReportExporter {
    @Override
    public String export(String reportTitle, String[] rows) {
        return "--- " + reportTitle.toUpperCase() + " ---\n" + String.join(",", rows);
    }
}

public class ReportExporterDemo {
    public static void main(String[] args) {
        String[] dataRows = {"Jan: $10k", "Feb: $12k", "Mar: $15k"};
        String title = "Quarterly Revenue";

        // Polymorphic Interface Reference
        ReportExporter exporter;

        // 1. JSON Export
        exporter = new JsonReportExporter();
        System.out.println(exporter.export(title, dataRows));
        exporter.printSuccessBanner();

        // 2. CSV Export
        exporter = new CsvReportExporter();
        System.out.println(exporter.export(title, dataRows));
        exporter.printSuccessBanner();
    }
}
```

**Terminal Output:**
```text
{
  \"report\": \"Quarterly Revenue\",
  \"data\": [Jan: $10k, Feb: $12k, Mar: $15k]
}
SUCCESS: Report export completed successfully.

--- QUARTERLY REVENUE ---
Jan: $10k,Feb: $12k,Mar: $15k
SUCCESS: Report export completed successfully.
```
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **Can an abstract class define a constructor?**
   - [x] A) Yes. While it cannot be instantiated directly with `new`, its constructor is called by subclass constructors via `super(...)` to initialize inherited state.
   - [ ] B) No. Abstract classes are forbidden from having constructors.
   - [ ] C) Yes, but only if all methods in the class are abstract.
   - [ ] D) Only in Java 21+.

2. **Why were `default` methods introduced to Java interfaces in Java 8?**
   - [ ] A) To replace abstract classes completely.
   - [x] B) To allow developers to add new methods to existing library interfaces (like `Collection.stream()`) without breaking existing classes that implemented those interfaces.
   - [ ] C) To make interface methods run faster.
   - [ ] D) To eliminate the need for classes.

3. **Can a Java class implement multiple interfaces?**
   - [x] A) Yes, Java supports multiple inheritance of type through interfaces (`implements A, B, C`).
   - [ ] B) No, a class can only implement one interface.
   - [ ] C) Only if the interfaces have no methods.
   - [ ] D) Only in Spring Boot applications.

---

### Junior Developer Interview Q&A

#### Q: "When should you choose an Abstract Class over an Interface in system design?"
> **Model Answer:**  
> "Choose an **Abstract Class** when:
> 1. You are modeling closely related classes in a strict **IS-A** hierarchy.
> 2. You need to provide shared, non-static, mutable state (instance variables) or constructors that initialize common state.
> 3. You want to provide a default template algorithm with certain steps delegated to subclasses (the **Template Method Design Pattern**).
> 
> Choose an **Interface** when:
> 1. You are defining a peripheral capability or behavioral contract (**CAN-DO**) that can be implemented by completely unrelated classes across different parts of the system (e.g. `Comparable`, `AutoCloseable`, `Serializable`).
> 2. You want to leverage multiple inheritance of type to combine diverse capabilities into a single service.
> 3. You are designing decoupled boundaries in a Spring Boot application where services depend strictly on contracts rather than concrete implementations."

---

## 10. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Define abstract classes with abstract methods and subclass constructors.
- [ ] Implement multiple interfaces on a single class.
- [ ] Write and invoke `default` and `static` methods inside interfaces.
- [ ] Articulate when to choose an abstract class vs an interface in architectural interviews.

👉 **Next Lesson:** **[Topic 2.9: Composition (HAS-A) vs Inheritance (IS-A) & Practical SOLID](09-solid-principles.md)**
