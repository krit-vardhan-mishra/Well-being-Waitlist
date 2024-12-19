package com.wellbeing_waitlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

// File for creating a Custom Max Heap

@Component
public class MaxHeapCustom {

    private final PriorityQueue<Patient> maxHeap;
    private final HashMap<String, Patient> patientMap; 
    private final ScheduledExecutorService scheduler; 
    private final DatabaseOperations databaseOperations;

    public MaxHeapCustom(DatabaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
        maxHeap = new PriorityQueue<>((p1, p2) -> Integer.compare(p2.getEmergencyLevel(), p1.getEmergencyLevel()));
        patientMap = new HashMap<>();
        scheduler = Executors.newScheduledThreadPool(1);
        startAutoExtract();
    }

    private String generateUniqueKey(Patient patient) {
        return patient.getName() + "-" + patient.getAge() + "-" + patient.getGender() + "-" + patient.getProblem();
    }

    // Function for inserting the data in max heap
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

    // Extracting max heap
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
                databaseOperations.saveOrUpdatePatient(patient);
            }

            DatabaseOperations.deleteFromDatabase(maxPatient.getName());
            System.out.println("Marked patient as cured in database: " + maxPatient.getName());
        }
        return maxPatient;
    }

    // Function to increase emergency level
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

    // Function to check if data already entered or not
    public boolean contains(Patient patient) {
        String uniqueKey = generateUniqueKey(patient);
        return patientMap.containsKey(uniqueKey);
    }

    public List<Patient> getPatientsInHeap() {
        return new ArrayList<>(maxHeap);
    }

    // Threading for auto extracting the data from the heap
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
        }, 0, 20, TimeUnit.SECONDS);
    }

    // Thread Scheduler
    public void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}

