package com.aiagent.controller;

import com.aiagent.dto.TaskRequest;
import com.aiagent.dto.TaskResponse;
import com.aiagent.dto.PaginatedResponse;
import com.aiagent.entity.Task;
import com.aiagent.entity.User;
import com.aiagent.service.JwtService;
import com.aiagent.service.TaskService;
import com.aiagent.service.UserService;
import com.aiagent.service.EmailService;
import com.aiagent.service.GeminiAiService;
import com.apiresponse.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final GeminiAiService geminiAiService;

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
        
        // Send email notification
        emailService.sendTaskCreatedNotification(user.get().getEmail(), task.getTitle(), task.getId());

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
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getUserTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);

        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<PaginatedResponse<TaskResponse>>builder()
                    .success(false)
                    .message("User not found")
                    .statusCode(404)
                    .build()
            );
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = taskService.getUserTasksPaginated(user.get(), pageable);
        
        List<TaskResponse> taskResponses = tasksPage.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        PaginatedResponse<TaskResponse> paginatedResponse = PaginatedResponse.<TaskResponse>builder()
            .content(taskResponses)
            .pageNumber(page)
            .pageSize(size)
            .totalElements(tasksPage.getTotalElements())
            .totalPages(tasksPage.getTotalPages())
            .hasMore(tasksPage.hasNext())
            .build();

        return ResponseEntity.ok(
            ApiResponse.<PaginatedResponse<TaskResponse>>builder()
                .success(true)
                .message("Tasks retrieved successfully")
                .data(paginatedResponse)
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

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request,
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

        Task updatedTask = taskService.updateTask(task.get(), request);
        return ResponseEntity.ok(
            ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task updated successfully")
                .data(mapToResponse(updatedTask))
                .statusCode(200)
                .build()
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String authHeader) {
        Optional<Task> task = taskService.getTaskById(taskId);

        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Void>builder()
                    .success(false)
                    .message("Task not found")
                    .statusCode(404)
                    .build()
            );
        }

        taskService.deleteTask(task.get());
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .message("Task deleted successfully")
                .statusCode(200)
                .build()
        );
    }

    @PostMapping("/{taskId}/automate")
    public ResponseEntity<ApiResponse<TaskResponse>> automateTask(
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

        // Generate AI response
        String aiResponse = geminiAiService.automateTask(task.get().getTitle(), task.get().getDescription());
        
        Task updatedTask = task.get();
        updatedTask.setAiResponse(aiResponse);
        updatedTask.setStatus(Task.TaskStatus.COMPLETED);
        taskService.saveTask(updatedTask);

        return ResponseEntity.ok(
            ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task automated successfully")
                .data(mapToResponse(updatedTask))
                .statusCode(200)
                .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> searchTasks(
            @RequestParam String query,
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

        List<TaskResponse> tasks = taskService.searchTasks(user.get(), query);
        return ResponseEntity.ok(
            ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Search completed")
                .data(tasks)
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
