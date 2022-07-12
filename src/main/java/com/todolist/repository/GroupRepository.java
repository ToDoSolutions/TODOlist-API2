package com.todolist.repository;

import com.todolist.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Serializable>, PagingAndSortingRepository<Group, Serializable> {
    List<Group> findAll();

    Group findByIdGroup(Long idGroup);
}
