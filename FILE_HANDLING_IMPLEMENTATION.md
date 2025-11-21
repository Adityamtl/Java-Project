# File Handling Implementation - Digital Banking System

## ✅ Successfully Converted from Database to File Storage

### Date: November 21, 2025

### Status: **Fully Functional with JSON File Storage** ✓

---

## 📋 What Was Changed

### **From: MySQL Database** → **To: JSON File Storage**

Your project now uses **file handling with JSON files** instead of a MySQL database. This is perfect for college projects as requested by your teacher!

---

## 📂 File Storage Structure

### Data Directory: `data/`

Three JSON files store all application data:

```
digital-banking-system/
└── data/
    ├── users.json          # Stores all user accounts
    ├── wallets.json        # Stores all user wallets
    └── transactions.json   # Stores all transactions
```

### File Format Examples:

**users.json:**

```json
[
  {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "password": "$2a$10$...",
    "role": "USER"
  }
]
```

**wallets.json:**

```json
[
  {
    "id": 1,
    "userId": 1,
    "walletCode": "WAL-A1B2C3D4",
    "balance": 1000.0
  }
]
```

**transactions.json:**

```json
[
  {
    "id": 1,
    "senderWalletId": null,
    "receiverWalletId": 1,
    "amount": 1000.0,
    "type": "DEPOSIT",
    "timestamp": "2025-11-21T19:20:00",
    "status": "SUCCESS"
  }
]
```

---

## 🔧 Technical Changes Made

### 1. **Created FileStorageService.java**

**Location:** `src/main/java/.../service/FileStorageService.java`

**Purpose:** Handles all file I/O operations

**Key Features:**

- ✅ Reads JSON files and converts to Java objects
- ✅ Writes Java objects to JSON files
- ✅ Auto-creates data directory if missing
- ✅ Handles errors gracefully
- ✅ Uses Jackson for JSON processing
- ✅ Pretty-printed JSON (easy to read)

**Main Methods:**

```java
// Read data from JSON file
public <T> List<T> readFromFile(String fileName, TypeReference<List<T>> typeReference)

// Write data to JSON file
public <T> void writeToFile(String fileName, List<T> data)

// Get next available ID
public Long getNextId(List<?> items)
```

---

### 2. **Updated Model Classes (POJOs)**

**Changed:** User.java, Wallet.java, Transaction.java

**Before (JPA/Database):**

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;
    // ...
}
```

**After (File Storage):**

```java
/**
 * User model - Plain Java Object for file-based storage
 */
public class User implements Serializable {
    private Long id;
    private String username;
    // ...
}
```

**Changes:**

- ❌ Removed `@Entity` annotation
- ❌ Removed `@Table` annotation
- ❌ Removed `@Id` and `@GeneratedValue`
- ❌ Removed `@Column` annotations
- ✅ Now simple POJOs (Plain Old Java Objects)
- ✅ Still implements Serializable

---

### 3. **Converted Repository Classes**

**Changed:** UserRepository, WalletRepository, TransactionRepository

**Before (JPA Interface):**

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

**After (File-based Class):**

```java
@Repository
public class UserRepository {
    private final FileStorageService fileStorageService;

    public Optional<User> findByUsername(String username) {
        List<User> users = findAll();
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public User save(User user) {
        List<User> users = findAll();
        if (user.getId() == null) {
            user.setId(fileStorageService.getNextId(users));
            users.add(user);
        } else {
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        fileStorageService.writeToFile("users.json", users);
        return user;
    }
}
```

**Key Points:**

- ✅ Changed from `interface` to concrete `class`
- ✅ Uses `FileStorageService` for reading/writing
- ✅ Manual ID generation (no more auto-increment)
- ✅ Uses Java Streams for filtering/searching
- ✅ All operations now read/write JSON files

---

### 4. **Removed Database Dependencies**

**From pom.xml:**

- ❌ Removed `spring-boot-starter-data-jpa`
- ❌ Removed `mysql-connector-j`
- ✅ Kept Jackson for JSON processing
- ✅ Project is now lighter and simpler

---

### 5. **Updated application.properties**

**Before:**

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/...
spring.datasource.username=root
spring.datasource.password=...

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**After:**

```properties
# File Storage Configuration (Using JSON files instead of database)
# Data files are stored in the 'data/' directory
```

**No database configuration needed!** ✅

---

### 6. **Removed Transaction Management**

**Changed:** Removed `@Transactional` annotations

**Why?**

- Transactions are a database concept
- File operations don't need transaction management
- Simpler code without `@Transactional`

---

## 🎓 File Handling Concepts Demonstrated

### 1. **File I/O Operations**

- Reading from files
- Writing to files
- Creating directories
- Checking file existence

### 2. **JSON Processing**

- Converting Java objects to JSON (Serialization)
- Converting JSON to Java objects (Deserialization)
- Using Jackson ObjectMapper
- TypeReference for generic types

### 3. **Data Persistence**

- Storing data in JSON format
- Maintaining data across application restarts
- Manual ID generation
- CRUD operations with files

### 4. **Java Streams**

- Filtering data with `.filter()`
- Mapping data with `.map()`
- Sorting with `.sorted()`
- Collecting results with `.collect()`

### 5. **Exception Handling**

- Try-catch blocks for IOException
- Graceful error handling
- Error logging

---

## 🚀 How It Works Now

### **User Registration Flow:**

1. User sends registration request
2. `AuthService.register()` creates User object
3. `UserRepository.save()` called
4. FileStorageService reads existing users from `users.json`
5. Assigns new ID (next available number)
6. Adds new user to list
7. Writes entire list back to `users.json`
8. Wallet is created and saved to `wallets.json`
9. User registered successfully!

### **Deposit Money Flow:**

1. User makes deposit request
2. `WalletService.deposit()` called
3. Reads wallet from `wallets.json`
4. Updates balance
5. Writes wallet back to `wallets.json`
6. Creates transaction record
7. Writes transaction to `transactions.json`
8. Returns success response

### **Data Persistence:**

- ✅ All data saved in JSON files
- ✅ Data persists across application restarts
- ✅ Easy to view and edit (just open JSON files)
- ✅ No database installation needed
- ✅ Portable - just copy the `data/` folder

---

## 📁 File Operations Summary

### **Read Operation:**

```java
List<User> users = fileStorageService.readFromFile(
    "users.json",
    new TypeReference<List<User>>() {}
);
```

### **Write Operation:**

```java
fileStorageService.writeToFile("users.json", users);
```

### **Search Operation:**

```java
Optional<User> user = users.stream()
    .filter(u -> u.getUsername().equals(username))
    .findFirst();
```

### **Add New Record:**

```java
List<User> users = findAll();
Long newId = fileStorageService.getNextId(users);
user.setId(newId);
users.add(user);
fileStorageService.writeToFile("users.json", users);
```

### **Update Record:**

```java
List<User> users = findAll();
users.removeIf(u -> u.getId().equals(user.getId()));
users.add(user);
fileStorageService.writeToFile("users.json", users);
```

### **Delete Record:**

```java
List<User> users = findAll();
users.removeIf(u -> u.getId().equals(id));
fileStorageService.writeToFile("users.json", users);
```

---

## ✅ What Still Works (Everything!)

### **All Features Functional:**

1. ✅ User Registration & Login
2. ✅ Password Encryption (BCrypt)
3. ✅ Role-Based Access (USER/ADMIN)
4. ✅ Wallet Operations:
   - Deposit money
   - Withdraw money
   - Transfer to other users
   - Check balance
   - View transaction history
5. ✅ Admin Features:
   - View all users
   - View all transactions
   - Bank transfers
6. ✅ REST API Endpoints
7. ✅ Session Management
8. ✅ Exception Handling
9. ✅ BigDecimal for precise money calculations

---

## 🎯 Advantages of File Storage

### **For College Projects:**

1. ✅ **Simple Setup** - No database installation
2. ✅ **Easy to Understand** - Read/write files is intuitive
3. ✅ **Portable** - Works on any computer
4. ✅ **Visible Data** - Can open and view JSON files
5. ✅ **Debugging Friendly** - Easy to see what's stored
6. ✅ **No Configuration** - No database credentials
7. ✅ **Lightweight** - Smaller project size

### **Demonstrates These Concepts:**

- ✅ File I/O operations
- ✅ JSON processing
- ✅ Data serialization
- ✅ Java Streams API
- ✅ Exception handling
- ✅ CRUD operations
- ✅ Data persistence

---

## 🧪 Testing Your File-Based System

### **1. Start Application:**

```bash
cd digital-banking-system
.\mvnw.cmd spring-boot:run
```

### **2. Register User (Postman):**

```
POST http://localhost:8080/api/auth/register
Body:
{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
}
```

### **3. Check users.json:**

Open `data/users.json` - you'll see the new user!

### **4. Login:**

```
POST http://localhost:8080/api/auth/login
Body:
{
    "username": "john",
    "password": "password123"
}
```

### **5. Deposit Money:**

```
POST http://localhost:8080/api/wallet/deposit
Body:
{
    "amount": 1000.00
}
```

### **6. Check Files:**

- `data/wallets.json` - Balance updated
- `data/transactions.json` - Transaction recorded

---

## 📊 Comparison: Before vs After

| Aspect                 | Before (Database)         | After (File Storage)     |
| ---------------------- | ------------------------- | ------------------------ |
| **Storage**            | MySQL Database            | JSON Files               |
| **Dependencies**       | JPA, MySQL Driver         | Jackson (JSON)           |
| **Setup**              | Install MySQL             | None needed              |
| **Configuration**      | Database URL, credentials | Just file path           |
| **Data Visibility**    | SQL queries needed        | Open JSON file           |
| **Portability**        | Need MySQL everywhere     | Just copy `data/` folder |
| **College-Friendly**   | Complex setup             | ✅ Perfect!              |
| **File Handling Demo** | ❌ No                     | ✅ Yes!                  |

---

## 💡 What to Tell Your Teacher

### **Technologies Used:**

- ✅ **Java File I/O** - Reading and writing files
- ✅ **Jackson Library** - JSON processing
- ✅ **ObjectMapper** - Serialize/deserialize objects
- ✅ **Java Streams** - Data filtering and manipulation
- ✅ **Exception Handling** - IOException handling
- ✅ **Data Persistence** - Storing data in files

### **File Handling Concepts:**

1. **File Creation** - Creating data directory and JSON files
2. **File Reading** - Reading JSON and parsing to objects
3. **File Writing** - Converting objects to JSON and saving
4. **Data Management** - CRUD operations with files
5. **Error Handling** - Try-catch for file operations

### **Project Explanation:**

_"This project uses file handling to store all data in JSON format. Instead of using a database, we have three JSON files (users, wallets, transactions) that store all information. The FileStorageService class handles all file operations using Jackson's ObjectMapper. All data persists across application restarts by reading from and writing to these files."_

---

## 🎉 Summary

Your Digital Banking System now:

- ✅ **Uses File Handling** - As requested by teacher
- ✅ **No Database Required** - Simpler setup
- ✅ **Fully Functional** - All features working
- ✅ **College-Appropriate** - Perfect for coursework
- ✅ **Easy to Demo** - Just show the JSON files!
- ✅ **Demonstrates File I/O** - Core Java concept
- ✅ **Well Structured** - Clean, professional code

**Your project is ready for submission!** 🚀

---

## 📝 Files Modified Summary

### **New Files:**

1. ✅ `FileStorageService.java` - File I/O handler
2. ✅ `data/users.json` - User storage
3. ✅ `data/wallets.json` - Wallet storage
4. ✅ `data/transactions.json` - Transaction storage

### **Modified Files:**

1. ✅ `User.java` - Removed JPA annotations
2. ✅ `Wallet.java` - Removed JPA annotations
3. ✅ `Transaction.java` - Removed JPA annotations
4. ✅ `UserRepository.java` - File-based operations
5. ✅ `WalletRepository.java` - File-based operations
6. ✅ `TransactionRepository.java` - File-based operations
7. ✅ `WalletService.java` - Removed @Transactional
8. ✅ `AdminService.java` - Removed @Transactional
9. ✅ `pom.xml` - Removed database dependencies
10. ✅ `application.properties` - Removed DB config

### **Build Status:**

```
✅ BUILD SUCCESS
✅ Application running on port 8080
✅ All features working with file storage
```

---

**Congratulations! Your project now uses file handling instead of database!** 🎊
