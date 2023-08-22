package com.todolist.services.user;

import com.todolist.entity.User;
import com.todolist.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindUserById() {
        Integer idUser = 123;
        User user = new User();
        user.setId(idUser);

        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

        User result = userService.findUserById(idUser);

        assertEquals(idUser, result.getId());
        verify(userRepository, times(1)).findById(idUser);
    }

    @Test
    void testFindUserByUsername() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.findUserByUsername(username);

        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    // Add more test methods for other service methods
}

