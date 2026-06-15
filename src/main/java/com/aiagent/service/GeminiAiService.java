package com.aiagent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiAiService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String generateTaskResponse(String taskDescription) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            return "AI integration not configured. Please set GEMINI_API_KEY environment variable.";
        }

        try {
            Map<String, Object> request = buildGeminiRequest(taskDescription);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = geminiApiUrl + "?key=" + geminiApiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            return extractResponseText(response);
        } catch (Exception e) {
            return "Error generating AI response: " + e.getMessage();
        }
    }

    public String automateTask(String taskTitle, String taskDescription) {
        String prompt = String.format(
            "As an AI task automation agent, please analyze and provide an automated solution for the following task:\n" +
            "Title: %s\n" +
            "Description: %s\n" +
            "Please provide step-by-step actions and recommendations.",
            taskTitle, taskDescription
        );
        
        return generateTaskResponse(prompt);
    }

    private Map<String, Object> buildGeminiRequest(String prompt) {
        Map<String, Object> request = new HashMap<>();
        
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        
        content.put("parts", parts);
        contents.add(content);
        
        request.put("contents", contents);
        
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 2048);
        
        request.put("generationConfig", generationConfig);
        
        return request;
    }

    @SuppressWarnings("unchecked")
    private String extractResponseText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            // Handle parsing error
        }
        return "Unable to extract response from AI";
    }
}
