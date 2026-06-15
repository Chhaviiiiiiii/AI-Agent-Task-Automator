package com.aiagent.service;

import com.aiagent.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIAgentService {
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String processTask(Task task) {
        try {
            String prompt = String.format(
                    "You are a smart task automation agent. Given this task:\n" +
                    "[TASK TITLE]: %s\n" +
                    "[DESCRIPTION]: %s\n" +
                    "[TYPE]: %s\n" +
                    "Generate a step-by-step action plan to complete this task. " +
                    "Keep it under 5 steps, be specific and professional.",
                    task.getTitle(),
                    task.getDescription(),
                    task.getType().toString()
            );

            Map<String, Object> requestBody = buildGeminiRequest(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String url = geminiApiUrl + "?key=" + geminiApiKey;
            String response = restTemplate.postForObject(url, request, String.class);

            return parseGeminiResponse(response);
        } catch (Exception e) {
            return "AI processing temporarily unavailable. Task queued for manual review.";
        }
    }

    private Map<String, Object> buildGeminiRequest(String prompt) {
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", new Object[]{part});
        request.put("contents", new Object[]{content});
        return request;
    }

    private String parseGeminiResponse(String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if (responseMap.containsKey("candidates")) {
                Object candidates = responseMap.get("candidates");
                if (candidates instanceof java.util.List) {
                    java.util.List<?> candidatesList = (java.util.List<?>) candidates;
                    if (!candidatesList.isEmpty()) {
                        Map<String, Object> firstCandidate = (Map<String, Object>) candidatesList.get(0);
                        if (firstCandidate.containsKey("content")) {
                            Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
                            if (contentMap.containsKey("parts")) {
                                java.util.List<?> parts = (java.util.List<?>) contentMap.get("parts");
                                if (!parts.isEmpty()) {
                                    Map<String, Object> firstPart = (Map<String, Object>) parts.get(0);
                                    if (firstPart.containsKey("text")) {
                                        return firstPart.get("text").toString();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "AI processing temporarily unavailable. Task queued for manual review.";
    }

    public boolean checkGeminiConnectivity() {
        try {
            String testPrompt = "Test";
            Map<String, Object> requestBody = buildGeminiRequest(testPrompt);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = geminiApiUrl + "?key=" + geminiApiKey;
            String response = restTemplate.postForObject(url, request, String.class);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
