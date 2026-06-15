package com.aiagent.controller;

import com.aiagent.dto.response.ApiResponse;
import com.aiagent.service.AIAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"})
public class AgentController {
    @Autowired
    private AIAgentService aiAgentService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse> checkAgentStatus() {
        try {
            boolean isConnected = aiAgentService.checkGeminiConnectivity();
            Map<String, Object> status = new HashMap<>();
            status.put("connected", isConnected);
            status.put("service", "Google Gemini AI");

            if (isConnected) {
                return ResponseEntity.ok(new ApiResponse(200, "AI Agent is operational", status));
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse(503, "AI Agent is unavailable", status));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error checking agent status: " + e.getMessage(), null));
        }
    }
}
