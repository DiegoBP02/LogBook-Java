package com.dev.logBook.services;

import com.dev.logBook.entities.User;
import com.dev.logBook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void create(User user) {
        userRepository.save(user);
    }

    public User findByAuth0Id(String auth0Id) {
        return userRepository.findByAuth0Id(auth0Id);
    }

    public User findByRawAuth0Id(String auth0IdRaw) {
        String auth0Id = auth0IdRaw.substring(auth0IdRaw.lastIndexOf("|") + 1);
        return userRepository.findByAuth0Id(auth0Id);
    }
}
