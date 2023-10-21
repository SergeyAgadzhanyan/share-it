package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:h2:mem:shareitTest",
                "spring.datasource.driverClassName=org.h2.Driver"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceITest {

    private final EntityManager em;
    private final UserService userService;


    UserDto userDto;
    UserDto userDto2;
    UserDto userDtoWithSameEmail;
    UserDto expectedUserDto;
    UserDto expectedUserDto2;


    @BeforeEach
    void init() {
        userDto = new UserDto(1L, "u1", "e1@mail.ru");
        userDto2 = new UserDto(2L, "u2", "e2@mail.ru");
        userDtoWithSameEmail = new UserDto(3L, "u3", "e2@mail.ru");
        expectedUserDto = userService.addUser(userDto);
        expectedUserDto2 = userService.addUser(userDto2);
    }

    @Test
    void addUser() {
        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> userList = query.getResultList();
        assertThat(expectedUserDto.getName(), equalTo(userList.get(0).getName()));
        assertThrows(RuntimeException.class, () -> userService.addUser(userDtoWithSameEmail));
    }

    @Test
    void getUserById() {
        UserDto userFromService = userService.getUserById(expectedUserDto.getId());
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", expectedUserDto.getId()).getSingleResult();
        assertThat(userFromService.getName(), equalTo(userFromDb.getName()));
        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    void update() {
        userDto.setName("newName");
        UserDto updatedUser = userService.update(expectedUserDto.getId(), userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", expectedUserDto.getId()).getSingleResult();
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(userFromDb.getName(), equalTo(userDto.getName()));
        assertThrows(RuntimeException.class, () -> {
            userDto2.setEmail(userDto.getEmail());
            userService.update(expectedUserDto2.getId(), userDto2);
            userService.getUsers();
        });
    }

    @Test
    void deleteUser() {
        userService.deleteUser(expectedUserDto.getId());
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        assertThrows(RuntimeException.class, () -> query.setParameter("id", expectedUserDto.getId()).getSingleResult());
    }

    @Test
    void getUsers() {
        List<UserDto> users = userService.getUsers();
        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> resultList = query.getResultList();
        assertThat(users.get(0).getName(), equalTo(resultList.get(0).getName()));
    }
}
