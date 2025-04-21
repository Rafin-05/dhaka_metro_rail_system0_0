# Dhaka Metro Rail Ticketing System

**A Java Swing application for metro rail ticketing, supporting one-time tickets and rechargeable MRT/Rapid passes, connected to a MySQL (XAMPP) backend.**

---

## ✨ Features

- **Buy One-Time Pass:** Purchase tickets between any two metro stations.
- **Apply for Pass:** Register for MRT/Rapid Pass using your NID, with automated card number generation and bonus balance.
- **Use Pass:** Pay for journeys at a discounted fare using your card.
- **Recharge Pass:** Add money to your MRT/Rapid Pass balance.
- **Fare Calculator:** Instantly view regular and discounted fares between stations.
- **Change Calculation:** System computes and displays change/notes for ticket purchases.

---

## 📦 Project Structure

metro-rail-ticketing/
│
├── src/
│ └── MetroRail.java # Main application code
│
├── sql/
│ └── metro1.sql # Database schema and sample data
│
├── README.md # This file
└── .gitignore

sql_more

Copy

---

## 🚀 Getting Started

### 1. Requirements

- Java 8 or higher
- IntelliJ IDEA (or similar Java IDE)
- [XAMPP](https://www.apachefriends.org/) (for MySQL)
- MySQL JDBC Driver (`mysql-connector-java`)

### 2. Setup Database

1. **Start** XAMPP’s MySQL service.
2. **Open** [phpMyAdmin](http://localhost/phpmyadmin/).
3. **Create** a database named `metro1`.
4. **Import** the SQL schema:
    - Go to the `Import` tab in phpMyAdmin.
    - Select `sql/metro1.sql` from your project.
    - Click **Go**.

### 3. Configure JDBC in IntelliJ

- Add `mysql-connector-java` to your project libraries.
- The default DB connection in `MetroRail.java` is:
    ```java
    private static final String DB_URL = "jdbc:mysql://localhost:3306/metro1";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    ```
- Adjust `DB_USER` and `DB_PASS` if your setup differs.

### 4. Run the Application

- Open the project in IntelliJ.
- Run `MetroRail.java`.
- Use the GUI to interact with the system.

---

## 🗃️ Database Structure

- **Tables:**
    - `stations` — List of stations
    - `fares` — Fares between station pairs
    - `one_time_pass` — Records of one-time tickets
    - `mrt_rapid_pass_users` — Registered card users

See `sql/metro.sql` for schema details.

---

## 💡 Notes

- The application does **not** upload your database itself, only the SQL schema and sample data.
- Always keep your database credentials secure; do not push sensitive passwords to public repos.

---

## 🛠️ Contributing

Pull requests and bug reports are welcome!  
Please open an [issue](https://github.com/Rafin-05/dhaka_metro_rail_system0_0/issues) for major changes or suggestions.



---

## 👤 Author

- [Rafin](https://github.com/Rafin-05)

---

