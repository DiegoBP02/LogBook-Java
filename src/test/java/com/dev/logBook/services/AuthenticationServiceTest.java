package com.dev.logBook.services;

import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.controller.dto.LoginDTO;
import com.dev.logBook.controller.dto.RegisterDTO;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest extends ApplicationConfigTest {

    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    RegisterDTO REGISTER_DTO_RECORD = new RegisterDTO(USER_RECORD.getUsername(), USER_RECORD.getPassword(), USER_RECORD.getEmail());
    LoginDTO LOGIN_DTO_RECORD = new LoginDTO(USER_RECORD.getUsername(), USER_RECORD.getPassword());

    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("should return an user")
    void loadUserByUsername() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(USER_RECORD);

        UserDetails result = authenticationService.loadUserByUsername(anyString());

        assertEquals(USER_RECORD, result);

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException if there is no user")
    void loadUserByUsernameUsernameNotFoundException() {
        String username = USER_RECORD.getUsername();

        when(userRepository.findByUsername(anyString()))
                .thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                authenticationService.loadUserByUsername(username));

        assertEquals("Username not found: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("should return a token")
    void register() {
        String token = "token";
        when(tokenService.generateToken(any(User.class))).thenReturn(token);

        String result = authenticationService.register(REGISTER_DTO_RECORD);

        assertEquals(token, result);

        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateKeyException if user already exists")
    void registerDuplicateKeyException() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(anyString()));

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            authenticationService.register(REGISTER_DTO_RECORD);
        });

        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("should return a token")
    void login() {
        String token = "token";
        Authentication authenticate = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticate);
        when(authenticate.getPrincipal()).thenReturn(USER_RECORD);
        when(tokenService.generateToken(any(User.class))).thenReturn(token);

        String result = authenticationService.login(LOGIN_DTO_RECORD);

        assertEquals(token, result);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authenticate, times(1)).getPrincipal();
        verify(tokenService, times(1)).generateToken(any(User.class));
    }

}