
import sys
from transformers import pipeline
import warnings

warnings.filterwarnings("ignore", category=FutureWarning)

classifier = None

def load_model():
    global classifier
    if classifier is None:
        try:
            classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")
        except Exception as e:
            classifier = None
    return classifier

def calculate_emergency_level(problem_description):
    load_model()

    if classifier:
        try:
            candidate_labels = ["low", "medium", "high"]
            result = classifier(problem_description, candidate_labels)

            # Getting the score
            max_score = max(result['scores'])
            emergency_level = int(max_score * 100)

            return emergency_level
        except Exception as e:
            return -1
    else:
        return -1

if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(1)

    problem = sys.argv[1]
    emergency_level = calculate_emergency_level(problem)
    print(emergency_level)
