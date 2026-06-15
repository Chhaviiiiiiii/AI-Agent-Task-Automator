package com.aiagent.controller;

import com.aiagent.dto.request.TaskRequest;
import com.aiagent.dto.response.ApiResponse;
import com.aiagent.dto.response.TaskResponse;
import com.aiagent.model.User;
import com.aiagent.model.enums.TaskStatus;
import com.aiagent.model.enums.TaskType;
import com.aiagent.service.TaskService;
import com.aiagent.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"})
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            User user = getCurrentUser();
            TaskResponse taskResponse = taskService.createTask(request, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "Task created successfully", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error creating task: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllTasks() {
        try {
            User user = getCurrentUser();
            List<TaskResponse> tasks = taskService.getAllTasksByUser(user);
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error retrieving tasks: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            TaskResponse task = taskService.getTaskById(id, user);
            return ResponseEntity.ok(new ApiResponse(200, "Task retrieved successfully", task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "Task not found: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        try {
            User user = getCurrentUser();
            TaskResponse updatedTask = taskService.updateTask(id, request, user);
            return ResponseEntity.ok(new ApiResponse(200, "Task updated successfully", updatedTask));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "Task not found: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            taskService.deleteTask(id, user);
            return ResponseEntity.ok(new ApiResponse(200, "Task deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "Task not found: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse> executeTask(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            TaskResponse executedTask = taskService.executeTask(id, user);
            return ResponseEntity.ok(new ApiResponse(200, "Task executed successfully", executedTask));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "Task not found: " + e.getMessage(), null));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getTasksByStatus(@PathVariable String status) {
        try {
            User user = getCurrentUser();
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            List<TaskResponse> tasks = taskService.getTasksByUserAndStatus(user, taskStatus);
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Invalid status: " + e.getMessage(), null));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse> getTasksByType(@PathVariable String type) {
        try {
            User user = getCurrentUser();
            TaskType taskType = TaskType.valueOf(type.toUpperCase());
            List<TaskResponse> tasks = taskService.getTasksByUserAndType(user, taskType);
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Invalid type: " + e.getMessage(), null));
        }
    }
}
