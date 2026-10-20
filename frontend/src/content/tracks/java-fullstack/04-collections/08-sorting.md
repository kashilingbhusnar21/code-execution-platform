# Sorting Objects: Comparable vs Custom Comparator

> **Phase 4: Data Structures & Java Collections Framework**  
> *Estimated Reading Time: 45 minutes | Total Practice Effort: ~3.0 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In any web application, users demand sorted data:
- Sort products on an e-commerce page by: *Price: Low to High*, *Highest Rated*, or *Newest Arrival*.
- Sort banking transactions chronologically or by transaction size.
- Sort employee directories alphabetically by last name, then first name.

Java provides two interfaces to control how objects are ordered: **`Comparable<T>`** (defining a class's internal *natural order*) and **`Comparator<T>`** (defining external, flexible *custom sorting strategies*). Mastering these interfaces allows you to build multi-column sorting pipelines cleanly and concisely.

### Prerequisites
- [Topic 3.8: The Object Contract: equals(), hashCode() & Identity vs Equality](../03-core/08-equals-hashcode.md)
- [Topic 3.9: Modern Java 8: Functional Interfaces & Lambda Syntax](../03-core/09-lambdas.md)

---

## 2. Plain-English Mental Model

```
[ Comparable: Internal Natural Order ]               [ Comparator: The External Referee ]
"An athlete measuring their own height"               "A judge scoring a gymnastics competition"
- Defined INSIDE the class itself                     - Defined OUTSIDE in an independent lambda/class
- Single method: compareTo(other)                     - Single method: compare(o1, o2)
- "What is my ONE default, obvious order?"            - "Can sort by 10 different criteria on demand!"
```

---

## 3. Architectural Comparison: `Comparable` vs `Comparator`

| Dimension | `Comparable<T>` | `Comparator<T>` |
| :--- | :--- | :--- |
| **Package** | `java.lang` | `java.util` |
| **Method Signature** | `int compareTo(T o)` | `int compare(T o1, T o2)` |
| **Location** | Implemented **inside** the target class | Created as a **separate** lambda, method reference, or class |
| **Number of Orders**| Exactly **one** natural order | **Unlimited** custom orders (by price, date, rating, etc.) |
| **Sorting Invocations**| `Collections.sort(list)` | `list.sort(comparator)` or `Collections.sort(list, comp)` |

---

## 4. The `compareTo` Three-Way Integer Contract

Whether using `compareTo()` or `compare()`, the return integer communicates relative order:

$$\text{Result} = 
\begin{cases} 
\text{Negative } (< 0) & \text{if } \text{this} < \text{other} \quad (\text{this comes FIRST}) \\
\text{Zero } (== 0) & \text{if } \text{this} == \text{other} \quad (\text{equal rank}) \\
\text{Positive } (> 0) & \text{if } \text{this} > \text{other} \quad (\text{this comes AFTER})
\end{cases}$$

> [!CAUTION] The Integer Subtraction Overflow Trap!
> Beginners often write:
> ```java
> public int compareTo(Item other) {
>     return this.id - other.id; // DANGEROUS BUG!
> }
> ```
> If `this.id` is positive and `other.id` is negative (or large), integer subtraction can **overflow**, producing reversed results!  
> **Always use typesafe helper methods:**
> ```java
> return Integer.compare(this.id, other.id); // Completely safe!
> return Double.compare(this.price, other.price);
> ```

---

## 5. Modern Java 8 Fluent `Comparator` Chaining

Modern Java provides fluent builder methods on the `Comparator` interface that eliminate all manual `if-else` comparison code:

```java
// Fluent multi-level sorting:
Comparator<Product> productSorter = Comparator
    .comparing(Product::getCategory)                    // 1. Primary: Category A-Z
    .thenComparing(Product::getRating).reversed()       // 2. Secondary: Highest rating first
    .thenComparingDouble(Product::getPrice);            // 3. Tertiary: Lowest price first

// Sorting in place:
products.sort(productSorter);
```

---

## 6. Under the Hood: Timsort

When you invoke `list.sort(comparator)` or `Arrays.sort(objects)`, what algorithm runs on your CPU?  
Java uses **Timsort** (invented by Tim Peters for Python, adopted by Java 7+):

- **Hybrid Algorithm:** Merges the stability of **Merge Sort** with the low-overhead speed of **Insertion Sort**.
- **Real-World Optimization:** Real-world datasets often contain pre-existing ordered sequences called "runs". Timsort detects these natural runs and merges them with minimal operations.
- **Complexity:**
  - Worst-Case: **$O(N \log N)$**
  - Best-Case (already sorted data): **$O(N)$** linear time!
  - Memory: Requires $O(N)$ temporary memory buffer.
- **Guaranteed Stability:** Timsort is a **stable sort**: if two items have equal keys, their original relative order is preserved.

---

## 7. Working Code Example: Multi-Criteria Product Catalog Sorter

```java
// File: SortingShowcaseDemo.java
import java.util.*;

class CatalogProduct implements Comparable<CatalogProduct> {
    private final String sku;
    private final String category;
    private final double price;
    private final double customerRating;

    public CatalogProduct(String sku, String category, double price, double customerRating) {
        this.sku = sku;
        this.category = category;
        this.price = price;
        this.customerRating = customerRating;
    }

    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public double getCustomerRating() { return customerRating; }

    // 1. Natural Ordering: Alphabetical by SKU code
    @Override
    public int compareTo(CatalogProduct other) {
        return this.sku.compareTo(other.sku);
    }

    @Override
    public String toString() {
        return String.format("[%s | %-11s | $%-6.2f | Rating: %.1f*]", 
                             sku, category, price, customerRating);
    }
}

public class SortingShowcaseDemo {
    public static void main(String[] args) {
        List<CatalogProduct> catalog = new ArrayList<>(List.of(
            new CatalogProduct("PROD-C", "ELECTRONICS", 99.00, 4.8),
            new CatalogProduct("PROD-A", "BOOKS",       19.99, 4.5),
            new CatalogProduct("PROD-B", "ELECTRONICS", 49.00, 4.2),
            new CatalogProduct("PROD-D", "BOOKS",       14.99, 4.8)
        ));

        // 1. Natural Sort via Comparable (by SKU)
        Collections.sort(catalog);
        System.out.println("=== 1. Natural Sort (by SKU) ===");
        catalog.forEach(System.out::println);

        // 2. Custom Sort via Comparator: Category ASC, then Price ASC
        catalog.sort(Comparator
            .comparing(CatalogProduct::getCategory)
            .thenComparingDouble(CatalogProduct::getPrice)
        );

        System.out.println("\n=== 2. Multi-Criteria Sort (Category -> Price) ===");
        catalog.forEach(System.out::println);
    }
}
```

### Exact Output:
```text
=== 1. Natural Sort (by SKU) ===
[PROD-A | BOOKS       | $19.99  | Rating: 4.5*]
[PROD-B | ELECTRONICS | $49.00  | Rating: 4.2*]
[PROD-C | ELECTRONICS | $99.00  | Rating: 4.8*]
[PROD-D | BOOKS       | $14.99  | Rating: 4.8*]

=== 2. Multi-Criteria Sort (Category -> Price) ===
[PROD-D | BOOKS       | $14.99  | Rating: 4.8*]
[PROD-A | BOOKS       | $19.99  | Rating: 4.5*]
[PROD-B | ELECTRONICS | $49.00  | Rating: 4.2*]
[PROD-C | ELECTRONICS | $99.00  | Rating: 4.8*]
```

---

## 8. Guided Hands-On Exercise

### Challenge: Leaderboard Scoreboard with Null-Safe Sorting
Create a program named `ScoreboardDemo.java`:
1. Class `PlayerScore`:
   - Fields: `username` (`String`), `score` (`int`), `trophyRank` (`String` - nullable!).
2. Sort a list of players using `Comparator`:
   - Primary: Highest score first (`score` descending).
   - Secondary: `trophyRank` ascending, with `null` values placed at the end (`Comparator.nullsLast(Comparator.naturalOrder())`).
3. In `main`:
   - Test with:
     - `("Alex", 1500, "GOLD")`
     - `("Bob", 2000, null)`
     - `("Charlie", 1500, "BRONZE")`
     - `("David", 1500, null)`
   - Print sorted scoreboard.

<details>
<summary>💡 Hint: Chaining with nullsLast</summary>

```java
Comparator<PlayerScore> comp = Comparator
    .comparingInt(PlayerScore::getScore).reversed()
    .thenComparing(PlayerScore::getTrophyRank, Comparator.nullsLast(Comparator.naturalOrder()));
```
</details>

<details>
<summary>✅ Solution</summary>

```java
import java.util.*;

class PlayerScore {
    private final String username;
    private final int score;
    private final String trophyRank;

    public PlayerScore(String username, int score, String trophyRank) {
        this.username = username;
        this.score = score;
        this.trophyRank = trophyRank;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public String getTrophyRank() { return trophyRank; }

    @Override
    public String toString() {
        return String.format("%-10s | Score: %-5d | Trophy: %s", username, score, trophyRank);
    }
}

public class ScoreboardDemo {
    public static void main(String[] args) {
        List<PlayerScore> leaderboard = new ArrayList<>(List.of(
            new PlayerScore("Alex", 1500, "GOLD"),
            new PlayerScore("Bob", 2000, null),
            new PlayerScore("Charlie", 1500, "BRONZE"),
            new PlayerScore("David", 1500, null)
        ));

        leaderboard.sort(Comparator
            .comparingInt(PlayerScore::getScore).reversed()
            .thenComparing(PlayerScore::getTrophyRank, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        System.out.println("=== Leaderboard Rankings ===");
        leaderboard.forEach(System.out::println);
    }
}
```

**Terminal Output:**
```text
=== Leaderboard Rankings ===
Bob        | Score: 2000  | Trophy: null
Charlie    | Score: 1500  | Trophy: BRONZE
Alex       | Score: 1500  | Trophy: GOLD
David      | Score: 1500  | Trophy: null
```
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **When should you implement `Comparable` versus passing a `Comparator`?**
   - [x] A) Implement `Comparable` when a class has a single, obvious natural ordering; use `Comparator` when you need flexible, external, or multi-criteria sorting without modifying the class.
   - [ ] B) `Comparable` is only used for numbers; `Comparator` is for strings.
   - [ ] C) `Comparator` is deprecated in Java 17.
   - [ ] D) There is no difference.

2. **Why is `return a.id - b.id;` considered an anti-pattern in `Comparator` implementations?**
   - [x] A) Integer subtraction can cause arithmetic overflow if values are negative or exceed half the integer range, returning completely incorrect signs.
   - [ ] B) Subtraction runs 100x slower than division.
   - [ ] C) It does not work on even numbers.
   - [ ] D) The compiler rejects subtraction in comparators.

3. **What is the sorting algorithm used by Java's `Collections.sort()` on object lists?**
   - [ ] A) Bubble Sort
   - [x] B) Timsort (a hybrid of Merge Sort and Insertion Sort)
   - [ ] C) Standard QuickSort
   - [ ] D) Radix Sort

---

### Junior Developer Interview Q&A

#### Q: "What is a 'Stable Sort', and is Java's Timsort stable?"
> **Model Answer:**  
> "A **Stable Sort** is a sorting algorithm that preserves the original relative order of elements that have equal sorting keys.  
> 
> For example, if an existing list has two products with the exact same price ($49.99), sorting by price with a stable sort guarantees that whichever product was listed first before the sort will remain first after the sort.  
> 
> Yes, **Java's Timsort (used for `List.sort()` and `Collections.sort()`) is strictly stable**. This is essential in multi-column sorting where a user sorts by 'Category' first, and then sorts by 'Price'—the previous category groupings are preserved among items with identical prices."

---

## 10. Phase 4 Mastery Checklist & Next Phase

Congratulations! You have completed **Phase 4: Data Structures & Java Collections Framework**.

### Phase 4 Mastery Self-Audit:
- [x] Hierarchy & Big-O: Master Collection vs Map, and analyze time complexities ($O(1)$, $O(\log N)$, $O(N)$).
- [x] ArrayList: Deconstruct 1.5x capacity growth and CPU hardware cache locality.
- [x] LinkedList Realities: Avoid node memory bloat and $O(N^2)$ traversal traps.
- [x] Sets: Leverage `HashSet` backing maps, `LinkedHashSet` insertion order, and `TreeSet` red-black trees.
- [x] HashMap Basics: Execute $O(1)$ CRUD, `computeIfAbsent`, and efficient `entrySet` iteration.
- [x] HashMap Internals [ADVANCED]: Deconstruct `(n - 1) & hash`, 0.75 load factor, and Java 8 treeification.
- [x] Queues & Deques: Implement FIFO and LIFO pipelines using `ArrayDeque`, and heap priority triage via `PriorityQueue`.
- [x] Sorting & Timsort: Contrast `Comparable` natural order against fluent chained `Comparator`s.

👉 **Next Phase:** **[Phase 5: Under the Hood: JVM Architecture & Memory Mechanics — Topic 5.1: JDK vs JRE vs JVM & The Bytecode Execution Lifecycle](../05-jvm/01-jdk-jre-jvm.md)**
