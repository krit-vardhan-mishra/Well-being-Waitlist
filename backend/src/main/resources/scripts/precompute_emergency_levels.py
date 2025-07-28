import sys
import json
import os
import warnings
import logging
import contextlib
from time import time
from transformers import pipeline

warnings.filterwarnings("ignore")
logging.getLogger("transformers").setLevel(logging.ERROR)

@contextlib.contextmanager
def suppress_stdout():
    with open(os.devnull, 'w') as devnull:
        with contextlib.redirect_stdout(devnull):
            yield

def load_classifier():
    try:
        MODEL_NAME = "typeform/mobilebert-uncased-mnli"
        with suppress_stdout():
            classifier = pipeline(
                "zero-shot-classification",
                model=MODEL_NAME,
                tokenizer=MODEL_NAME,
                device=-1
            )
        return classifier
    except Exception as e:
        print(f"Error loading classifier: {e}")
        return None

def calculate_emergency_level(problem: str, classifier) -> int:
    if not problem or not problem.strip():
        return 1

    if not classifier:
        return -1

    labels = [
        "very low urgency", "low urgency", 
        "medium urgency", "high urgency", 
        "very high urgency", "critical emergency"
    ]

    try:
        result = classifier(problem, labels, multi_label=False)
        weights = {
            "very low urgency": 10, "low urgency": 30,
            "medium urgency": 50, "high urgency": 70,
            "very high urgency": 90, "critical emergency": 98
        }
        score = 0
        for lbl, sc in zip(result["labels"], result["scores"]):
            score += weights.get(lbl, 0) * sc

        level = int(min(max(round(score), 1), 100))
        return level

    except Exception as e:
        print(f"Error calculating emergency level: {e}")
        return -1

def get_common_medical_problems():
    """Comprehensive list of common medical problems"""
    return [
        # Critical emergencies
        "heart attack", "cardiac arrest", "stroke", "severe bleeding", "unconscious", 
        "not breathing", "severe chest pain", "severe burns", "severe trauma",
        "anaphylactic shock", "severe allergic reaction", "choking", "overdose",
        
        # High urgency
        "chest pain", "difficulty breathing", "shortness of breath", "severe pain",
        "high fever", "broken bone", "fracture", "severe headache", "migraine",
        "vomiting blood", "blood in stool", "severe abdominal pain", "appendicitis",
        "kidney stones", "gallstones", "pneumonia", "asthma attack",
        
        # Medium-high urgency
        "fever", "high temperature", "persistent headache", "back pain",
        "severe nausea", "persistent vomiting", "dehydration", "food poisoning",
        "urinary tract infection", "ear infection", "eye infection", "rash",
        "allergic reaction", "sprain", "minor fracture", "cut requiring stitches",
        
        # Medium urgency
        "cough", "cold symptoms", "flu symptoms", "sore throat", "runny nose",
        "muscle pain", "joint pain", "arthritis", "minor headache", "diarrhea",
        "constipation", "acid reflux", "heartburn", "minor burn", "bruise",
        "insect bite", "minor cut", "scrape", "skin irritation",
        
        # Low urgency
        "routine checkup", "physical exam", "vaccination", "immunization",
        "prescription refill", "medication review", "blood pressure check",
        "diabetes checkup", "cholesterol check", "annual exam", "preventive care",
        "minor fatigue", "tiredness", "mild anxiety", "sleep issues",
        
        # Specific conditions
        "diabetes", "hypertension", "high blood pressure", "low blood pressure",
        "depression", "anxiety", "panic attack", "stress", "insomnia",
        "chronic fatigue", "fibromyalgia", "chronic pain", "back problems",
        "neck pain", "shoulder pain", "knee pain", "hip pain",
        
        # Women's health
        "pregnancy symptoms", "morning sickness", "menstrual cramps", 
        "irregular periods", "heavy bleeding", "pelvic pain",
        
        # Children's health
        "teething", "diaper rash", "cradle cap", "colic", "ear pain",
        "growing pains", "fever in child", "child not eating",
        
        # Respiratory
        "bronchitis", "sinusitis", "sinus infection", "laryngitis",
        "whooping cough", "tuberculosis", "lung infection",
        
        # Gastrointestinal
        "stomach ache", "indigestion", "bloating", "gas", "cramps",
        "irritable bowel syndrome", "gastritis", "ulcer", "hemorrhoids",
        
        # Skin conditions
        "eczema", "psoriasis", "acne", "warts", "moles", "skin cancer",
        "sunburn", "heat rash", "hives", "dermatitis",
        
        # Mental health
        "mood swings", "irritability", "concentration problems",
        "memory issues", "confusion", "dizziness", "vertigo",
        
        # Combinations and variations
        "severe headache with nausea", "chest pain with breathing difficulty",
        "fever with chills", "abdominal pain with vomiting", "back pain with numbness",
        "headache with vision problems", "chest pain radiating to arm",
        "difficulty swallowing", "persistent cough with blood",
        "severe fatigue with weight loss", "joint pain with swelling"
    ]

def main():
    print("Loading ML model...")
    classifier = load_classifier()
    
    if not classifier:
        print("Failed to load classifier!")
        return
    
    print("Model loaded successfully!")
    
    problems = get_common_medical_problems()
    emergency_levels = {}
    
    total_problems = len(problems)
    
    print(f"Processing {total_problems} medical problems...")
    
    for i, problem in enumerate(problems, 1):
        print(f"Processing {i}/{total_problems}: {problem}")
        level = calculate_emergency_level(problem, classifier)
        emergency_levels[problem.lower().strip()] = level
        
        # Progress indicator
        if i % 10 == 0:
            print(f"Completed {i}/{total_problems} ({(i/total_problems)*100:.1f}%)")
    
    # Save to JSON file
    output_file = "emergency_levels_precomputed.json"
    with open(output_file, 'w') as f:
        json.dump(emergency_levels, f, indent=2, sort_keys=True)
    
    print(f"\nPrecomputation complete!")
    print(f"Results saved to {output_file}")
    print(f"Total problems processed: {len(emergency_levels)}")
    
    # Display sample results
    print("\nSample results:")
    sample_items = list(emergency_levels.items())[:10]
    for problem, level in sample_items:
        print(f"  {problem}: {level}")

if __name__ == "__main__":
    main()