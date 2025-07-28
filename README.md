
# Well‑being Waitlist

Well‑being Waitlist is a hospital portal that prioritizes patient checkups using **precomputed emergency levels** (no LLM needed), making the system faster and more efficient.

## Project Overview

- **System goal:** Dynamically manage patient queue by emergency priority.
- **Updated flow:** Users submit patient problem → mapped to precomputed emergency level → patient is queued in a max‑heap.
- On each **check‑up**:
  - Highest‑priority patient is removed.
  - Remaining patients’ emergency levels increase by a fixed increment (e.g. +5).
- User interface updates in real time to reflect current queue.

## Tech Stack

| Layer       | Tools & Frameworks                    |
|-------------|---------------------------------------|
| Backend     | Spring Boot (Java), REST API          |
| Frontend    | React.js, Tailwind CSS                |
| Data Store  | MySQL                                 |

## 🚀 How It Works (Updated Flow)

1. Patient problem is submitted through the React frontend.
2. The backend maps the problem to a **precomputed emergency level** (LLM will only required when data is not found on precomputed emergency levels).
3. Patient data is inserted into a **max-heap** based on emergency priority.
4. Upon checkup:
   - Highest priority patient is removed.
   - All remaining patients’ emergency level increases (e.g., by +5).
5. Frontend reflects real-time updates.


# SpringBoot-React
trying to create a project using Spring-Boot in Backend and React.js + Tailwind CSS in Frontend
