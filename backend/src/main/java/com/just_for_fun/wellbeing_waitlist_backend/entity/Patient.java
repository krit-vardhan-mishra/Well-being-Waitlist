package com.just_for_fun.wellbeing_waitlist_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient")
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name")
    private String name;

    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 150, message = "Age must be a valid number")
    @Column(name = "age")
    private int age;

    @NotBlank(message = "Gender is mandatory")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    @Column(name = "gender")
    private String gender;

    @NotBlank(message = "Must write your problem")
    @Size(min = 5, max = 200, message = "Problem description must be between 5 and 200 characters")
    @Column(name = "problem")
    private String problem;

    @Column(name = "emergency_level")
    private int emergencyLevel;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "cured", nullable = false)
    private boolean cured;

    public Patient() {
    }

    public Patient(String name, int age, String gender, String problem) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.problem = problem;
        this.arrivalTime = LocalDateTime.now();
        this.cured = false;
    }
}