package com.todolist.repositories;

import com.todolist.dtos.autodoc.RoleStatus;
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

    List<Role> findAllByStatus(RoleStatus name);

    List<Role> findAllByTaskId(Integer taskId);

    @Query("SELECT r.duration FROM Role r WHERE r.id = :taskId")
    List<Duration> findAllDurationByTaskId(Integer taskId);

    @Query("SELECT r.status FROM Role r WHERE r.id = :taskId")
    List<RoleStatus> findAllStatusByTaskId(Integer taskId);

    Optional<Role> findRoleByTaskIdAndStatus(Integer taskId, RoleStatus status);
}
