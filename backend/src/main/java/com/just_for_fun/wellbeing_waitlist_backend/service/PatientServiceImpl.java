package com.just_for_fun.wellbeing_waitlist_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.just_for_fun.wellbeing_waitlist_backend.entity.Patient;
import com.just_for_fun.wellbeing_waitlist_backend.repository.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final EmergencyLevelServiceImpl emergencyLevelService;

    public PatientServiceImpl(PatientRepository patientRepository, EmergencyLevelServiceImpl emergencyLevelService) {
        this.patientRepository = patientRepository;
        this.emergencyLevelService = emergencyLevelService;
    }

    @Override
    public void addPatient(Patient patient) {
        Optional<Patient> existingPatient = patientRepository.findByNameAndAgeAndGenderAndProblem(patient.getName(), patient.getAge(),
                patient.getGender(), patient.getProblem());

        if (existingPatient.isPresent()) {
            throw new IllegalArgumentException("Patient already exists in the system.");
        }

        int emergencyLevel = emergencyLevelService.calculateEmergencyLevel(patient.getProblem());
        patient.setEmergencyLevel(emergencyLevel);
        patient.setArrivalTime(LocalDateTime.now());
        patient.setCured(false);
        patientRepository.save(patient);
    }

    @Override
    public List<Patient> findByCured(boolean cured) {
        return patientRepository.findByCured(cured);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

}
