package com.aiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiAgentTaskAutomatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgentTaskAutomatorApplication.class, args);
    }
}
