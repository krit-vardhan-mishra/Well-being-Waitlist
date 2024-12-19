package com.wellbeing_waitlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// This files is made to perform the operation on the Database to insert, update and delete data from the Database
@Service
public class DatabaseOperations {

    // First create a database in mysql and in it create a table with the description given in description.png file in the main folder.

    // Function to insert data on the database
    @Transactional
    public void insertPatients(List<Patient> patients) {
        String query = "INSERT INTO patient (name, age, gender, problem, emergency_level) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

                for (Patient patient : patients) {
                    statement.setString(1, patient.getName());
                    statement.setInt(2, patient.getAge());
                    statement.setString(3, patient.getGender());
                    statement.setString(4, patient.getProblem());
                    statement.setInt(5, patient.getEmergencyLevel());
                    statement.addBatch();
                }

                int[] updateCounts = statement.executeBatch();
                System.out.println("Inserted patients into database: " + updateCounts.length);
            } catch (SQLException e) {
                System.out.println("Error inserting patients into database: " + e.getMessage());
                e.printStackTrace();
            }
    }

    // Function to delete (update) data on the database
    public static void deleteFromDatabase(String name) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE patient SET cured = TRUE WHERE NAME = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to update details of user
    public void saveOrUpdatePatient(Patient patient) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO patient (name, age, gender, problem, emergency_level, cured) " + "VALUES (?, ?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE emergency_level = VALUES(emergency_level), cured = VALUES(cured)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, patient.getName());
                statement.setInt(2, patient.getAge());
                statement.setString(3, patient.getGender());
                statement.setString(4, patient.getProblem());
                statement.setInt(5, patient.getEmergencyLevel());
                statement.setBoolean(6, false);
                statement.executeUpdate();
            }
    
        } catch (SQLException e) {
        }
    }

    // Function helps to retrieve data from database
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patient WHERE cured = false ORDER BY emergency_level DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Patient patient = new Patient(
                    resultSet.getString("name"),
                    resultSet.getInt("age"),
                    resultSet.getString("gender"),
                    resultSet.getString("problem")
                );
                patient.setId(resultSet.getLong("id"));
                patient.setArrivalTime(resultSet.getTimestamp("arrival_time"));
                patient.setEmergencyLevel(resultSet.getInt("emergency_level"));
                
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    // Function to retrieve data from the database through id
    public Patient getPatientById(Long id) {
        Patient patient = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM patient WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    patient = new Patient(
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("gender"),
                        resultSet.getString("problem")
                    );
                    patient.setId(resultSet.getLong("id"));
                    patient.setArrivalTime(resultSet.getTimestamp("arrival_time"));
                    patient.setEmergencyLevel(resultSet.getInt("emergency_level"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    // Function of retrieving data from the database for admin
    public List<Patient> getAllPatientsAdmin() {
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patient ORDER BY emergency_level DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("Extracting all the data from the database");
            
            while (resultSet.next()) {
                Patient patient = new Patient(
                    resultSet.getString("name"),
                    resultSet.getInt("age"),
                    resultSet.getString("gender"),
                    resultSet.getString("problem")
                );
                patient.setId(resultSet.getLong("id"));
                patient.setArrivalTime(resultSet.getTimestamp("arrival_time"));
                patient.setEmergencyLevel(resultSet.getInt("emergency_level"));
                
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }
}
