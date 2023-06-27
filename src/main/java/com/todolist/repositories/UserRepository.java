package com.todolist.repositories;

import com.todolist.entity.Task;
import com.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Serializable>, PagingAndSortingRepository<User, Serializable> {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    List<User> findByClockifyId(String clockifyId);

    @Query(value = "SELECT COUNT(*) FROM User u JOIN u.tasks t WHERE t.status = 'DONE' AND u.id = ?1")
    Long countTaskCompleted(Integer idUser);

    @Query(value = "SELECT u FROM User u JOIN u.tasks t WHERE t.id = ?1")
    List<User> findAllByTaskId(Integer idTask);

    @Query(value = "SELECT t FROM User u JOIN u.tasks t WHERE u.id = ?1")
    List<Task> findAllTaskByUserId(Integer idUser);

    @Query(value = "SELECT t FROM User u JOIN u.tasks t WHERE u.id = ?1 AND t.student = 0")
    List<Task> findAllGroupTaskByUserId(Integer idUser);

}
