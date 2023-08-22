package com.todolist.repositories;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

public interface GroupTaskRepository extends JpaRepository<Group, Serializable>, PagingAndSortingRepository<Group, Serializable> {

    @Query("SELECT t FROM Task t WHERE t.group.id = ?1")
    List<Task> findAllTasksFromGroupId(Integer idGroup);

    @Query("SELECT g FROM Group g WHERE g.id IN (SELECT t.group.id FROM Task t WHERE t.id = ?1)")
    List<Group> findAllGroupsByTaskId(Integer idTask);
}
