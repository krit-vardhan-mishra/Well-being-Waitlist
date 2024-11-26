package com.wellbeing_waitlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String gender;
    private String problem;
    private int emergency_level;
    Timestamp arrivalTime;
    private boolean cured;

    public Patient() {}

    public Patient (String name, int age, String gender, String problem) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.problem = problem;
        this.arrivalTime = new Timestamp(System.currentTimeMillis());
        this.emergency_level = calculateEmergencyLevel(); // Calculating using python code
    }

    private int calculateEmergencyLevel() {
        try {
            // Get the Python script path from environment variable or use default
            String pydir = "PYTHON_SCRIPT_PATH";

            // Build the process to run the Python script
            ProcessBuilder pb = new ProcessBuilder("python", pydir, this.problem);

            // Set the working directory
            pb.directory(new File("PYTHON_SCRIPT_FOLDER_PATH"));

            // Start the process
            Process p = pb.start();

            // Read the output from the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream())); // Capture error stream

            String strScore = reader.readLine(); // Get the first line of output
            reader.close();

            // Capture any errors
            StringBuilder errorMessage = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorMessage.append(errorLine);
            }
            errorReader.close();

            // Wait for the process to complete
            int exitCode = p.waitFor();

            // Check for errors
            if (errorMessage.length() > 0 || exitCode != 0) {
                System.err.println("Error from Python script: " + errorMessage.toString());
                return -1; // Handle the error accordingly
            }

            // Parse and return the emergency level score
            return Integer.parseInt(strScore.trim());

        } catch (IOException | InterruptedException | NumberFormatException e) {
            e.printStackTrace();
            return -1; // Return -1 or handle the error appropriately
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public int getEmergencyLevel() {
        return emergency_level;
    }

    public void setEmergencyLevel(int newLevel) {
        this.emergency_level = newLevel;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isCured() {
        return cured;
    }

    public void setCured(boolean cured) {
        this.cured = cured;
    }
    
    @Override
    public String toString() {
        return "[" +
            "Id: " + id +
            ", Name: " + name +
            ", Age: " + age +
            ", Gender: " + gender +
            ", Problem: " + problem +
            ", Arrival Time: " + arrivalTime +
            ", Emergency Level: " + emergency_level +
            "]";
    }

}