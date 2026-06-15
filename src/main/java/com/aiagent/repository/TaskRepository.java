package com.aiagent.repository;

import com.aiagent.entity.Task;
import com.aiagent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);
    List<Task> findByUserOrderByCreatedAtDesc(User user);
}
