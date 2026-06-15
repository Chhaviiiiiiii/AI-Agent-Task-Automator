package com.aiagent.service;

import com.aiagent.model.Task;
import com.aiagent.model.enums.TaskStatus;
import com.aiagent.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSchedulerService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void executeScheduledTasks() {
        try {
            List<Task> pendingTasks = taskRepository.findByStatusAndScheduledTimeBefore(
                    TaskStatus.PENDING,
                    LocalDateTime.now()
            );

            for (Task task : pendingTasks) {
                try {
                    task.setStatus(TaskStatus.IN_PROGRESS);
                    taskRepository.save(task);

                    // Send email if task type is EMAIL
                    if (task.getType().toString().equals("EMAIL")) {
                        emailService.sendTaskExecutionEmail(
                                task.getUser().getEmail(),
                                task.getTitle(),
                                task.getAiResponse()
                        );
                    }

                    task.setStatus(TaskStatus.COMPLETED);
                    task.setExecutedAt(LocalDateTime.now());
                    taskRepository.save(task);
                } catch (Exception e) {
                    task.setStatus(TaskStatus.FAILED);
                    taskRepository.save(task);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
