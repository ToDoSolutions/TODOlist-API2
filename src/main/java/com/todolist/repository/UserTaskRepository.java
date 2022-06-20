package com.todolist.repository;

import com.todolist.entity.UserTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

@Qualifier("userTaskRepository")
public interface UserTaskRepository extends JpaRepository<UserTask, Serializable>, PagingAndSortingRepository<UserTask, Serializable> {

    List<UserTask> findAll();
    List<UserTask> findByIdTask(Long idTask);
    List<UserTask> findByIdUser(Long idUser);
    List<UserTask> findByIdTaskAndIdUser(Long idTask, Long idUser);

}
