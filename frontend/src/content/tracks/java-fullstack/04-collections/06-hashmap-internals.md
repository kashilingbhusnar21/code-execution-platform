# HashMap Deep Dive: Buckets, Collisions & Treeification [ADVANCED]

> **Phase 4: Data Structures & Java Collections Framework**  
> *Estimated Reading Time: 60 minutes | Total Practice Effort: ~4.0 hours | Tier: Advanced Deep Dive*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
In Topic 4.5, you used `HashMap` to store and retrieve data in $O(1)$ constant time. But how does that actually work in memory?
- *What happens when two different keys generate the exact same hash code?*
- *Why does Java force the bucket array length to always be a power of 2 ($16, 32, 64 \dots$)?*
- *What is "Treeification", and why was it introduced in Java 8 to prevent Denial-of-Service (DoS) attacks?*

`HashMap` internals is the single most frequently tested topic in senior and junior Java technical interviews. Mastering buckets, bitwise index masking, the 0.75 load factor, and Red-Black tree conversion will give you complete mastery over Java memory and algorithmic performance.

### Prerequisites
- [Topic 3.8: The Object Contract: equals(), hashCode() & Identity vs Equality](../03-core/08-equals-hashcode.md)
- [Topic 4.5: Map Interface: HashMap Fundamentals & Key-Value Usage](05-hashmap-basics.md)

---

## 2. Plain-English Mental Model

Think of `HashMap` like a high-speed coat check room with numbered bins:

```
[ Key: "User-90" ] 
       |
       v  1. Calculate Hash & Apply Bitwise Mask
[ Bin Index: 4 ]
       |
       v  2. Inspect Bucket 4
  +-------------------------------------------------------------+
  | Bucket 4 (Linked List):                                      |
  | [Node A] ---> [Node B] ---> [Node C (Our Match!)]           |
  |                                                             |
  | If this chain reaches 8 items...                            |
  | TRANSFORM INTO BALANCED RED-BLACK SEARCH TREE!              |
  +-------------------------------------------------------------+
```

1. **Hash Calculation:** The key's `hashCode()` is converted into a positive integer.
2. **Index Masking:** The integer is mapped to an index in the bucket array (`Node<K,V>[] table`).
3. **Collision Chaining:** If multiple keys map to the same bucket, they are linked together in a list.
4. **Treeification:** If too many keys collide in the same bucket, the linked list transforms into a balanced Red-Black Tree to guarantee $O(\log N)$ worst-case performance.

---

## 3. The 4-Step Life of a `map.put(key, value)`

```mermaid
flowchart TD
    A["1. Calculate Hash Code & Perturbation<br/>(h = key.hashCode()) ^ (h >>> 16)"] --> B["2. Compute Bucket Index<br/>index = (table.length - 1) & hash"]
    B --> C{"Is Bucket Empty?"}
    C -->|Yes| D["Insert new Node<K, V> into bucket array"]
    C -->|No: Collision!| E{"Is it already a TreeNode?"}
    E -->|Yes| F["Insert into Red-Black Tree: O(log N)"]
    E -->|No| G["Traverse Linked List<br/>Compare via equals()"]
    G --> H{"Does key match existing node?"}
    H -->|Yes| I["Overwrite old value and return it"]
    H -->|No| J["Append new Node to tail of list"]
    J --> K{"Chain length >= 8 AND capacity >= 64?"}
    K -->|Yes| L["TREEIFY: Convert bucket to Red-Black Tree!"]
    D --> M{"Total items > threshold (capacity * 0.75)?"}
    I --> M
    L --> M
    M -->|Yes| N["RESIZE: Double bucket capacity (e.g. 16 -> 32) & rehash"]
```

---

## 4. Deep Dive: Bitwise Index Masking (`(n - 1) & hash`)

In math, you map an arbitrary integer into a range of size $N$ using the modulus operator (`hash % N`).  
However, the integer division `%` instruction is **extremely slow on modern CPUs** (requiring 20 to 40 CPU clock cycles).

Java's architects engineered a brilliant optimization: **If $N$ (capacity) is always a power of 2 ($16, 32, 64 \dots$), then:**
$$\text{hash} \% N \equiv \text{hash} \ \& \ (N - 1)$$

Bitwise AND (`&`) executes in a **single CPU clock cycle**!

### Example (Capacity = 16):
- Capacity $N = 16$ (binary: `0001 0000`)
- Mask $(N - 1) = 15$ (binary: `0000 1111`)
- Any hash bitwise-ANDed with `0000 1111` preserves only the last 4 bits, perfectly bounding the result between `0` and `15`!

### The Perturbation Function (Bit Spreading)
Before calculating the index, Java applies a bit-shift:
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
**Why:** Many hash codes differ only in their upper bits. If we only look at the lower 4 bits, collisions skyrocket! Shifting by 16 bits (`h >>> 16`) mixes the high-order bits into the low-order bits, distributing keys evenly across all buckets.

---

## 5. Load Factor (0.75) and Rehashing

`HashMap` has two critical tuning parameters:
1. **Initial Capacity:** Default is `16`.
2. **Load Factor:** Default is `0.75`.

The **Threshold** for resizing is:
$$\text{Threshold} = \text{Capacity} \times \text{Load Factor} = 16 \times 0.75 = 12$$

When the 13th key-value pair is added, the `HashMap` automatically **resizes**:
- A new table of size **$32$ (double the capacity)** is allocated in the Heap.
- All existing entries are re-indexed into the new buckets.
- Why 0.75? Statistical analysis (Poisson distribution) proves that 0.75 offers the optimal balance between time efficiency (minimal collisions) and space efficiency (minimal wasted memory).

---

## 6. Java 8 Treeification: Defending Against Hash DoS Attacks

In Java 7, collisions were stored in simple linked lists ($O(N)$ lookup).  
Malicious attackers discovered they could send HTTP requests with specially crafted parameter strings that all produced the exact same hash code. This forced servers into $O(N)$ linked list traversals, pegging CPU at 100% and taking down enterprise web services (**Hash Collision DoS attack**).

### The Java 8 Red-Black Tree Solution:
- **`TREEIFY_THRESHOLD = 8`**: If a bucket's linked list reaches 8 nodes, AND total map capacity is at least 64, the linked list is converted into a **Red-Black Tree** (`TreeNode<K, V>`).
- Search time in that bucket drops from $O(N)$ linear time to **$O(\log N)$ logarithmic time**.
- **`UNTREEIFY_THRESHOLD = 6`**: If deletions reduce the tree size to 6, it converts back to a lightweight linked list to conserve memory.

---

## 7. Working Code Example: Simulating the Bitwise Index Calculation

```java
// File: HashMapInternalsDemo.java

public class HashMapInternalsDemo {
    public static void main(String[] args) {
        int capacity = 16;
        int mask = capacity - 1; // 15 (0000 1111 in binary)

        String[] sampleKeys = {"User_Alice", "Order_1001", "Session_XYZ", "Product_A"};

        System.out.println("=== Internal Bucket Index Computation (Capacity: " + capacity + ") ===");

        for (String key : sampleKeys) {
            int rawHash = key.hashCode();
            // Java's perturbation function: mixes high bits into low bits
            int spreadHash = rawHash ^ (rawHash >>> 16);
            // Bitwise AND mask equivalent to spreadHash % capacity
            int bucketIndex = spreadHash & mask;

            System.out.printf("Key: %-15s | Raw Hash: %11d | Bucket Index: %2d%n", 
                              key, rawHash, bucketIndex);
        }
    }
}
```

### Exact Output:
```text
=== Internal Bucket Index Computation (Capacity: 16) ===
Key: User_Alice     | Raw Hash:  -909562725 | Bucket Index:  7
Key: Order_1001     | Raw Hash:  -975929845 | Bucket Index: 11
Key: Session_XYZ    | Raw Hash:  1485603774 | Bucket Index:  6
Key: Product_A      | Raw Hash:  1834988771 | Bucket Index:  7
```
*Notice that `User_Alice` and `Product_A` both mapped to Bucket Index 7! This is a **Hash Collision**—both items will live in bucket 7 connected via a linked list or tree node.*

---

## 8. Common Rookie Mistakes & Interview Traps

### Trap: Mutable Keys in HashMap
```java
class CustomerId {
    int id; // Mutable field!
}

Map<CustomerId, String> map = new HashMap<>();
CustomerId c = new CustomerId();
c.id = 5;
map.put(c, "Alice");

// Later, mutating the key:
c.id = 999; 
// The hashCode changed! map.get(c) now searches the wrong bucket!
System.out.println(map.get(c)); // Prints: null (The entry is LOST forever in memory!)
```
> [!CAUTION] Production Rule
> **Always use immutable classes (`String`, `Integer`, `UUID`, or a record) as Map keys.** If a key must be a custom class, declare all fields `private final`.

---

## 9. Guided Hands-On Exercise

### Challenge: Custom Hash Bucket Simulator
Create a program named `BucketCollisionSimulator.java`:
1. Simulate a fixed array of 8 buckets: `int capacity = 8;`.
2. Given a list of keys: `{"apple", "banana", "cherry", "date", "elderberry", "fig", "grape"}`.
3. Compute the bucket index for each key using `(key.hashCode() ^ (key.hashCode() >>> 16)) & (capacity - 1)`.
4. Count and display how many keys landed in each of the 8 buckets. Identify the bucket with the highest collision count.

<details>
<summary>💡 Hint: Bucket array counter</summary>

```java
int[] bucketCounts = new int[capacity];
// for each key:
int index = Math.abs(spreadHash) & (capacity - 1);
bucketCounts[index]++;
```
</details>

<details>
<summary>✅ Solution</summary>

```java
public class BucketCollisionSimulator {
    public static void main(String[] args) {
        String[] fruits = {"apple", "banana", "cherry", "date", "elderberry", "fig", "grape"};
        int capacity = 8;
        int mask = capacity - 1;

        int[] bucketCounts = new int[capacity];

        for (String fruit : fruits) {
            int h = fruit.hashCode();
            int spread = h ^ (h >>> 16);
            int bucket = (spread & mask);
            bucketCounts[bucket]++;
        }

        System.out.println("=== Bucket Distribution Simulation ===");
        int maxCollisions = 0;
        int mostCrowdedBucket = 0;

        for (int i = 0; i < capacity; i++) {
            System.out.println("Bucket [" + i + "]: " + bucketCounts[i] + " items");
            if (bucketCounts[i] > maxCollisions) {
                maxCollisions = bucketCounts[i];
                mostCrowdedBucket = i;
            }
        }

        System.out.println("\nMost Crowded Bucket: Bucket " + mostCrowdedBucket 
                           + " with " + maxCollisions + " collisions.");
    }
}
```

**Terminal Output (Representative):**
```text
=== Bucket Distribution Simulation ===
Bucket [0]: 1 items
Bucket [1]: 0 items
Bucket [2]: 2 items
Bucket [3]: 1 items
Bucket [4]: 0 items
Bucket [5]: 1 items
Bucket [6]: 2 items
Bucket [7]: 0 items

Most Crowded Bucket: Bucket 2 with 2 collisions.
```
</details>

---

## 10. Knowledge Check & Interview Defense

### Quick Quiz
1. **Why does Java calculate bucket indices using `(n - 1) & hash` instead of `hash % n`?**
   - [x] A) Bitwise AND executes in a single CPU cycle, whereas integer division (`%`) takes dozens of cycles. This requires capacity $n$ to be a power of 2.
   - [ ] B) Modulus doesn't work on negative numbers.
   - [ ] C) Java does not have a modulus operator.
   - [ ] D) Only for string keys.

2. **What threshold triggers a bucket in Java 8 HashMap to convert from a linked list to a Red-Black Tree?**
   - [ ] A) 2 items
   - [ ] B) 4 items
   - [x] C) 8 items (with a minimum table capacity of 64)
   - [ ] D) 16 items

3. **What is the default load factor of a Java HashMap, and what is its purpose?**
   - [x] A) 0.75; it determines when the map doubles its capacity (`capacity * 0.75`), providing the optimal balance between time and space efficiency.
   - [ ] B) 0.50; to save disk space.
   - [ ] C) 1.0; resizing only happens when completely full.
   - [ ] D) 0.90; for graphics processing.

---

### Junior Developer Interview Q&A

#### Q: "Explain what happens internally when `map.put(key, value)` encounters a hash collision in Java 8+."
> **Model Answer:**  
> "When `put()` is called, Java computes the bucket index via `(table.length - 1) & hash`. If that bucket already contains one or more nodes, a **hash collision** has occurred.  
> 
> The JVM handles it in three steps:
> 1. **Check Equality:** It traverses the bucket's nodes, checking if `node.hash == hash && (node.key == key || key.equals(node.key))`. If a matching key is found, it overwrites the value and returns the old value.
> 2. **Append to Chain:** If no matching key exists, it appends a new `Node<K, V>` to the tail of the linked list ($O(N)$).
> 3. **Treeification:** If the number of nodes in this bucket reaches **8** and the table's total capacity is at least **64**, Java converts the linked list into a balanced **Red-Black Tree (`TreeNode<K, V>`)**. This guarantees that worst-case search and insertion times remain bounded at $O(\log N)$ rather than degrading to $O(N)$."

---

## 11. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Diagram the complete execution flow of `map.put()` from hash spreading to insertion.
- [ ] Explain why capacities are powers of 2 to enable `(n - 1) & hash` bitwise masking.
- [ ] Understand why load factor 0.75 triggers automatic capacity doubling.
- [ ] Defend Java 8's Red-Black treeification mechanism against Hash DoS attacks in interviews.

👉 **Next Lesson:** **[Topic 4.7: Queue & Deque: FIFO Processing, ArrayDeque & PriorityQueue](07-queues.md)**
