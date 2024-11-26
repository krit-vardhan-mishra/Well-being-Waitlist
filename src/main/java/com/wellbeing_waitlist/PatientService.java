package com.wellbeing_waitlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    private final MaxHeapCustom maxHeapCustom;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(MaxHeapCustom maxHeapCustom, PatientRepository patientRepository) {
        this.maxHeapCustom = maxHeapCustom;
        this.patientRepository = patientRepository;
    }

    public void addPatient(Patient patient) {
        System.out.println("Adding patient to Max Heap and Database: " + patient.getName());
        maxHeapCustom.insert(patient);
        patientRepository.save(patient);
        System.out.println("Patient successfully inserted in to Database and Max Heap.");
    }

    public void markAsCured(Long id) {
        patientRepository.markAsCured(id);
    }
}

