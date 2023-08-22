package com.todolist.repositories;

import com.todolist.entity.Subcriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface SubscriberRepository extends JpaRepository<Subcriber, Serializable>, PagingAndSortingRepository<Subcriber, Serializable> {


}
