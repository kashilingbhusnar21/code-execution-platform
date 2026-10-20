# SQL CRUD Operations: CREATE, INSERT, SELECT, UPDATE, DELETE

> **Phase 7: Relational Databases & SQL Mastery**  
> *Estimated Reading Time: 45 minutes | Total Practice Effort: ~3.0 hours | Tier: Core Essential*

---

## 1. Purpose & Prerequisites

### Why This Matters in Full Stack Development
Every web application—from Twitter to Amazon—is essentially a **CRUD** interface over a database:
- **C**reate: Register a new user (`INSERT`).
- **R**ead: View user profile and past orders (`SELECT`).
- **U**pdate: Change shipping address or password (`UPDATE`).
- **D**elete: Close account or remove an item from a cart (`DELETE`).

Writing accurate SQL statements is essential. A single careless `UPDATE` or `DELETE` without a `WHERE` clause can overwrite or delete every single customer record in your production database within milliseconds!

### Prerequisites
- [Topic 7.1: Relational Concepts: Tables, Primary Keys, Foreign Keys & Schema](01-db-concepts.md)

---

## 2. Plain-English Mental Model

```
[ INSERT ]  ---> "Add a brand-new folder to the filing cabinet"
[ SELECT ]  ---> "Pull out specific folders and read selected pages"
[ UPDATE ]  ---> "Open a specific folder and pencil in a new phone number"
[ DELETE ]  ---> "Take a specific folder and run it through the shredder"
```

---

## 3. The 4 Fundamental CRUD Statements

### 1. `INSERT`: Adding Rows
```sql
-- Single-row insert with explicit column list
INSERT INTO users (username, email, is_active)
VALUES ('alex_dev', 'alex@company.com', true);

-- High-performance batch insert (Multiple rows in one network roundtrip!)
INSERT INTO products (sku, title, price, stock_quantity)
VALUES 
    ('KB-101', 'Mechanical Keyboard', 89.99, 50),
    ('MS-202', 'Wireless Gaming Mouse', 39.99, 120),
    ('MON-303', '4K Ultra-Wide Monitor', 499.00, 15);
```

### 2. `SELECT`: Querying Rows
```sql
-- Projection: Select only the specific columns you need (Never use SELECT * in production!)
SELECT id, username, email 
FROM users 
WHERE is_active = true;
```

### 3. `UPDATE`: Modifying Existing Rows
```sql
UPDATE products 
SET price = 79.99, stock_quantity = 45 
WHERE sku = 'KB-101';
```

### 4. `DELETE`: Removing Rows
```sql
DELETE FROM products 
WHERE sku = 'MS-202';
```

> [!CAUTION] The Production Nightmare: Missing `WHERE` Clauses!
> If you execute:
> ```sql
> UPDATE users SET is_active = false; -- ACCIDENTALLY DEACTIVATES ALL USERS IN COMPANY!
> DELETE FROM orders;                 -- ACCIDENTALLY DELETES ENTIRE SALES HISTORY!
> ```
> Without a `WHERE` clause, `UPDATE` and `DELETE` execute on **every single row in the table**. Always write the `WHERE` condition first, verify it with a `SELECT`, and only then execute the `UPDATE` or `DELETE`!

---

## 4. Mastering the `WHERE` Clause & SQL Operators

| Operator | Plain-English Meaning | Example |
| :--- | :--- | :--- |
| `=`, `!=` (or `<>`) | Equals / Not Equals | `WHERE status = 'PAID'` |
| `<`, `>`, `<=`, `>=`| Numeric / Date Comparisons | `WHERE price >= 100.00` |
| **`IN (...)`** | Matches any value in a set | `WHERE country IN ('US', 'CA', 'GB')` |
| **`BETWEEN x AND y`**| Inclusive range check | `WHERE created_at BETWEEN '2026-01-01' AND '2026-01-31'` |
| **`LIKE`** | Pattern matching with wildcards (`%` = any chars, `_` = 1 char) | `WHERE email LIKE '%@gmail.com'` |
| **`IS NULL` / `IS NOT NULL`**| Checks for missing values (**Never use `= NULL`!**) | `WHERE deleted_at IS NULL` |

---

## 5. SQL Three-Valued Logic: Why `= NULL` is ALWAYS False!

In SQL, `NULL` does not mean "empty string" or "zero"; it means **"UNKNOWN"**.  
Because of this, SQL uses **Three-Valued Logic** (`TRUE`, `FALSE`, and `UNKNOWN`):

```sql
SELECT * FROM users WHERE deleted_at = NULL; -- ALWAYS RETURNS ZERO ROWS!
```
*Why:* You cannot ask "Is this unknown value equal to another unknown value?". The result is `UNKNOWN`, which is evaluated as `FALSE` by `WHERE` filters!  
> [!IMPORTANT] Production Rule
> **Always use `IS NULL` or `IS NOT NULL`** to test for null values in SQL.

---

## 6. `DELETE` vs `TRUNCATE TABLE`

| Dimension | `DELETE FROM table;` | `TRUNCATE TABLE table;` |
| :--- | :--- | :--- |
| **SQL Category** | DML (Data Manipulation) | DDL (Data Definition) |
| **How It Executes** | Deletes rows **one by one**, writing undo records to transaction log. | Drops the internal table storage and recreates empty structure. |
| **Speed** | Slow on large tables (can take minutes). | **Instantaneous** (sub-millisecond). |
| **Can Filter with `WHERE`?**| ✅ Yes | ❌ No (wipes entire table). |
| **Resets AUTO_INCREMENT?**| ❌ No (next ID continues from max) | ✅ **Yes (resets ID counter back to 1)** |

---

## 7. Working Code Example: Interactive SQL CRUD Script

```sql
-- File: crud_practice.sql

-- 1. Insert Initial Customers
INSERT INTO users (username, email, is_active)
VALUES 
    ('alice_w', 'alice@cloud.io', true),
    ('bob_m', 'bob@test.com', true),
    ('charlie_k', 'charlie@gmail.com', false);

-- 2. Querying with Compound Filters
SELECT id, username, email 
FROM users 
WHERE is_active = true 
  AND (email LIKE '%@cloud.io' OR username = 'bob_m');

-- 3. Updating an email address safely with WHERE
UPDATE users 
SET email = 'alice.morgan@cloud.io' 
WHERE username = 'alice_w';

-- 4. Verifying the update
SELECT username, email FROM users WHERE username = 'alice_w';

-- 5. Safe Delete of Inactive Test Accounts
DELETE FROM users 
WHERE is_active = false 
  AND email LIKE '%@gmail.com';
```

---

## 8. Guided Hands-On Exercise

### Challenge: Order Management CRUD Engine
Given an `orders` table: `id` (BigInt), `customer_id` (BigInt), `amount` (Decimal), `status` (Varchar), `shipped_at` (Datetime nullable).  
Write SQL statements to:
1. Insert two orders for `customer_id = 42`:
   - Order 1: `$120.50`, status `'PENDING'`, `shipped_at = NULL`.
   - Order 2: `$45.00`, status `'PAID'`, `shipped_at = NULL`.
2. Select all orders with an amount strictly greater than `$50.00` that have **not yet shipped** (`shipped_at IS NULL`).
3. Update Order 2: change its status to `'SHIPPED'` and set `shipped_at` to the current timestamp (`CURRENT_TIMESTAMP`).
4. Delete any orders that are still `'PENDING'` with an amount less than `$10.00`.

<details>
<summary>💡 Hint: Update with timestamp</summary>

```sql
UPDATE orders 
SET status = 'SHIPPED', shipped_at = CURRENT_TIMESTAMP 
WHERE id = 2;
```
</details>

<details>
<summary>✅ Solution</summary>

```sql
-- 1. Insert Orders
INSERT INTO orders (customer_id, amount, status, shipped_at)
VALUES 
    (42, 120.50, 'PENDING', NULL),
    (42, 45.00, 'PAID', NULL);

-- 2. Query Unshipped Orders > $50
SELECT id, customer_id, amount, status 
FROM orders 
WHERE amount > 50.00 
  AND shipped_at IS NULL;

-- 3. Update Shipped Status
UPDATE orders 
SET status = 'SHIPPED', shipped_at = CURRENT_TIMESTAMP 
WHERE customer_id = 42 AND amount = 45.00;

-- 4. Delete Low-Value Pending Orders
DELETE FROM orders 
WHERE status = 'PENDING' 
  AND amount < 10.00;
```
</details>

---

## 9. Knowledge Check & Interview Defense

### Quick Quiz
1. **Why does `SELECT * FROM users WHERE status = NULL;` return zero rows even if null rows exist?**
   - [x] A) In SQL's three-valued logic, comparing anything to `NULL` using `=` evaluates to `UNKNOWN` (treated as false); you must use `IS NULL`.
   - [ ] B) Because NULL is converted to zero.
   - [ ] C) Because SQL is case-sensitive.
   - [ ] D) Only MySQL does this.

2. **What is the key functional difference between `DELETE FROM users;` and `TRUNCATE TABLE users;`?**
   - [ ] A) `DELETE` cannot be undone.
   - [x] B) `DELETE` removes rows one by one through the transaction log; `TRUNCATE` deallocates table pages instantly and resets the `AUTO_INCREMENT` counter back to 1.
   - [ ] C) `TRUNCATE` is only for temporary tables.
   - [ ] D) `DELETE` resets the primary key.

3. **In the SQL statement `SELECT * FROM users WHERE name LIKE 'A%';`, what does the `%` symbol mean?**
   - [x] A) It matches zero, one, or multiple arbitrary characters.
   - [ ] B) It matches exactly one single character.
   - [ ] C) It divides the name by 100.
   - [ ] D) It performs a regex search.

---

### Junior Developer Interview Q&A

#### Q: "What safety protocols should a backend engineer follow before executing an UPDATE or DELETE in a production database?"
> **Model Answer:**  
> "I adhere to three strict safety protocols:
> 1. **Write and Test the `SELECT` Query First:** Before running `DELETE FROM orders WHERE ...`, write `SELECT count(*) FROM orders WHERE ...` with the exact same `WHERE` clause to verify the exact rows affected.
> 2. **Explicit Transactions:** Wrap the statement inside a manual transaction:
>    ```sql
>    START TRANSACTION;
>    UPDATE users SET is_active = false WHERE id = 101;
>    -- Verify affected row count!
>    -- If correct: COMMIT; 
>    -- If anything looks wrong: ROLLBACK;
>    ```
> 3. **Use Safe Update Mode:** Enable database safety flags (such as MySQL's `SQL_SAFE_UPDATES = 1`) that reject any `UPDATE` or `DELETE` statement that lacks a key constraint in the `WHERE` clause."

---

## 10. Mastery Checklist & Next Step

Before moving on, verify that you can:
- [ ] Write batch `INSERT` statements with explicit column definitions.
- [ ] Construct safe `UPDATE` and `DELETE` statements with strict `WHERE` conditions.
- [ ] Explain SQL Three-Valued Logic and use `IS NULL` correctly.
- [ ] Differentiate between `DELETE` and `TRUNCATE TABLE`.

👉 **Next Lesson:** **[Topic 7.3: Filtering, Sorting & Aggregations (WHERE, GROUP BY, HAVING)](03-filtering-aggregations.md)**
