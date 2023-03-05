package com.todolist.repositories;


import com.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Serializable>, PagingAndSortingRepository<Task, Serializable> {

    List<Task> findAll();

    Optional<Task> findByIdTask(Long idTask);

    List<Task> findByTitle(String title);

}
