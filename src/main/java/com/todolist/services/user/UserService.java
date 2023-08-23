package com.todolist.services.user;

import com.fadda.common.tuples.pair.Pair;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import com.todolist.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService<User> {

    // Repositories -----------------------------------------------------------
    private final UserRepository userRepository;

    @PostConstruct
    public void init() throws IOException {
        super.init();
    }

    @Override
    @Transactional
    public void saveEntity(User entity) {
        userRepository.save(entity);
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<User> findAllUsers(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public User findUserById(Integer idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("The user with idUser " + idUser + " does not exist."));
    }

    @Transactional(readOnly = true)
    public User findUserByIdClockify(String idClockify) {
        return userRepository.findByClockifyId(idClockify).stream().findFirst().orElseThrow(() -> new NotFoundException("The user with idUser " + idClockify + " does not exist."));
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("The user with username " + username + " does not exist."));
    }

    @Transactional
    public Long getTaskCompleted(User user) {
        return userRepository.countTaskCompleted(user.getId());
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
