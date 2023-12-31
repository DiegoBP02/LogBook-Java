package com.dev.logBook.controller;

import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.controller.dto.LoginDTO;
import com.dev.logBook.controller.dto.RegisterDTO;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.services.AuthenticationService;
import com.dev.logBook.services.exceptions.UniqueConstraintViolationError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ApplicationConfigTest {

    private static final String PATH = "/auth";

    User USER_RECORD = new User("username", "email@email.com",
            "password", Role.ROLE_USER);
    RegisterDTO REGISTER_DTO_RECORD = new RegisterDTO(USER_RECORD.getUsername(),
            USER_RECORD.getPassword(), USER_RECORD.getEmail());
    LoginDTO LOGIN_DTO_RECORD = new LoginDTO(USER_RECORD.getUsername(), USER_RECORD.getPassword());

    @MockBean
    private AuthenticationService authenticationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should register a user")
    void register_successful() throws Exception {
        String token = "token";
        when(authenticationService.register(any(RegisterDTO.class))).thenReturn(token);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(REGISTER_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(authenticationService, times(1)).register(any(RegisterDTO.class));
    }

    @Test
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void register_invalidBody() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(registerDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("should throw UniqueConstraintViolationError if user already exists in db")
    void register_userAlreadyExists() throws Exception {
        when(authenticationService.register(any(RegisterDTO.class)))
                .thenThrow(UniqueConstraintViolationError.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(REGISTER_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UniqueConstraintViolationError));

        verify(authenticationService, times(1)).register(any(RegisterDTO.class));
    }

    @Test
    @DisplayName("should login a user")
    void login_successful() throws Exception {
        when(authenticationService.login(any(LoginDTO.class))).thenReturn("token");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(LOGIN_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().string("token"));

        verify(authenticationService, times(1)).login(any(LoginDTO.class));
    }

    @Test
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void login_invalidBody() throws Exception {
        LoginDTO loginDTO = new LoginDTO();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(loginDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));
    }

}
