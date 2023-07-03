package com.dev.logBook.services;

import com.dev.logBook.controller.dto.LoginDTO;
import com.dev.logBook.controller.dto.RegisterDTO;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.repositories.UserRepository;
import com.dev.logBook.services.exceptions.UniqueConstraintViolationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return user;
    }

    public String register(RegisterDTO register) {
        try {
            User user = new User(register.getUsername(), register.getEmail(),
                    passwordEncoder.encode(register.getPassword()), Role.ROLE_USER);
            userRepository.save(user);
            return tokenService.generateToken(user);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationError("user", "username or email");
        }
    }

    public String login(LoginDTO login) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

        Authentication authentication = this.authenticationManager.authenticate
                (usernamePasswordAuthenticationToken);

        User user = (User) authentication.getPrincipal();

        return tokenService.generateToken(user);
    }
}
