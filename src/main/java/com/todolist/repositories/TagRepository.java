package com.todolist.repositories;

import com.todolist.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Serializable>, PagingAndSortingRepository<Tag, Serializable> {
    Optional<Tag> findById(String clockifyId);
}
