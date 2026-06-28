# 📚 Library Management System

## Overview

A JavaFX desktop application for managing a small library catalogue. Users log in with a username and password, then interact with a MySQL-backed book inventory through a clean multi-scene GUI. The app supports searching, buying, renting, selling, and returning books, and includes a dark mode toggle.

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 17                           |
| UI Framework | JavaFX 21                         |
| Database     | MySQL / MariaDB                   |
| DB Driver    | MySQL Connector/J 9.x             |
| Build Tool   | Apache Maven                      |
| Tests        | JUnit 5                           |

---

## Project Structure

```
library-app/
├── pom.xml                                      Maven build file
├── sql/
│   └── library.sql                              DB schema + seed data
├── docs/
│   └── class-diagram.png                        UML class diagram
└── src/
    ├── main/java/library/
    │   ├── LibraryApplication.java              Main JavaFX app + all scenes
    │   ├── Book.java                            Model class (JavaBean for TableView)
    │   └── DatabaseManager.java                MySQL CRUD operations
    └── test/java/library/
        └── LibraryAppTest.java                  JUnit 5 unit tests
```

---

## Screens

| Scene        | Description                                                              |
|--------------|--------------------------------------------------------------------------|
| Login        | Username + password form, validated against the `user` table             |
| Home         | Navigation hub with buttons to all screens + logout                      |
| Dashboard    | Search book, Buy, Rent, Sell (dialog), Return — all updating the DB      |
| All Books    | TableView showing name, owner, quantity, price from the `book` table     |
| Profile      | Displays current user info (username, role, member since)                |
| Settings     | Toggle between light and dark themes                                     |

---

## Database Schema

Two tables in the `library` database:

```sql
-- Users (authentication)
CREATE TABLE user (
    username VARCHAR(50)  PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);

-- Book catalogue
CREATE TABLE book (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    bookname VARCHAR(150) NOT NULL,
    price    DOUBLE       NOT NULL,
    quantity INT          NOT NULL,
    owner    VARCHAR(100) NOT NULL
);
```

---

## Key Code Segments

### DatabaseManager — connection & credential check
```java
public Connection connect() throws SQLException {
    return DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/library", "root", "");
}

public boolean checkCredentials(String username, String password) {
    String sql = "SELECT 1 FROM user WHERE username = ? AND password = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username); stmt.setString(2, password);
        return stmt.executeQuery().next();
    } catch (SQLException e) { return false; }
}
```

### Book — JavaBean model for TableView binding
```java
public class Book {
    private String name, owner;
    private int quantity;
    private double price;
    // PropertyValueFactory requires standard getters
    public String getName()    { return name; }
    public String getOwner()   { return owner; }
    public int    getQuantity(){ return quantity; }
    public double getPrice()   { return price; }
}
```

### LibraryApplication — scene switching pattern
```java
private void showHomeScene() {
    // build layout, wrap in Scene, apply theme, set on stage
    currentScene = new Scene(layout, 500, 440);
    applyTheme(currentScene);
    primaryStage.setScene(currentScene);
}
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL running locally on port 3306

### Setup
```bash
# 1. Create the database
mysql -u root -p < sql/library.sql

# 2. Run the app
mvn javafx:run

# 3. Run tests
mvn test
```

### Default credentials
| Username | Password |
|----------|----------|
| admin    | 123456   |
| staff    | 123456   |
| member   | 123456   |

> To change DB credentials, edit `DB_URL`, `DB_USER`, `DB_PASS` in `DatabaseManager.java`.
