package com.todolist.repositories;


import com.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Serializable>, PagingAndSortingRepository<Task, Serializable> {

    List<Task> findAll();

}
