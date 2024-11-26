package com.wellbeing_waitlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Fetches a list of patients based on their 'cured' status
    List<Patient> findByCured(boolean cured);

    // Marks a patient as cured by updating the 'cured' column in the database for a specific patient ID
    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.cured = true WHERE p.id = :id")
    void markAsCured(@Param("id") Long id);
}
