package com.aiagent.service;

import com.aiagent.dto.request.TaskRequest;
import com.aiagent.dto.response.TaskResponse;
import com.aiagent.model.Task;
import com.aiagent.model.User;
import com.aiagent.model.enums.TaskStatus;
import com.aiagent.model.enums.TaskType;
import com.aiagent.repository.TaskRepository;
import com.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AIAgentService aiAgentService;

    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .status(TaskStatus.PENDING)
                .scheduledTime(request.getScheduledTime())
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        // Call AI Agent to generate response
        String aiResponse = aiAgentService.processTask(savedTask);
        savedTask.setAiResponse(aiResponse);
        savedTask = taskRepository.save(savedTask);

        return convertToResponse(savedTask);
    }

    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found");
        }

        return convertToResponse(task);
    }

    public List<TaskResponse> getAllTasksByUser(User user) {
        return taskRepository.findByUser(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUserAndStatus(User user, TaskStatus status) {
        return taskRepository.findByUserAndStatus(user, status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUserAndType(User user, TaskType type) {
        return taskRepository.findByUserAndType(user, type).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setScheduledTime(request.getScheduledTime());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    public TaskResponse executeTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found");
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        try {
            task.setStatus(TaskStatus.COMPLETED);
            task.setExecutedAt(LocalDateTime.now());
        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
        }

        Task executedTask = taskRepository.save(task);
        return convertToResponse(executedTask);
    }

    public List<Task> getPendingTasksForExecution() {
        return taskRepository.findByStatusAndScheduledTimeBefore(
                TaskStatus.PENDING,
                LocalDateTime.now()
        );
    }

    public TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .type(task.getType())
                .status(task.getStatus())
                .scheduledTime(task.getScheduledTime())
                .aiResponse(task.getAiResponse())
                .executedAt(task.getExecutedAt())
                .createdAt(task.getCreatedAt())
                .userId(task.getUser().getId())
                .userName(task.getUser().getName())
                .build();
    }
}
