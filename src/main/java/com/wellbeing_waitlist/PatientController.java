package com.wellbeing_waitlist;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// File for controlling the request from the web-page

@Controller
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DatabaseOperations databaseOperations;

    private static final String ADMIN_PASSWORD = System.getenv("DB_PASSWORD");      // Create a Environment Variable or enter the password directly for the admin access

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @GetMapping("/admin-login")
    public String showAdminLoginPage(Model model) {
        return "admin-login"; 
    }
    
    @PostMapping("/register")
    public String registerPatient(@RequestParam String name, @RequestParam int age, @RequestParam String gender, @RequestParam String problem, Model model) {
        try {
            System.out.println("Register patient: " + name);
            Patient patient = new Patient(name, age, gender, problem);
            patientService.addPatient(patient);
            return "redirect:/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to register patient. Please try again.");
            return "register";
        }
    }
    
    @GetMapping("/details")
    public String showPatientDetails(Model model) {
        List<Patient> patients = databaseOperations.getAllPatients();
        if (patients.isEmpty()) {
            model.addAttribute("message", "No patients available.");
            System.out.println("No patients available in the database.");
        } else {
            model.addAttribute("patients", patients);
            System.out.println("Fetched patients: " + patients);
        }
        return "details";
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Patient> patients = databaseOperations.getAllPatients();
        if (patients.isEmpty()) {
            model.addAttribute("message", "No patients available.");
        } else {
            model.addAttribute("patients", patients);
        }
        return "index";
    }

    @GetMapping("/api/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = databaseOperations.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/api/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = Optional.ofNullable(databaseOperations.getPatientById(id));
        if (patient != null) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Function for confirming the admin login details
    @PostMapping("/admin-login")
    public String validatePasswordAndShowDetails(@RequestParam("password") String password, Model model) {
        if (ADMIN_PASSWORD.equals(password)) {
            List<Patient> patients = databaseOperations.getAllPatientsAdmin();
            model.addAttribute("patients", patients);
            return "details";
        } else {
            model.addAttribute("errorMessage", "Incorrect Password. Access Denied.");
            return "admin-login";
        }
    }
    
}