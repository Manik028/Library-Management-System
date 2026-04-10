# Library-Management-System
A project build with JavaFX

Library Management System is a feature-rich desktop application. Built on JavaFX with a local SQLite database, it provides a secure and interactive platform for both librarians & users.


A modern desktop-based Library Management System built using **JavaFX** 
This application provides an efficient way to manage books, users, and library operations with a clean and interactive UI.

---

## 🚀 Features

- 🔐 User Authentication (Different panel for both User and Admin)(Login System)
- 📊 Dashboard Interface
- 📖 Book Management (Add, Update, Delete)
- Quiz Exam System
- 👤 User Management
- 🔍 Search Functionality
- 📦 Organized UI using JavaFX

---

## 🛠️ Technologies Used

- **Java**
- **JavaFX**
- **Maven**
- **SQLite** (Lightweight embedded database)
- **FXML (Scene Builder)**

---

## ⚙️ Prerequisites

Before running this project, make sure you have:

- ☕ Java JDK 11 or higher
- 🧰 **Maven installed** (or use included `mvnw`)
- 🖥️ **IDE** (IntelliJ IDEA / Eclipse / VS Code)
- 🎨 **JavaFX SDK configured**

> ✅ No external database installation required (uses SQLite)
> ### 4. Database Setup

- This project uses **SQLite**, a lightweight embedded database.
- No separate installation is required.
- The database file (`.db`) is automatically created or included in the project.

> ✅ Make sure the database file path is correctly configured in your code.

---

## 📥 Installation & Setup

### 1. Clone the Repository
In bash

git clone https://github.com/Manik028/Library-Management-System.git
cd Library-Management-System

### 2. Open in IDE
Open the project in IntelliJ IDEA / Eclipse
Make sure Maven dependencies are loaded

### 3. Configure JavaFX
Add JavaFX SDK to your project
Set VM options (if needed):
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml

Finally run the application from Launcer.java

## 📂 Project Structure


Library-Management-System/
│
├── src/
│ ├── main/
│ │ ├── java/ # Java source code
│ │ └── resources/ # FXML, CSS, images
│
├── database/ # SQLite database file
├── images/ # Screenshots
├── pom.xml # Maven configuration
├── mvnw # Maven wrapper
└── README.md






