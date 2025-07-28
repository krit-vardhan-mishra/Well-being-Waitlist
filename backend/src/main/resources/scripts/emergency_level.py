import sys
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
    with open(os.devnull, "w") as devnull:
        with contextlib.redirect_stdout(devnull):
            yield

classifier = None
LAST_LOAD = 0
COOLDOWN = 60

def load_classifier():
    global classifier, LAST_LOAD
    now = time()
    if classifier is None and (now - LAST_LOAD) > COOLDOWN:
        try:
            MODEL_NAME = "typeform/mobilebert-uncased-mnli"
            with suppress_stdout():
                classifier = pipeline(
                    "zero-shot-classification",
                    model=MODEL_NAME,
                    tokenizer=MODEL_NAME,
                    device=-1
                )
            LAST_LOAD = now
        except Exception:
            classifier = None
    return classifier

def calculate_emergency_level(problem: str) -> int:
    if not problem or not problem.strip():
        return 1

    clf = load_classifier()
    if not clf:
        return -1

    labels = [
        "very low urgency", "low urgency", 
        "medium urgency", "high urgency", 
        "very high urgency", "critical emergency"
    ]

    try:
        result = clf(problem, labels, multi_label=False)
        weights = {
            "very low urgency": 10, "low urgency":       30,
            "medium urgency":    50, "high urgency":      70,
            "very high urgency": 90, "critical emergency":98
        }
        score = 0
        for lbl, sc in zip(result["labels"], result["scores"]):
            score += weights.get(lbl, 0) * sc

        level = int(min(max(round(score), 1), 100))
        return level

    except Exception:
        return -1

def main():
    if len(sys.argv) < 2:
        print(-1)
        sys.exit(1)

    problem_text = sys.argv[1]
    lvl = calculate_emergency_level(problem_text)
    print(lvl)
    sys.exit(0 if lvl > 0 else 1)

if __name__ == "__main__":
    main()