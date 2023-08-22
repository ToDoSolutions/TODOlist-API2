package com.todolist.repositories;

import com.todolist.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Serializable>, PagingAndSortingRepository<Group, Serializable> {
    List<Group> findAll();

    Optional<Group> findByName(String name);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.group.id = ?1")
    Long countTaskInGroup(Integer idGroup);


}
