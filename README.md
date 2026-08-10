# 🚀 IssuePulse

IssuePulse is a full-stack Complaint Management System that enables users to register complaints, track their progress, and receive updates, while providing administrators with powerful tools to manage, analyze, and resolve issues efficiently.

---

## 📌 Features

### 👤 User
- User Registration & Login
- Secure JWT Authentication
- Create and Submit Complaints
- Upload Images as Evidence
- Track Complaint Status
- View Complaint History

### 🛡️ Admin
- Admin Dashboard
- Manage All Complaints
- Update Complaint Status
- Respond to User Complaints
- Analytics Dashboard
- Complaint Timeline
- Fraud Detection

---

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT Authentication
- Maven

### Frontend
- React
- Vite
- React Router
- Axios

### Database
- MySQL

---

## 📂 Project Structure

```
IssuePulse/
│
├── issuepulse-backend/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── ...
│
└── README.md
```

---

## ⚙️ Installation

### Clone the Repository

```bash
git clone https://github.com/your-username/IssuePulse.git
cd IssuePulse
```

### Backend

```bash
cd issuepulse-backend
```

Configure MySQL in `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/issuepulse
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Run the backend

```bash
mvn spring-boot:run
```

---

### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## 🔐 Authentication

IssuePulse uses **JWT (JSON Web Token)** authentication.

- User Login
- Role-based Authorization
- Protected APIs
- Secure Access

---

## 📊 Future Enhancements

- Email Notifications
- AI-based Complaint Classification
- Mobile Application
- Real-time Notifications
- Complaint Heatmaps
- Multi-language Support

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Developer

**Mohammed Azam**
B.Tech (Information Technology)

If you found this project useful, don't forget to ⭐ the repository!
