package com.todolist.repositories;

import com.todolist.entity.Group;
import com.todolist.entity.GroupUser;
import com.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Serializable>, PagingAndSortingRepository<GroupUser, Serializable>{

    List<GroupUser> findAll();

    List<GroupUser> findByIdGroup(Integer idGroup);

    List<GroupUser> findByIdAndIdUser(Integer idGroup, Integer idUser);

    @Query("SELECT g FROM Group g WHERE g.id IN (SELECT gu.idGroup FROM GroupUser gu WHERE gu.idUser = ?1)")
    List<Group> findAllByIdUser(Integer idUser);

    @Query("SELECT u FROM User u WHERE u.id IN (SELECT gu.idUser FROM GroupUser gu WHERE gu.idGroup = ?1)")
    List<User> findAllByIdGroup(Integer idGroup);


}
