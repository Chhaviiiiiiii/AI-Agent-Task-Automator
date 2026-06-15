package com.aiagent.controller;

import com.aiagent.dto.response.ApiResponse;
import com.aiagent.model.Task;
import com.aiagent.model.User;
import com.aiagent.model.enums.TaskStatus;
import com.aiagent.repository.TaskRepository;
import com.aiagent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(new ApiResponse(200, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error retrieving users: " + e.getMessage(), null));
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse> getAllTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error retrieving tasks: " + e.getMessage(), null));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        try {
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalUsers", (long) userRepository.findAll().size());
            stats.put("totalTasks", (long) taskRepository.findAll().size());
            stats.put("completedTasks", taskRepository.findByStatusAndScheduledTimeBefore(
                    TaskStatus.COMPLETED, java.time.LocalDateTime.now()).size());
            stats.put("pendingTasks", taskRepository.findByStatusAndScheduledTimeBefore(
                    TaskStatus.PENDING, java.time.LocalDateTime.now()).size());
            stats.put("failedTasks", taskRepository.findByStatusAndScheduledTimeBefore(
                    TaskStatus.FAILED, java.time.LocalDateTime.now()).size());

            return ResponseEntity.ok(new ApiResponse(200, "Statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error retrieving stats: " + e.getMessage(), null));
        }
    }

    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<ApiResponse> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            Task task = taskRepository.findById(id).orElse(null);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(404, "Task not found", null));
            }
            task.setStatus(status);
            taskRepository.save(task);
            return ResponseEntity.ok(new ApiResponse(200, "Task status updated", task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "Error updating task: " + e.getMessage(), null));
        }
    }
}
