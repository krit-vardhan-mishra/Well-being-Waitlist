package com.wellbeing_waitlist;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    private final MaxHeapCustom maxHeapCustom;
    private final DatabaseOperations databaseOperations;

    public PatientService(MaxHeapCustom maxHeapCustom, DatabaseOperations databaseOperations) {
        this.maxHeapCustom = maxHeapCustom;
        this.databaseOperations = databaseOperations;
    }

    public void addPatient(Patient patient) {
        System.out.println("Adding patient to Max Heap and Database: " + patient.getName());
        maxHeapCustom.insert(patient);
        databaseOperations.insertPatients(List.of(patient));
        System.out.println("Patient successfully inserted in to Database and Max Heap.");
    }

    public List<Patient> markAsCured(Long id) {
        return databaseOperations.getAllPatients();
    }
}

