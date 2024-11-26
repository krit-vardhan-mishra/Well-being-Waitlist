package com.wellbeing_waitlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class MaxHeapCustom {

    private final PriorityQueue<Patient> maxHeap;
    private final HashMap<String, Patient> patientMap; 
    private final ScheduledExecutorService scheduler; 
    private final PatientRepository patientRepository;

    public MaxHeapCustom(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        maxHeap = new PriorityQueue<>((p1, p2) -> Integer.compare(p2.getEmergencyLevel(), p1.getEmergencyLevel()));
        patientMap = new HashMap<>();
        scheduler = Executors.newScheduledThreadPool(1);
        startAutoExtract();
    }

    private String generateUniqueKey(Patient patient) {
        return patient.getName() + "-" + patient.getAge() + "-" + patient.getGender() + "-" + patient.getProblem();
    }

    public void insert(Patient patient) {
        String uniqueKey = generateUniqueKey(patient);
        if (!patientMap.containsKey(uniqueKey)) {
            maxHeap.add(patient);
            patientMap.put(uniqueKey, patient);
            System.out.println("Patient added to Max Heap: " + patient.getName());
        } else {
            System.out.println("Patient with Name '" + patient.getName() + "' already exists.");
        }
    }

    public Patient extractMax() {
        Patient maxPatient = maxHeap.poll(); 
        if (maxPatient != null) {
            String uniqueKey = generateUniqueKey(maxPatient);
            patientMap.remove(uniqueKey);
            System.out.println("Extracted max patient from Max Heap: " + maxPatient.getName());

            List<Patient> updatedPatients = new ArrayList<>();
            while (!maxHeap.isEmpty()) {
                Patient patient = maxHeap.poll();
                increaseEmergencyLevel(patient);
                updatedPatients.add(patient);
            }
            
            for (Patient patient : updatedPatients) {
                maxHeap.add(patient);
                patientRepository.save(patient);
            }

            patientRepository.save(maxPatient);
            System.out.println("Marked patient as cured in database: " + maxPatient.getName());
        }
        return maxPatient;
    }

    private void increaseEmergencyLevel(Patient patient) {
        int currentLevel = patient.getEmergencyLevel();
        int age = patient.getAge();
        int increaseFactor = 5;

        if (currentLevel > 60) {
            increaseFactor += 3;
        }

        if (age > 60) {
            increaseFactor += 2;
        }

        int increasedLevel = currentLevel + increaseFactor;
        patient.setEmergencyLevel(increasedLevel);
    }

    public boolean contains(Patient patient) {
        String uniqueKey = generateUniqueKey(patient);
        return patientMap.containsKey(uniqueKey);
    }

    public List<Patient> getPatientsInHeap() {
        return new ArrayList<>(maxHeap);
    }

    public final void startAutoExtract() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Auto-extracting patient with highest emergency level...");
            Patient maxPatient = extractMax();
            if (maxPatient != null) {
                System.out.println("Auto-extracted patient: " + maxPatient.getName());
                DatabaseOperations.deleteFromDatabase(maxPatient.getName());
            } else {
                System.out.println("No patients to extract.");
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    public void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}

