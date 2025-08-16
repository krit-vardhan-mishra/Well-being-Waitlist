# Well-being Waitlist

Well-being Waitlist is a hospital portal that prioritizes patient checkups using **precomputed emergency levels** (no LLM needed by default), making the system faster and more efficient.

---

## 🌍 Vision

Modern hospitals face the challenge of efficiently managing patient queues, especially in emergency situations. Traditional “first come, first served” systems fail to address critical cases quickly.  
**Well-being Waitlist solves this problem** by introducing a priority-based queue that ensures patients with the most urgent needs are always seen first.

This system is built with **hospital IT infrastructure in mind**:
- Runs **entirely on local hospital servers/computers**
- Keeps all **patient data within hospital premises** for compliance and privacy
- Provides **real-time updates across multiple terminals** within the hospital
- Works reliably even without internet connectivity
- Flexible for hospital staff to configure and adapt

Our vision is to enable hospitals to adopt a **secure, reliable, and intelligent queue management system** that improves patient care and reduces waiting time.

---

## 🏥 How Hospitals Could Use It

1. **Installation**  
   Hospital IT deploys the system on a local server/computer.  

2. **Database Setup**  
   A MySQL instance is configured with hospital security policies.  

3. **Network Access**  
   Staff terminals across the hospital connect to the system through the hospital’s secure intranet.  

4. **Queue Display**  
   Doctors, nurses, and receptionists can view and manage patient queues in real time.  

5. **Admin Control**  
   Admin staff log in with credentials to manage patients, update problems, and configure system rules.  

---

## 📋 Project Overview

- **System goal:** Dynamically manage patient queue by emergency priority.  
- **Updated flow:**  
  Users submit patient problem → mapped to **precomputed emergency level** → patient is queued in a max-heap.  
- On each **check-up**:  
  - Highest-priority patient is removed.  
  - Remaining patients’ emergency levels increase by a fixed increment (e.g. +5).  
- **Frontend updates in real time** to reflect current queue.  

---

## 🛠 Tech Stack

| Layer        | Tools & Frameworks                     |
|--------------|-----------------------------------------|
| Backend      | Spring Boot (Java), REST API           |
| Frontend     | React.js, Tailwind CSS                 |
| Data Store   | MySQL                                  |

---

## ⚙️ Requirements (Manual Setup)

Make sure the following are installed before running the project manually:

- **Java 17+** (for Spring Boot backend)  
- **Maven 3.6+** (for building the backend project)  
- **Node.js 18+ & npm/yarn** (for React frontend)  
- **MySQL 8+** (for database)  
- **Git** (for cloning the repository)  

---

## 🚀 Installation & Running Locally (Manual Setup)

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/wellbeing-waitlist.git
cd wellbeing-waitlist
````

### 2. Backend Setup (Spring Boot)

```bash
cd server
```

* Configure **application.properties** (inside `src/main/resources/`) with your MySQL settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wellbeing_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

* Run the backend with Maven:

```bash
mvn spring-boot:run
```

The backend will start at **[http://localhost:8080](http://localhost:8080)**.

---

### 3. Database Setup (MySQL)

* Create a database in MySQL:

```sql
CREATE DATABASE wellbeing_db;
```

* The backend will automatically create the required tables when it runs.

---

### 4. Frontend Setup (React + Tailwind)

```bash
cd client
npm install   # or yarn install
npm run dev   # or yarn dev
```

The frontend will start at **[http://localhost:5173](http://localhost:5173)**.

---

### 5. Access the App

* Open the frontend URL in your browser: [http://localhost:5173](http://localhost:5173)
* The frontend communicates with the backend at **[http://localhost:8080](http://localhost:8080)**
* Ensure both backend and frontend are running simultaneously

---

## 🐳 Docker Deployment (Recommended for Hospitals)

To simplify installation, you can use **Docker + Docker Compose**.
This way, hospitals don’t need to manually install Java, Node, or MySQL.

### 1. Requirements

* Install [Docker](https://www.docker.com/)
* Install [Docker Compose](https://docs.docker.com/compose/)

### 2. Clone the Repository

```bash
git clone https://github.com/your-username/wellbeing-waitlist.git
cd wellbeing-waitlist
```

### 3. Run with Docker Compose

```bash
docker-compose up --build
```

This will:

* Start a **MySQL container** with the database `wellbeing_db`
* Start the **Spring Boot backend** container
* Start the **React frontend** container

### 4. Access the App

* Frontend → [http://localhost:5173](http://localhost:5173)
* Backend → [http://localhost:8080](http://localhost:8080)
* MySQL DB → running inside container (accessible if needed)

### 5. Stop Containers

```bash
docker-compose down
```

---

## 📚 Documentation You Could Add

* **Hospital IT Setup Guide**
* **Network Configuration Requirements**
* **Database Security Recommendations**
* **Multi-terminal Setup Instructions**
* **Backup and Recovery Procedures**

---

## 🔮 Why This Matters

By focusing on **local deployment, privacy, and reliability**, this project goes beyond a generic web app.
It demonstrates understanding of **real-world healthcare IT constraints** and could realistically be deployed in hospitals today.

---

# SpringBoot-React

A full-stack project using **Spring Boot** for backend and **React.js + Tailwind CSS** for frontend.

```
✨ This might seem like a lot of work at first, but once everything is set up, you’ll appreciate how smooth and reliable the system runs.
```
