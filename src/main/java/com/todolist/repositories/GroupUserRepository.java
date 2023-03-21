package com.todolist.repositories;

import com.todolist.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Serializable>, PagingAndSortingRepository<GroupUser, Serializable> {

    List<GroupUser> findAll();

    List<GroupUser> findById(Integer idGroup);

    List<GroupUser> findByIdAndIdUser(Integer idGroup, Integer idUser);


}
