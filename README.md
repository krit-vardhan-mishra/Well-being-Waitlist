**Well-being Waitlist**

Well-being Waitlist is a hospital portal designed to efficiently manage patient checkups by prioritizing them based on emergency levels. The system integrates AI to assess patient conditions and dynamically updates priority levels for optimal medical attention.

**Features:**
AI-Based Emergency Assessment: Uses a Python AI model (transformers library) to generate an emergency level based on the patient's described problem.
Custom Max Heap Implementation: Patients are stored in a max heap, ensuring that those with the highest emergency levels are attended first.
Dynamic Priority Update: Each time a patient is removed for a checkup, the remaining patients' emergency levels increase by 5.
Spring Boot API: Handles database operations and integrates with the AI model for emergency level calculation.
MySQL Database: Stores patient details, including name, age, gender, problem description, and emergency level.

**Technologies Used:**
Backend: Java (Spring Boot)
Database: MySQL
AI Model: Python (transformers library)
Data Structure: Custom Max Heap


**Project Structure:**
/src/main/java/com/wellbeingwaitlist
  ├── config  
  │   ├── AppConfig.java  
  ├── database  
  │   ├── DatabaseConnection.java  
  │   ├── DatabaseOperations.java  
  ├── heap  
  │   ├── MaxHeapCustom.java  
  ├── model  
  │   ├── Patient.java  
  ├── controller  
  │   ├── PatientController.java  
  ├── repository  
  │   ├── PatientRepository.java  
  ├── service  
  │   ├── PatientService.java  
  ├── WellbeingWaitlistApplication.java

**Installation & Setup:**
1. Clone the repository:
git clohttps://github.com/krit-vardhan-mishra/Well-being-Waitlist.git
cd wellbeing-waitlist

2. Set up MySQL database:
Create a database and update connection details in DatabaseConnection.java.

3. Install dependencies and run the Spring Boot application:
mvn clean install  
mvn spring-boot:run

4. Start the AI service (Python script for emergency calculation).
API Endpoints
Add a patient: POST /patients
Get all patients: GET /patients
Remove highest priority patient: DELETE /patients/top
Update emergency levels: Automatically adjusted on each removal.

**Future Improvements:**
Implement a web UI for patient management.
Enhance AI model for more accurate emergency level predictions.
Optimize database operations for better performance.
