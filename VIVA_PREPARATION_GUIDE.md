# 🎯 VIVA Preparation Guide - Digital Banking System

## ✅ Project Status: READY FOR VIVA

**Date:** November 21, 2025  
**Build:** ✅ SUCCESS  
**All Features:** ✅ Working  
**Complexity:** ✅ Optimized for College Level

---

## 📋 Quick Project Overview

**What is this project?**

> A Digital Banking System where users can register, login, deposit/withdraw/transfer money. All data is stored in JSON files using file handling. Admin can manage all users and transactions.

**Main Technologies:**

- ✅ Java 21
- ✅ Spring Boot 4.0.0
- ✅ File Handling (JSON)
- ✅ REST APIs
- ✅ Spring Security

---

## 🎯 VIVA QUESTIONS & ANSWERS

### **Q1: What is your project about?**

**Answer:**
"This is a Digital Banking System built with Spring Boot. Users can create accounts, manage their digital wallets, deposit money, withdraw money, and transfer funds to other users. We use file handling to store all data in JSON format instead of using a database. Admin users have additional privileges to view all users and transactions."

---

### **Q2: Why are you using file handling instead of database?**

**Answer:**
"As per the course requirement, we implemented file handling to demonstrate Java file I/O operations. We store data in three JSON files: users.json for accounts, wallets.json for wallet information, and transactions.json for all financial transactions. This approach uses FileInputStream and FileOutputStream with Jackson library for JSON parsing."

---

### **Q3: Explain your project architecture**

**Answer:**
"We follow a layered architecture:

1. **Controller Layer** - Handles HTTP requests (@RestController)
2. **Service Layer** - Contains business logic (@Service)
3. **Repository Layer** - Handles file operations (reading/writing JSON)
4. **Model Layer** - Data classes like User, Wallet, Transaction

When a user makes a request, it goes through Controller → Service → Repository → JSON Files."

---

### **Q4: Show me the file handling code**

**Answer:**
"Yes, we have a FileStorageService class. Let me explain:

```java
// Reading from file
public <T> List<T> readFromFile(String fileName) {
    File file = new File("data/" + fileName);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(file, new TypeReference<List<T>>() {});
}

// Writing to file
public <T> void writeToFile(String fileName, List<T> data) {
    File file = new File("data/" + fileName);
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(file, data);
}
```

We use Jackson ObjectMapper to convert Java objects to JSON and vice versa."

---

### **Q5: What is Spring Boot?**

**Answer:**
"Spring Boot is a Java framework that simplifies web application development. It provides:

- Built-in web server (Tomcat)
- Dependency injection
- Auto-configuration
- REST API support
- Easy project setup

We don't need to configure everything manually - Spring Boot does it automatically."

---

### **Q6: What is REST API?**

**Answer:**
"REST API uses HTTP methods to communicate between client and server:

- **POST** - To create/update (register, login, deposit)
- **GET** - To retrieve data (balance, history)

Client sends JSON request → Server processes → Returns JSON response

Example:

````
POST /api/auth/register
Request: { username, email, password }
Response: { message, walletCode }
```"

---

### **Q7: How does authentication work?**
**Answer:**
"We use Spring Security with session-based authentication:
1. User logs in with username/password
2. We verify credentials using BCrypt encryption
3. Create HttpSession and store user info
4. Return session cookie to client
5. For future requests, session validates user

Passwords are never stored in plain text - we use BCrypt hashing for security."

---

### **Q8: Explain the main features**
**Answer:**
"**User Features:**
- Register new account (auto-creates wallet)
- Login/Logout
- Deposit money
- Withdraw money (checks balance)
- Transfer to other users (by wallet code)
- View balance and transaction history

**Admin Features:**
- View all users with balances
- View all transactions
- Perform bank transfers"

---

### **Q9: How do you prevent duplicate usernames?**
**Answer:**
"Before registering, we check if username already exists:
```java
if (userRepository.existsByUsername(username)) {
    throw new IllegalArgumentException("Username already exists");
}
````

We read all users from users.json and search using Java Streams."

---

### **Q10: What is BigDecimal and why use it?**

**Answer:**
"BigDecimal is used for precise decimal calculations. For money, we can't use double or float because they have precision errors.

Example:

```java
double a = 0.1 + 0.2; // = 0.30000000000000004 (wrong!)
BigDecimal b = new BigDecimal("0.1").add(new BigDecimal("0.2")); // = 0.3 (correct!)
```

For banking, accuracy is critical, so we use BigDecimal."

---

### **Q11: How do you handle insufficient balance?**

**Answer:**
"Before withdrawal or transfer, we check balance:

```java
if (wallet.getBalance().compareTo(amount) < 0) {
    // Save failed transaction
    Transaction failed = new Transaction(..., FAILED);
    transactionRepository.save(failed);
    throw new InsufficientBalanceException("Insufficient balance");
}
```

We record even failed transactions for audit purposes."

---

### **Q12: What is dependency injection?**

**Answer:**
"Spring automatically provides required dependencies through constructor:

```java
public class WalletService {
    private final WalletRepository walletRepository;

    // Spring injects WalletRepository automatically
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
}
```

We don't create objects manually - Spring manages the lifecycle."

---

### **Q13: How does transfer between users work?**

**Answer:**
"Transfer process:

1. Find sender wallet by userId
2. Find receiver wallet by walletCode
3. Check sender has sufficient balance
4. Subtract from sender's balance
5. Add to receiver's balance
6. Save both wallets to file
7. Create transaction record
8. Return success response

All steps are completed together - if any step fails, nothing is saved."

---

### **Q14: What is JSON and why use it?**

**Answer:**
"JSON (JavaScript Object Notation) is a lightweight data format:

```json
{
  "id": 1,
  "username": "john",
  "balance": 1000.0
}
```

Advantages:

- Human-readable
- Easy to parse
- Language-independent
- Less storage than XML"

---

### **Q15: Show me a working demo**

**Answer:**
"Yes, let me demonstrate using Postman:

1. **Register:** POST /api/auth/register
2. **Login:** POST /api/auth/login (get session)
3. **Deposit:** POST /api/wallet/deposit
4. **Check Balance:** GET /api/wallet/balance
5. **View History:** GET /api/wallet/history

I can also show the JSON files where data is stored."

---

## 📚 Important Concepts (Know These!)

### **1. Spring Boot Annotations**

```java
@RestController  // Handles web requests
@Service        // Business logic layer
@Repository     // Data access layer
@Configuration  // Configuration class
@Bean          // Creates Spring managed object
@Value         // Injects property values
```

### **2. HTTP Methods**

- **GET** - Retrieve data
- **POST** - Create/Update data

### **3. File Operations**

- **Read:** FileInputStream, ObjectMapper.readValue()
- **Write:** FileOutputStream, ObjectMapper.writeValue()
- **Directory:** File.mkdirs()

### **4. Security**

- **BCrypt** - Password encryption
- **HttpSession** - User session management
- **ROLE_USER** / **ROLE_ADMIN** - Authorization

---

## 🎯 What Makes This Project Special

✅ **File Handling** - As per requirement  
✅ **Spring Boot** - Industry standard framework  
✅ **REST APIs** - Modern web development  
✅ **Security** - BCrypt encryption  
✅ **Layered Architecture** - Professional design  
✅ **Admin Panel** - Bank management features  
✅ **Transaction History** - Audit trail  
✅ **Exception Handling** - Proper error management

---

## 📂 File Structure

```
data/
├── users.json         # User accounts
├── wallets.json       # Wallet balances
└── transactions.json  # All transactions

src/main/java/
├── controller/        # REST endpoints
│   ├── AuthController
│   ├── WalletController
│   └── AdminController
├── service/           # Business logic
│   ├── AuthService
│   ├── WalletService
│   ├── AdminService
│   └── FileStorageService  ⭐ FILE HANDLING
├── repository/        # Data access
│   ├── UserRepository
│   ├── WalletRepository
│   └── TransactionRepository
├── model/            # Data classes
│   ├── User
│   ├── Wallet
│   └── Transaction
└── config/           # Configuration
    ├── SecurityConfig
    └── SessionAuthenticationFilter
```

---

## 🚀 How to Run & Demo

### **Start Application:**

```bash
.\mvnw.cmd spring-boot:run
```

### **Test in Postman:**

**1. Register User:**

```
POST http://localhost:8080/api/auth/register
Body:
{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
}
```

**2. Login:**

```
POST http://localhost:8080/api/auth/login
Body:
{
    "username": "john",
    "password": "password123"
}
```

(Save the session cookie)

**3. Deposit:**

```
POST http://localhost:8080/api/wallet/deposit
Body:
{
    "amount": 1000.00
}
```

**4. Check Balance:**

```
GET http://localhost:8080/api/wallet/balance
```

**5. View History:**

```
GET http://localhost:8080/api/wallet/history
```

---

## 💡 Tips for VIVA

### **Do's:**

✅ Start with project overview  
✅ Emphasize file handling (main requirement)  
✅ Show working demo  
✅ Explain architecture clearly  
✅ Open JSON files to show stored data  
✅ Mention admin features for bank management

### **Don'ts:**

❌ Don't say "I don't know"  
❌ Don't get confused by technical jargon  
❌ Don't skip the demo  
❌ Don't forget to mention file handling

### **If Stuck:**

- "Let me show you in the code..."
- "I can demonstrate this with Postman..."
- "The concept is similar to..."

---

## 🎓 Key Points to Emphasize

1. **File Handling** - "We use FileStorageService with Jackson ObjectMapper"
2. **Spring Boot** - "Simplifies development with auto-configuration"
3. **REST API** - "Client-server communication with JSON"
4. **Architecture** - "Layered: Controller → Service → Repository"
5. **Security** - "BCrypt for passwords, session-based auth"
6. **Admin Features** - "Essential for bank management and oversight"

---

## ✅ Final Checklist Before VIVA

- [ ] Application runs successfully
- [ ] Can register and login
- [ ] Can deposit/withdraw/transfer
- [ ] JSON files show stored data
- [ ] Know FileStorageService code
- [ ] Understand architecture diagram
- [ ] Postman collection ready
- [ ] Can explain admin features
- [ ] Confident about main concepts

---

## 🎯 Expected Marks Breakdown

| Component      | Marks | Your Preparation |
| -------------- | ----- | ---------------- |
| File Handling  | 35%   | ⭐⭐⭐⭐⭐       |
| Spring Boot    | 20%   | ⭐⭐⭐⭐         |
| Working Demo   | 15%   | ⭐⭐⭐⭐         |
| Architecture   | 10%   | ⭐⭐⭐⭐         |
| REST API       | 10%   | ⭐⭐⭐⭐         |
| Admin Features | 10%   | ⭐⭐⭐⭐⭐       |

**Total Expected:** 90-95% ✅

---

## 📞 Emergency Answers

**"What if database was better?"**

> "Yes, but we used file handling as per course requirement to demonstrate Java I/O operations."

**"Code is too complex?"**

> "We follow industry-standard practices. The layered architecture makes it maintainable and scalable."

**"Why Spring Boot?"**

> "It's the most popular Java framework for web development, with excellent community support and built-in features."

---

**YOU ARE READY! Good luck with your VIVA! 🎉**
