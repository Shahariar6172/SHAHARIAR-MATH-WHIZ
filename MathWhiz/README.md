# SHAHARIAR – MATH WHIZ

A web-based mathematical game built with **Java Servlet, JSP and JDBC (MySQL)**.
Players enter their name, answer four questions (Prime, Modulus, 2 × Trigonometry), get a score out of 10, and the result is saved to a database. The top 10 scores are shown on a leaderboard.

**Student:** Al-Shahariar · **ID:** IT-24057 · Dept. of ICT, MBSTU

## Project Objectives
- Write reusable Java methods for mathematical calculations (`isPrime`, `calculateMod`, `calculateSin/Cos/Tan`).
- Evaluate every answer with real Java calculations — no stored answers.
- Process requests on the server with a Servlet.
- Save and read data using JDBC and `PreparedStatement`.
- Show a sorted Top 10 leaderboard.

## Technologies Used
- Java 17+
- Jakarta Servlet 6.0 + JSP + JSTL 3.0
- Apache Tomcat 10.1
- MySQL 8 + MySQL Connector/J (JDBC)
- Maven
- JUnit 5 (testing)
- VS Code

## Software Requirements
| Software | Version |
|---|---|
| JDK | 17 or newer |
| Apache Maven | 3.8+ |
| Apache Tomcat | 10.1.x (must be 10+, because the project uses `jakarta.*`) |
| MySQL Server | 8.x |
| VS Code | with *Extension Pack for Java* and *Community Server Connectors* |

## Project Structure
```
MathWhiz/
├── pom.xml
├── database/math_whiz_db.sql
├── src/main/java/
│   ├── controller/GameServlet.java   # /start, /submit, /leaderboard
│   ├── model/GameScore.java          # one row of game_scores
│   ├── model/Question.java           # a question + answer checking
│   ├── service/MathService.java      # all math methods
│   └── dao/ScoreDAO.java             # JDBC INSERT / SELECT
├── src/main/webapp/
│   ├── index.jsp  game.jsp  result.jsp  leaderboard.jsp
│   └── css/style.css
├── src/test/java/service/MathServiceTest.java
└── README.md
```

## Database Configuration
Edit the connection details in `src/main/java/dao/ScoreDAO.java`:
```java
private static final String URL  = "jdbc:mysql://localhost:3306/math_whiz_db";
private static final String USER = "root";
private static final String PASS = "your_password";
```

## Database Creation SQL
```sql
CREATE DATABASE IF NOT EXISTS math_whiz_db;
USE math_whiz_db;

CREATE TABLE IF NOT EXISTS game_scores (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(100) NOT NULL,
    score       INT          NOT NULL,
    difficulty  VARCHAR(20)  NOT NULL DEFAULT 'NORMAL',
    played_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Steps to Run the Application
1. Create the database: `mysql -u root -p < database/math_whiz_db.sql`
2. Set your MySQL password in `ScoreDAO.java`.
3. Run the tests: `mvn test`
4. Build the WAR: `mvn clean package` → creates `target/MathWhiz.war`
5. Deploy — either:
   - copy `target/MathWhiz.war` into `TOMCAT_HOME/webapps/` and start Tomcat (`bin/startup.bat` on Windows, `bin/startup.sh` on Linux/macOS), **or**
   - in VS Code, *Servers* panel → add Tomcat 10.1 → right-click server → *Add Deployment* → choose `target/MathWhiz.war` → *Start Server*.
6. Open <http://localhost:8080/MathWhiz/>

## Game Rules and Scoring System
| Question | Area | Marks |
|---|---|---|
| Q1 | Prime Number (1 = Prime, 0 = Not Prime) | 2 |
| Q2 | Modulus (integers only) | 2 |
| Q3 | Trigonometry (sin/cos/tan) | 3 |
| Q4 | Trigonometry (sin/cos/tan) | 3 |
| **Total** | | **10** |

- Numbers below 2 are not prime.
- Modulus divisor is never zero; `calculateMod(a, 0)` throws an error.
- Angles are in degrees and converted to radians with `Math.toRadians()`.
- `tan(90°)` / `tan(270°)` are undefined and are never asked.
- Decimal answers are accepted within a tolerance of **±0.01**.
- Difficulty is **NORMAL** and is stored with each score.
- Leaderboard: `ORDER BY score DESC, played_at ASC LIMIT 10`.

## Test Cases
| Test | Expected | Result |
|---|---|---|
| isPrime(7) | 1 | ✅ |
| isPrime(8) | 0 | ✅ |
| isPrime(1) | 0 | ✅ |
| 17 % 5 | 2 | ✅ |
| sin(30°) | 0.5 | ✅ |
| cos(60°) | 0.5 | ✅ |
| tan(45°) | 1.0 | ✅ |
| Divisor 0 | Validation error | ✅ |
| All answers correct | 10 marks | ✅ |
| Wrong answers | No marks | ✅ |

## Application Screenshots

### Welcome Page
![Welcome Page](screenshots/home.png)

### Game Page
![Game Page](screenshots/game.png)

### Result Page
![Result Page](screenshots/result.png)

### Leaderboard
![Leaderboard](screenshots/leaderboard.png)

## GitHub Project Directory
https://github.com/Shahariar6172/REPOSITORY_NAME/tree/main/MathWhiz
