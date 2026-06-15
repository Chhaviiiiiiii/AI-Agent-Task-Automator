package com.aiagent.service;

import com.aiagent.dto.TaskRequest;
import com.aiagent.entity.Task;
import com.aiagent.entity.User;
import com.aiagent.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(user);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setPriority(Task.Priority.MEDIUM);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setPriority("MEDIUM");
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(user, taskRequest);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateTask() {
        taskRequest.setTitle("Updated Task");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(task, taskRequest);

        assertNotNull(updatedTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testDeleteTask() {
        taskService.deleteTask(task);
        verify(taskRepository, times(1)).delete(task);
    }
}
