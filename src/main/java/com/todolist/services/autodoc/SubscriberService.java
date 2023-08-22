package com.todolist.services.autodoc;


import com.todolist.entity.Group;
import com.todolist.entity.Subcriber;
import com.todolist.entity.User;
import com.todolist.repositories.SubscriberRepository;
import com.todolist.services.group.GroupService;
import com.todolist.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private SubscriberRepository subscriberRepository;
    private UserService userService;
    private GroupService groupService;

    public void createSubscriber(int idUser, int idGroup) {
        User user = userService.findUserById(idUser);
        Group group = groupService.findGroupById(idGroup);
        Subcriber subcriber = new Subcriber();
        subcriber.setUser(user);
        subcriber.setGroup(group);
        subscriberRepository.save(subcriber);
    }
}
