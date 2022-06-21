package com.todolist.repository;

import com.todolist.entity.GroupUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

@Qualifier("groupUserRepository")
public interface GroupUserRepository extends JpaRepository<GroupUser, Serializable>, PagingAndSortingRepository<GroupUser, Serializable> {

    List<GroupUser> findAll();

    List<GroupUser> findByIdGroup(Long idGroup);

    List<GroupUser> findByIdUser(Long idUser);

    List<GroupUser> findByIdGroupAndIdUser(Long idGroup, Long idUser);


}
