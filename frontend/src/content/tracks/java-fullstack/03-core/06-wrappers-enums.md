# Wrapper Classes, Autoboxing Pitfalls & Enums

> **Phase 3: Core Java, Testing Foundations & Modern Java 8+**  
> *Estimated Reading Time: 40 minutes | Total Practice Effort: ~2.5 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In Java, primitive types like `int` and `boolean` store raw data efficiently. However, modern Java APIs—such as Collections (`List`, `Map`), generics, JSON serializers, and database entities—**can only manipulate Objects**, not raw primitives. Writing `List<int>` is illegal in Java!

To bridge this gap, Java provides **Wrapper Classes** that wrap primitives inside heap objects. But automatic conversion (**Autoboxing** and **Unboxing**) introduces subtle traps, including mysterious `NullPointerException` crashes and reference identity bugs.

Furthermore, modeling discrete states (like `PENDING`, `SHIPPED`, `DELIVERED`) using strings or numbers is error-prone. Java **Enums** provide typesafe, expressive state management for production systems.

### Prerequisites
- [Topic 1.2: Variables, Primitive Data Types & Memory Sizes](../01-foundations/02-variables-types.md)
- [Topic 2.1: Classes, Objects & The new Instantiation Lifecycle](../02-oop/01-classes-objects.md)

---

## 2. Plain-English Mental Model

```
[ Raw Primitive: int 42 ]  ---> ( Autoboxing: Put in gift box )  ---> [ Wrapper Object: Integer(42) ]
                                                                                |
                                ( Unboxing: Open gift box )   <-----------------+
                                      |
       (DANGER: What if the gift box is completely EMPTY / NULL? -> CRASH!)
```

- **Autoboxing:** Java automatically places a primitive into an object wrapper box (e.g. `Integer box = 42;`).
- **Unboxing:** Java opens the box to pull the raw primitive number out (e.g. `int x = box;`).
- **Enum (Enumeration):** A finite, fixed set of named constants that prevents invalid states (like `OrderStatus.CONFIRMED` instead of typo-prone `"CONFIRMED"` strings).

---

## 3. The 8 Primitive Wrappers & Autoboxing

| Primitive Type | Wrapper Class | Size in Heap (approx) | Parsing Helper |
| :--- | :--- | :--- | :--- |
| `byte` | `java.lang.Byte` | 16 bytes | `Byte.parseByte("10")` |
| `short` | `java.lang.Short` | 16 bytes | `Short.parseShort("20")` |
| **`int`** | **`java.lang.Integer`** | 16 bytes | `Integer.parseInt("100")` |
| **`long`** | **`java.lang.Long`** | 24 bytes | `Long.parseLong("999999")` |
| `float` | `java.lang.Float` | 16 bytes | `Float.parseFloat("3.14")` |
| **`double`** | **`java.lang.Double`** | 24 bytes | `Double.parseDouble("99.99")`|
| `char` | `java.lang.Character` | 16 bytes | `Character.isDigit('5')` |
| **`boolean`** | **`java.lang.Boolean`** | 16 bytes | `Boolean.parseBoolean("true")`|

---

## 4. The Two Traps of Autoboxing

### Trap 1: The Integer Cache Trap (`==` vs `.equals()`)
To conserve memory, Java caches `Integer` objects for values from **-128 to 127**:

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b); // TRUE (Both point to cached instance!)

Integer c = 200;
Integer d = 200;
System.out.println(c == d); // FALSE! (Above 127, new heap objects are allocated!)
```
> [!IMPORTANT] Production Rule
> **Never compare wrapper objects with `==`.** Always use `.equals()`:  
> `c.equals(d)` is `true` for all numbers with identical values.

### Trap 2: The Hidden `NullPointerException` During Unboxing
```java
// Common database entity scenario:
Integer userScore = null; // Unrated user

// Hidden unboxing happens when assigned to primitive int:
int score = userScore; // CRASH: java.lang.NullPointerException!
```
When Java unboxes `userScore`, it compiles into `userScore.intValue()`. Invoking `.intValue()` on `null` throws an immediate `NullPointerException`.

---

## 5. Typesafe Enums: State Machines with Data & Behavior

Enums in Java are full-fledged classes that can have constructors, fields, and custom methods:

```java
public enum OrderStatus {
    PENDING(100, "Order awaiting payment"),
    PAID(200, "Payment verified"),
    SHIPPED(300, "Package in transit"),
    DELIVERED(400, "Delivered to customer"),
    CANCELLED(900, "Order cancelled");

    private final int statusCode;
    private final String description;

    // Enum constructors are strictly private by definition!
    OrderStatus(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() { return statusCode; }
    public String getDescription() { return description; }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
```

---

## 6. Working Code Example: Order Engine with Enums & Safe Unboxing

```java
// File: OrderStateMachineDemo.java

public class OrderStateMachineDemo {
    public static void main(String[] args) {
        OrderStatus currentStatus = OrderStatus.PAID;

        System.out.println("Status: " + currentStatus.name());
        System.out.println("Code: " + currentStatus.getStatusCode());
        System.out.println("Description: " + currentStatus.getDescription());
        System.out.println("Is Terminal State? " + currentStatus.isTerminal());

        // Safe Unboxing Pattern: Always guard against null before unboxing!
        Integer optionalCustomerDiscount = null; // Came from database nullable column
        
        int finalDiscountPercent = (optionalCustomerDiscount != null) 
                                   ? optionalCustomerDiscount 
                                   : 0; // Default fallback

        System.out.println("\nApplied Discount: " + finalDiscountPercent + "%");

        // Using Enums in modern switch expressions
        String nextAction = switch (currentStatus) {
            case PENDING   -> "Prompt user for payment gateway";
            case PAID      -> "Notify warehouse fulfillment crew";
            case SHIPPED   -> "Send tracking link via SMS";
            case DELIVERED -> "Request product review";
            case CANCELLED -> "Release held inventory lock";
        };
        System.out.println("Next Workflow Action: " + nextAction);
    }
}
```

### Exact Output:
```text
Status: PAID
Code: 200
Description: Payment verified
Is Terminal State? false

Applied Discount: 0%
Next Workflow Action: Notify warehouse fulfillment crew
```

---

## 7. Common Rookie Mistakes & Compiler Traps

### Trap: Declaring Public Enum Constructors
```java
public enum Priority {
    HIGH, LOW;
    public Priority() {} // COMPILER ERROR: modifier public not allowed here
}
```
Enums are fixed singletons initialized exclusively by the JVM. Their constructors are implicitly `private`.

---

## 8. Guided Hands-On Exercise

### Challenge: Currency & Financial Gateway Enum
Create a program named `CurrencyEnumDemo.java`:
1. Define an enum `Currency`:
   - Constants:
     - `USD("US Dollar", "$", 1.0)`
     - `EUR("Euro", "€", 0.92)`
     - `GBP("British Pound", "£", 0.79)`
     - `JPY("Japanese Yen", "¥", 155.0)`
   - Private fields: `currencyName` (`String`), `symbol` (`String`), `rateToUsd` (`double`).
   - Constructor initializing fields.
   - Getters for fields.
   - Method `public double convertTo(double amountInThisCurrency, Currency targetCurrency)`:
     - Converts to USD first (`amount / rateToUsd`), then multiplies by `targetCurrency.getRateToUsd()`.
2. In `main`:
   - Convert `100.0` EUR to USD.
   - Convert `100.0` EUR to JPY.
   - Format and print results cleanly.

<details>
<summary>💡 Hint: Conversion formula</summary>

```java
double usdValue = amountInThisCurrency / this.rateToUsd;
return usdValue * targetCurrency.rateToUsd;
```
</details>

<details>
<summary>✅ Solution</summary>

```java
enum Currency {
    USD("US Dollar", "$", 1.0),
    EUR("Euro", "€", 0.92),
    GBP("British Pound", "£", 0.79),
    JPY("Japanese Yen", "¥", 155.0);

    private final String currencyName;
    private final String symbol;
    private final double rateToUsd;

    Currency(String currencyName, String symbol, double rateToUsd) {
        this.currencyName = currencyName;
        this.symbol = symbol;
        this.rateToUsd = rateToUsd;
    }

    public String getCurrencyName() { return currencyName; }
    public String getSymbol() { return symbol; }
    public double getRateToUsd() { return rateToUsd; }

    public double convertTo(double amount, Currency target) {
        double inUsd = amount / this.rateToUsd;
        return inUsd * target.getRateToUsd();
    }
}

public class CurrencyEnumDemo {
    public static void main(String[] args) {
        Currency fromCurrency = Currency.EUR;

        double eurAmount = 100.0;
        double convertedToUsd = fromCurrency.convertTo(eurAmount, Currency.USD);
        double convertedToJpy = fromCurrency.convertTo(eurAmount, Currency.JPY);

        System.out.println("=== Currency Conversion ===");
        System.out.printf("€%.2f EUR = $%.2f USD%n", eurAmount, convertedToUsd);
        System.out.printf("€%.2f EUR = ¥%.2f JPY%n", eurAmount, convertedToJpy);
    }
}
```

**Terminal Output:**
```text
=== Currency Conversion ===
€100.00 EUR = $108.70 USD
€100.00 EUR = ¥16847.83 JPY
```
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **Why does `Integer x = 127; Integer y = 127; x == y` evaluate to true, but `Integer a = 128; Integer b = 128; a == b` evaluate to false?**
   - [x] A) The JVM caches `Integer` objects in the range `-128` to `127`. For values outside this range, new heap objects are instantiated with different memory addresses.
   - [ ] B) `128` is a prime number.
   - [ ] C) Autoboxing is disabled for numbers larger than 127.
   - [ ] D) A bug in the Java compiler.

2. **What occurs when unboxing a `null` Wrapper object into a primitive (`Integer count = null; int x = count;`)?**
   - [ ] A) `x` is assigned `0`.
   - [x] B) The JVM throws `java.lang.NullPointerException`.
   - [ ] C) A compile-time error is raised.
   - [ ] D) `x` becomes `NaN`.

3. **Can an Enum in Java implement interfaces?**
   - [x] A) Yes, Enums can implement any interface.
   - [ ] B) No, Enums cannot participate in OOP contracts.
   - [ ] C) Only if the interface has no methods.
   - [ ] D) Only in Java 21+.

---

### Junior Developer Interview Q&A

#### Q: "Why should you use Enums instead of public static final integer or string constants for state representations?"
> **Model Answer:**  
> "Using `public static final int` or `String` constants (known as the 'int enum pattern') has three major flaws:
> 1. **No Type Safety:** A method accepting `int status` can be passed any arbitrary number like `999` or `-1`, leading to invalid states.
> 2. **No Namespacing:** Integer constants can collide across domains.
> 3. **Brittle & Cryptic:** Printing an integer prints a raw number (`2`) rather than an expressive name (`PAID`).
> 
> Java Enums provide compile-time **type safety** (a method taking `OrderStatus` rejects all other types), support **rich behavior** (fields, constructors, and methods), and integrate seamlessly with `switch` statements and specialized high-speed collections like `EnumSet` and `EnumMap`."

---

## 10. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Understand why Collections require Wrapper classes instead of raw primitives.
- [ ] Guard against unboxing `NullPointerException`s when reading nullable database fields.
- [ ] Explain the Integer Cache range (`-128` to `127`) and always use `.equals()`.
- [ ] Construct rich Java Enums with custom fields, methods, and switch expressions.

👉 **Next Lesson:** **[Topic 3.7: Generics: Type Parameters, Generic Classes & Type Erasure](07-generics.md)**
