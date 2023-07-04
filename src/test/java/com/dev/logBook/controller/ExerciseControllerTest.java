package com.dev.logBook.controller;

import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import com.dev.logBook.services.ExerciseService;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExerciseControllerTest extends ApplicationConfigTest {

    private static final String PATH = "/exercises";

    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    Workout WORKOUT_RECORD = Workout.builder()
            .date(LocalDate.now())
            .muscle(Muscles.CHEST)
            .user(USER_RECORD)
            .lowerRepsRange(8)
            .upperRepsRange(12)
            .build();
    ExerciseDto EXERCISE_DTO_RECORD = ExerciseDto.builder()
            .name("name")
            .weight(50)
            .reps(10)
            .rir(0)
            .build();
    Exercise EXERCISE_RECORD = Exercise.builder()
            .name(EXERCISE_DTO_RECORD.getName())
            .weight(EXERCISE_DTO_RECORD.getWeight())
            .reps(EXERCISE_DTO_RECORD.getReps())
            .rir(EXERCISE_DTO_RECORD.getRir())
            .workout(WORKOUT_RECORD)
            .user(USER_RECORD)
            .build();

    @MockBean
    private ExerciseService exerciseService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(EXERCISE_RECORD, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(WORKOUT_RECORD, "id", UUID.randomUUID());
        EXERCISE_DTO_RECORD.setWorkoutId(WORKOUT_RECORD.getId());
    }

    @Test
    @WithMockUser
    @DisplayName("should create an exercise")
    void create_successful() throws Exception {
        when(exerciseService.create(any(ExerciseDto.class))).thenReturn(EXERCISE_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EXERCISE_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(EXERCISE_RECORD)));

        verify(exerciseService, times(1)).create(any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void create_invalidBody() throws Exception {
        ExerciseDto exerciseDto = new ExerciseDto();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exerciseDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));

        verify(exerciseService, never()).create(any(ExerciseDto.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void create_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EXERCISE_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(exerciseService, never()).create(any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of exercises")
    void findAll_success() throws Exception {
        List<Exercise> exercises = Collections.singletonList(EXERCISE_RECORD);
        when(exerciseService.findAll()).thenReturn(exercises);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(exercises.size())))
                .andExpect(jsonPath("$[0].name", is(EXERCISE_RECORD.getName())));

        verify(exerciseService, times(1)).findAll();
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void findAll_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(exerciseService, never()).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("should return an exercise")
    void findById_success() throws Exception {
        when(exerciseService.findById(any(UUID.class))).thenReturn(EXERCISE_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(EXERCISE_RECORD)));

        verify(exerciseService, times(1)).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void findById_noExerciseFound() throws Exception {
        when(exerciseService.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(exerciseService, times(1)).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void findById_invalidCheckOwnership() throws Exception {
        when(exerciseService.findById(any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(exerciseService, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void findById_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(exerciseService, never()).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should update an exercise")
    void update_success() throws Exception {
        when(exerciseService.update(any(UUID.class), any(ExerciseDto.class)))
                .thenReturn(EXERCISE_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EXERCISE_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(EXERCISE_RECORD)));

        verify(exerciseService, times(1))
                .update(any(UUID.class), any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void update_invalidBody() throws Exception {
        ExerciseDto exerciseDto = new ExerciseDto();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exerciseDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));

        verify(exerciseService, never()).update(any(UUID.class), any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void update_noExerciseFound() throws Exception {
        when(exerciseService.update(any(UUID.class), any(ExerciseDto.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EXERCISE_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(exerciseService, times(1))
                .update(any(UUID.class), any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void update_invalidCheckOwnership() throws Exception {
        when(exerciseService.update(any(UUID.class), any(ExerciseDto.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EXERCISE_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(exerciseService, times(1))
                .update(any(UUID.class), any(ExerciseDto.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void update_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(exerciseService, never())
                .update(any(UUID.class), any(ExerciseDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should delete an exercise")
    void delete_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(exerciseService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void delete_noExerciseFound() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(exerciseService).delete(any(UUID.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(exerciseService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void delete_invalidCheckOwnership() throws Exception {
        doThrow(UnauthorizedAccessException.class)
                .when(exerciseService).delete(any(UUID.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(exerciseService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void delete_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + EXERCISE_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(exerciseService, never())
                .delete(any(UUID.class));
    }


}
