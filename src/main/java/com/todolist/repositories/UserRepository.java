package com.todolist.repositories;

import com.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
