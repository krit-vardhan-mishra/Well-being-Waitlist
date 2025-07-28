package com.just_for_fun.wellbeing_waitlist_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmergencyLevelServiceImpl implements EmergencyLevelService {

    private final Map<String, Integer> precomputedLevels = new ConcurrentHashMap<>();
    private final Map<String, Integer> runtimeCache = new ConcurrentHashMap<>();
    private static final double SIMILARITY_THRESHOLD = 0.8;

    @PostConstruct
    public void initializePrecomputedLevels() {
        loadPrecomputedLevels();
    }

    private void loadPrecomputedLevels() {
        try {
            ClassPathResource resource = new ClassPathResource("emergency_levels_precomputed.json");
            ObjectMapper mapper = new ObjectMapper();

            @SuppressWarnings("unchecked")
            Map<String, Integer> levels = mapper.readValue(resource.getInputStream(), Map.class);

            precomputedLevels.putAll(levels);
            System.out.println("Loaded " + precomputedLevels.size() + " pre-computed emergency levels");

        } catch (IOException e) {
            System.err.println("Warning: Could not load pre-computed emergency levels. Using fallback method.");
            loadFallbackLevels();
        }
    }

    private void loadFallbackLevels() {
        Map<String, Integer> fallback = new HashMap<>();

        fallback.put("heart attack", 98);
        fallback.put("cardiac arrest", 98);
        fallback.put("stroke", 95);
        fallback.put("severe bleeding", 95);
        fallback.put("unconscious", 95);
        fallback.put("not breathing", 98);
        fallback.put("severe chest pain", 90);
        fallback.put("severe burns", 90);

        fallback.put("chest pain", 80);
        fallback.put("difficulty breathing", 85);
        fallback.put("severe pain", 75);
        fallback.put("high fever", 70);
        fallback.put("broken bone", 75);
        fallback.put("severe headache", 75);
        fallback.put("allergic reaction", 80);

        fallback.put("fever", 50);
        fallback.put("headache", 45);
        fallback.put("nausea", 40);
        fallback.put("vomiting", 45);
        fallback.put("cough", 35);
        fallback.put("sore throat", 30);

        fallback.put("cold", 20);
        fallback.put("minor cut", 15);
        fallback.put("routine checkup", 10);
        fallback.put("vaccination", 10);

        precomputedLevels.putAll(fallback);
    }

    @Override
    public int calculateEmergencyLevel(String problem) {
        if (problem == null || problem.trim().isEmpty()) {
            return 10;
        }

        String normalizedProblem = problem.toLowerCase().trim();

        if (runtimeCache.containsKey(normalizedProblem)) {
            return runtimeCache.get(normalizedProblem);
        }

        if (precomputedLevels.containsKey(normalizedProblem)) {
            int level = precomputedLevels.get(normalizedProblem);
            runtimeCache.put(normalizedProblem, level);
            return level;
        }

        int partialMatch = findPartialMatch(normalizedProblem);
        if (partialMatch > 0) {
            runtimeCache.put(normalizedProblem, partialMatch);
            return partialMatch;
        }

        int fuzzyMatch = findFuzzyMatch(normalizedProblem);
        if (fuzzyMatch > 0) {
            runtimeCache.put(normalizedProblem, fuzzyMatch);
            return fuzzyMatch;
        }

        int mlResult = calculateUsingPythonScript(normalizedProblem);
        if (mlResult > 0) {
            runtimeCache.put(normalizedProblem, mlResult);
            return mlResult;
        }

        int keywordResult = calculateUsingKeywords(normalizedProblem);
        runtimeCache.put(normalizedProblem, keywordResult);
        return keywordResult;
    }

    private int findPartialMatch(String problem) {
        for (Map.Entry<String, Integer> entry : precomputedLevels.entrySet()) {
            String knownProblem = entry.getKey();
            if (problem.contains(knownProblem) || knownProblem.contains(problem)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    private int findFuzzyMatch(String problem) {
        String bestMatch = null;
        double bestSimilarity = 0.0;
        int bestLevel = 0;

        for (Map.Entry<String, Integer> entry : precomputedLevels.entrySet()) {
            String knownProblem = entry.getKey();
            double similarity = calculateSimilarity(problem, knownProblem);

            if (similarity > bestSimilarity && similarity >= SIMILARITY_THRESHOLD) {
                bestSimilarity = similarity;
                bestMatch = knownProblem;
                bestLevel = entry.getValue();
            }
        }

        if (bestMatch != null) {
            System.out.println("Fuzzy match found: '" + problem + "' -> '" + bestMatch +
                    "' (similarity: " + String.format("%.2f", bestSimilarity) + ")");
            return bestLevel;
        }

        return 0;
    }

    private double calculateSimilarity(String s1, String s2) {
        String[] words1 = s1.split("\\s+");
        String[] words2 = s2.split("\\s+");

        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        for (String word : words1)
            set1.add(word);
        for (String word : words2)
            set2.add(word);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty())
            return 0.0;
        return (double) intersection.size() / union.size();
    }

    private int calculateUsingPythonScript(String problem) {
        try {
            String scriptPath = new File("src/main/resources/scripts/emergency_level.py").getAbsolutePath();
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, problem);
            processBuilder.directory(new File("src/main/resources/scripts"));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String result = reader.readLine();
            reader.close();

            int exitCode = process.waitFor();
            if (exitCode == 0 && result != null) {
                int level = Integer.parseInt(result.trim());
                System.out.println("ML model calculated level " + level + " for: " + problem);
                return level;
            }

        } catch (IOException | NumberFormatException | InterruptedException e) {
            System.err.println("Python script execution failed: " + e.getMessage());
        }

        return 0; 
    }

    private int calculateUsingKeywords(String problem) {
        int score = 20;

        if (problem.contains("severe") || problem.contains("acute") ||
                problem.contains("intense") || problem.contains("critical")) {
            score += 30;
        }

        if (problem.contains("pain")) {
            score += 20;
        }

        if (problem.contains("bleeding") || problem.contains("blood")) {
            score += 25;
        }

        if (problem.contains("breathing") || problem.contains("breath")) {
            score += 35;
        }

        if (problem.contains("chest") || problem.contains("heart")) {
            score += 30;
        }

        if (problem.contains("emergency") || problem.contains("urgent")) {
            score += 25;
        }

        if (problem.contains("mild") || problem.contains("minor") ||
                problem.contains("routine") || problem.contains("checkup")) {
            score -= 15;
        }

        return Math.max(10, Math.min(score, 100));
    }

    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("precomputedCount", precomputedLevels.size());
        stats.put("runtimeCacheCount", runtimeCache.size());
        stats.put("totalCachedProblems", precomputedLevels.size() + runtimeCache.size());
        return stats;
    }

    public void addToRuntimeCache(String problem, int level) {
        String normalizedProblem = problem.toLowerCase().trim();
        runtimeCache.put(normalizedProblem, level);
    }
}