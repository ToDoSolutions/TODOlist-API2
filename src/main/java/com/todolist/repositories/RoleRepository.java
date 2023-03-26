package com.todolist.repositories;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Serializable>, PagingAndSortingRepository<Role, Serializable> {

    List<Role> findAllByStatus(RoleStatus name);
}
