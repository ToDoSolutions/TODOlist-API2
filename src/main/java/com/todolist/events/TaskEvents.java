package com.todolist.events;

import com.todolist.dtos.ShowTask;
import com.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
public class TaskEvents {

    // Services ---------------------------------------------------------------
    private final TaskService taskService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public TaskEvents(TaskService taskService) {
        this.taskService = taskService;
    }

    // Scheduled --------------------------------------------------------------
    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Madrid")
    private void delateOutDatedTask() {
        taskService.findAllTasks().stream()
                .filter(task -> new ShowTask(task).getFinishedDate().isBefore(LocalDate.now()))
                .forEach(taskService::deleteTask);
    }
}
