package com.aiagent.dto.response;

import com.aiagent.model.enums.TaskStatus;
import com.aiagent.model.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskType type;
    private TaskStatus status;
    private LocalDateTime scheduledTime;
    private String aiResponse;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
}
