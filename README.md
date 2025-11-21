# 🏦 Digital Banking System

A secure and feature-rich digital banking application built with **Spring Boot** and **File Handling** (JSON-based storage). This project demonstrates modern banking operations including wallet management, money transfers, and administrative controls.

## 🌟 Features

### For Users
- 👤 **User Registration & Login** - Secure authentication with BCrypt password encryption
- 💰 **Wallet Management** - Personal digital wallet with unique wallet code
- 💵 **Deposit Money** - Add funds to your wallet
- 💸 **Withdraw Money** - Withdraw funds from your wallet
- 🔄 **Transfer Money** - Send money to other users using their wallet code
- 📜 **Transaction History** - View all your transactions

### For Administrators
- 👥 **View All Users** - Monitor all registered users in the system
- 📊 **View All Transactions** - Track all transactions across the platform
- 🏦 **Bank Transfers** - Direct deposits to user wallets from bank

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 4.0.0
- **Language**: Java 21
- **Security**: Spring Security with BCrypt
- **Data Storage**: JSON File Handling (using Jackson)
- **Session Management**: HttpSession
- **Build Tool**: Maven
- **Server**: Embedded Tomcat

## 📁 Project Structure

```
digital-banking-system/
├── src/main/java/com/spring_project/digital_banking_system/
│   ├── config/              # Security & Session configuration
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Custom exceptions & handlers
│   ├── model/               # Entity classes (User, Wallet, Transaction)
│   ├── repository/          # Data access layer with file operations
│   └── service/             # Business logic
├── src/main/resources/
│   └── application.properties
├── data/                    # JSON file storage
│   ├── users.json
│   ├── wallets.json
│   └── transactions.json
└── pom.xml
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd digital-banking-system
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080`
   - Test with Postman or any REST client

## 📡 API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login user
- `POST /auth/logout` - Logout user

### Wallet Operations (User)
- `GET /api/wallet` - Get wallet details
- `POST /api/wallet/deposit` - Deposit money
- `POST /api/wallet/withdraw` - Withdraw money
- `POST /api/wallet/transfer` - Transfer to another user
- `GET /api/wallet/transactions` - View transaction history

### Admin Operations
- `GET /api/admin/users` - View all users
- `GET /api/admin/transactions` - View all transactions
- `POST /api/admin/bank-transfer` - Bank transfer to user wallet

## 🔐 Security Features

- **Password Encryption**: BCrypt hashing algorithm
- **Role-Based Access**: USER and ADMIN roles
- **Session Management**: Secure session handling with HttpSession
- **Authentication Filter**: Custom filter for session-based authentication
- **Authorization**: Protected endpoints based on user roles

## 💾 Data Storage

This project uses **JSON file-based storage** instead of traditional databases:
- **users.json** - Stores user account information
- **wallets.json** - Stores wallet details and balances
- **transactions.json** - Stores transaction records

Files are automatically created in the `data/` directory on first run.

## 📝 Sample API Requests

### Register User
```json
POST /auth/register
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```json
POST /auth/login
{
  "username": "john_doe",
  "password": "password123"
}
```

### Transfer Money
```json
POST /api/wallet/transfer
{
  "toWalletCode": "WALLET123456",
  "amount": 500.00
}
```

## 👨‍💼 Default Admin Access

- **Username**: `admin`
- **Password**: `admin123`
- **Master Key**: `BANK_MASTER_KEY_2024`

The admin account is automatically created on first run.

## 🎯 Key Learning Points

This project demonstrates:
- ✅ REST API development with Spring Boot
- ✅ File handling with JSON serialization/deserialization
- ✅ Spring Security implementation
- ✅ Session management
- ✅ Role-based authorization
- ✅ Exception handling
- ✅ DTO pattern usage
- ✅ Service layer architecture

## 📚 Additional Documentation

- `FILE_HANDLING_IMPLEMENTATION.md` - Technical details of file storage system
- `VIVA_PREPARATION_GUIDE.md` - Q&A guide for project presentation

## 🤝 Contributing

This is an educational project. Feel free to fork and enhance it with additional features.

## 📄 License

This project is created for educational purposes.

---

**Built with ❤️ using Spring Boot**
