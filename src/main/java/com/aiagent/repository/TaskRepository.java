package com.aiagent.repository;

import com.aiagent.model.Task;
import com.aiagent.model.User;
import com.aiagent.model.enums.TaskStatus;
import com.aiagent.model.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, TaskStatus status);
    List<Task> findByUserAndType(User user, TaskType type);
    List<Task> findByStatusAndScheduledTimeBefore(TaskStatus status, LocalDateTime time);
}
