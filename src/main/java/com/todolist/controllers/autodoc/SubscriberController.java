package com.todolist.controllers.autodoc;

import com.todolist.services.autodoc.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/autodoc")
@RequiredArgsConstructor
public class SubscriberController {

    private SubscriberService subscriberService;

    @PostMapping("suscribe/group/{idGroup}/user/{idUser}")
    public void addSubcription(@PathVariable("idGroup") int idGroup, @PathVariable("idUser") int idUser) {
        subscriberService.createSubscriber(idUser, idGroup);
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void sendMessage() {

    }
}
