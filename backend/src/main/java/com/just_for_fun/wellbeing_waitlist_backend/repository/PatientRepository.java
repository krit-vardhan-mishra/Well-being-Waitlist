package com.just_for_fun.wellbeing_waitlist_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.just_for_fun.wellbeing_waitlist_backend.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByNameAndAgeAndGenderAndProblem(String name, int age, String gender, String problem);
    List<Patient> findByCured(boolean cured);
}