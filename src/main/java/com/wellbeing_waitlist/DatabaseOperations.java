package com.wellbeing_waitlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DatabaseOperations {

    // first create a database in mysql and in it create a table with the description given in description.png file in the main folder.
    public void insertIntoDatabase(Patient patient) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO patient (name, age, gender, problem, emergency_level) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, patient.getName());
                statement.setInt(2, patient.getAge());
                statement.setString(3, patient.getGender());
                statement.setString(4, patient.getProblem());
                statement.setInt(5, patient.getEmergencyLevel());
                statement.executeUpdate();
                System.out.println("Inserted patient into database: " + patient.getName());
            }
        } catch (SQLException e) {
            System.out.println("Error inserting patient into database: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
