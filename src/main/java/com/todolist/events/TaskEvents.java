package com.todolist.events;

import com.todolist.dtos.ShowTask;
import com.todolist.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
@AllArgsConstructor
public class TaskEvents {

    private TaskService taskService;

    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Madrid")
    private void delateOutDatedTask() {
        taskService.findAllTasks().stream()
                .filter(task -> new ShowTask(task).getFinishedDate().isBefore(LocalDate.now()))
                .forEach(task -> taskService.deleteTask(task));
    }
}
