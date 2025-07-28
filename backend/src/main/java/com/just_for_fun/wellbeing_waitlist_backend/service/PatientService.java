package com.just_for_fun.wellbeing_waitlist_backend.service;

import java.util.List;
import com.just_for_fun.wellbeing_waitlist_backend.entity.Patient;

public interface PatientService {
    public void addPatient(Patient patient);
    public List<Patient> findByCured(boolean cured);
    public List<Patient> findAll();
}