package com.todolist.utilities;

import com.todolist.model.ShowTask;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;

public class Order {

    public static Sort sequenceTask(String order) {
        if (order.equals("idTask"))
            return Sort.by(Sort.Direction.ASC, "idTask");
        else if (order.equals("-idTask"))
            return Sort.by(Sort.Direction.DESC, "idTask");
        else if (order.equals("title"))
            return Sort.by(Sort.Direction.ASC, "title");
        else if (order.equals("-title"))
            return Sort.by(Sort.Direction.DESC, "title");
        else if (order.equals("status"))
            return Sort.by(Sort.Direction.ASC, "status");
        else if (order.equals("-status"))
            return Sort.by(Sort.Direction.DESC, "status");
        else if (order.equals("releaseDate"))
            return Sort.by(Sort.Direction.ASC, "releaseDate");
        else if (order.equals("-releaseDate"))
            return Sort.by(Sort.Direction.DESC, "releaseDate");
        else if (order.equals("finishedDate"))
            return Sort.by(Sort.Direction.ASC, "finishedDate");
        else if (order.equals("-finishedDate"))
            return Sort.by(Sort.Direction.DESC, "finishedDate");
        else if (order.equals("priority"))
            return Sort.by(Sort.Direction.ASC, "priority");
        else if (order.equals("-priority"))
            return Sort.by(Sort.Direction.DESC, "priority");
        else if (order.equals("difficulty"))
            return Sort.by(Sort.Direction.ASC, "difficulty");
        else if (order.equals("-difficulty"))
            return Sort.by(Sort.Direction.DESC, "difficulty");
        else
            return Sort.by(Sort.Direction.ASC, "idTask");
    }

    /*
    public static void sequenceUser(List<User> result, String order) {
        if (order.equals("idUser"))
            result.sort(Comparator.comparing(User::getIdUser));
        else if (order.equals("-idUser"))
            result.sort(Comparator.comparing(User::getIdUser).reversed());
        else if (order.equals("name"))
            result.sort(Comparator.comparing(User::getName));
        else if (order.equals("-name"))
            result.sort(Comparator.comparing(User::getName).reversed());
        else if (order.equals("surname"))
            result.sort(Comparator.comparing(User::getSurname));
        else if (order.equals("-surname"))
            result.sort(Comparator.comparing(User::getSurname).reversed());
        else if (order.equals("email"))
            result.sort(Comparator.comparing(User::getEmail));
        else if (order.equals("-email"))
            result.sort(Comparator.comparing(User::getEmail).reversed());
        else if (order.equals("location"))
            result.sort(Comparator.comparing(User::getLocation));
        else if (order.equals("-location"))
            result.sort(Comparator.comparing(User::getLocation).reversed());
        else if (order.equals("taskCompleted"))
            result.sort(Comparator.comparing(User::getTaskCompleted));
        else if (order.equals("-taskCompleted"))
            result.sort(Comparator.comparing(User::getTaskCompleted).reversed());
    }

    public static void sequenceGroup(List<Group> result, String order) {
        if (order.equals("idGroup"))
            result.sort(Comparator.comparing(Group::getIdGroup));
        else if (order.equals("-idGroup"))
            result.sort(Comparator.comparing(Group::getIdGroup).reversed());
        else if (order.equals("name"))
            result.sort(Comparator.comparing(Group::getName));
        else if (order.equals("-name"))
            result.sort(Comparator.comparing(Group::getName).reversed());
        else if (order.equals("description"))
            result.sort(Comparator.comparing(Group::getDescription));
        else if (order.equals("-description"))
            result.sort(Comparator.comparing(Group::getDescription).reversed());
        else if (order.equals("numTask"))
            result.sort(Comparator.comparing(Group::getNumTasks));
        else if (order.equals("-numTask"))
            result.sort(Comparator.comparing(Group::getNumTasks).reversed());
    }
     */

    private Order() {
    }
}
