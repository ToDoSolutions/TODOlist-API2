package com.todolist.repositories;


import com.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface PlanningRepository extends JpaRepository<Task, Serializable>, PagingAndSortingRepository<Task, Serializable> {

    @Query(value = "SELECT t FROM Task t WHERE t.user.id = ?1")
    List<Task> findAllTaskByUserId(Integer idUser);


}
