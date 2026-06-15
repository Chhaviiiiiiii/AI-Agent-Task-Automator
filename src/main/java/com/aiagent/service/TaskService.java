package com.aiagent.service;

import com.aiagent.dto.TaskRequest;
import com.aiagent.dto.TaskResponse;
import com.aiagent.entity.Task;
import com.aiagent.entity.User;
import com.aiagent.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(User user, TaskRequest request) {
        Task task = Task.builder()
            .user(user)
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(Task.Priority.valueOf(request.getPriority() != null ? request.getPriority().toUpperCase() : "MEDIUM"))
            .status(Task.TaskStatus.PENDING)
            .build();
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<TaskResponse> getUserTasks(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public Task updateTaskStatus(Task task, Task.TaskStatus status) {
        task.setStatus(status);
        if (status == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
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
