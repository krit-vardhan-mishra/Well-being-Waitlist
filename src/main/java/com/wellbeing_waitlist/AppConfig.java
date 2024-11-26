package com.wellbeing_waitlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    private PatientRepository patientRepository;  
    
    @Bean
    public MaxHeapCustom maxHeapCustom() {
        return new MaxHeapCustom(patientRepository);
    }
}
