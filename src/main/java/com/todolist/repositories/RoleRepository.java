package com.todolist.repositories;

import com.todolist.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Serializable>, PagingAndSortingRepository<Role, Serializable> {

    List<Role> findAllByTaskId(Integer taskId);

    @Query("SELECT r.duration FROM Role r WHERE r.id = :taskId")
    List<Duration> findAllDurationByTaskId(Integer taskId);

    @Query("SELECT r FROM Role r WHERE r.id = :taskId")
    List<Role> findAllStatusByTaskId(Integer taskId);

    Optional<Role> findRoleByTaskIdAndTagName(Integer taskId, String name);
}
