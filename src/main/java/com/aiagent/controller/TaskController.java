package com.aiagent.controller;

import com.aiagent.dto.TaskRequest;
import com.aiagent.dto.TaskResponse;
import com.aiagent.entity.Task;
import com.aiagent.entity.User;
import com.aiagent.service.JwtService;
import com.aiagent.service.TaskService;
import com.aiagent.service.UserService;
import com.apiresponse.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);

        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<TaskResponse>builder()
                    .success(false)
                    .message("User not found")
                    .statusCode(404)
                    .build()
            );
        }

        Task task = taskService.createTask(user.get(), request);
        TaskResponse response = mapToResponse(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task created successfully")
                .data(response)
                .statusCode(201)
                .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUserTasks(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);

        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<List<TaskResponse>>builder()
                    .success(false)
                    .message("User not found")
                    .statusCode(404)
                    .build()
            );
        }

        List<TaskResponse> tasks = taskService.getUserTasks(user.get());

        return ResponseEntity.ok(
            ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Tasks retrieved successfully")
                .data(tasks)
                .statusCode(200)
                .build()
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String authHeader) {
        Optional<Task> task = taskService.getTaskById(taskId);

        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<TaskResponse>builder()
                    .success(false)
                    .message("Task not found")
                    .statusCode(404)
                    .build()
            );
        }

        return ResponseEntity.ok(
            ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task retrieved successfully")
                .data(mapToResponse(task.get()))
                .statusCode(200)
                .build()
        );
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus().toString())
            .priority(task.getPriority().toString())
            .result(task.getResult())
            .aiResponse(task.getAiResponse())
            .createdAt(task.getCreatedAt())
            .completedAt(task.getCompletedAt())
            .build();
    }
}
