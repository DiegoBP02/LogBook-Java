package com.dev.logBook.repositories;

import com.dev.logBook.entities.User;
import com.dev.logBook.entities.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository subject;

    @AfterEach
    void tearDown() throws Exception {
        subject.deleteAll();
    }

    @Test
    @DisplayName("should find an user by username")
    void findByUsername() throws Exception {
        User USER_RECORD = new User("username", "email",
                "password", Role.ROLE_USER);
        subject.save(USER_RECORD);

        User result = subject.findByUsername(USER_RECORD.getUsername());

        assertEquals(USER_RECORD, result);
    }
}