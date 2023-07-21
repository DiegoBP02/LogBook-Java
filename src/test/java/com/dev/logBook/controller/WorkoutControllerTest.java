package com.dev.logBook.controller;

import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.controller.exceptions.InvalidMuscleEnumException;
import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import com.dev.logBook.services.WorkoutService;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;
import com.dev.logBook.services.exceptions.UniqueConstraintViolationError;
import com.dev.logBook.services.utils.ExerciseComparator;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkoutControllerTest extends ApplicationConfigTest {

    private static final String PATH = "/workouts";

    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    WorkoutDto WORKOUT_DTO_RECORD = WorkoutDto.builder()
            .date(LocalDate.now())
            .muscle(Muscles.CHEST)
            .lowerRepsRange(8)
            .upperRepsRange(12)
            .build();
    Workout WORKOUT_RECORD = Workout.builder()
            .date(WORKOUT_DTO_RECORD.getDate())
            .muscle(WORKOUT_DTO_RECORD.getMuscle())
            .user(USER_RECORD)
            .lowerRepsRange(WORKOUT_DTO_RECORD.getLowerRepsRange())
            .upperRepsRange(WORKOUT_DTO_RECORD.getUpperRepsRange())
            .build();
    ExerciseDto EXERCISE_DTO_RECORD = ExerciseDto.builder()
            .name("name")
            .weight(BigDecimal.valueOf(50))
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
    private WorkoutService workoutService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(WORKOUT_RECORD, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(EXERCISE_RECORD, "id", UUID.randomUUID());
    }

    @Test
    @WithMockUser
    @DisplayName("should create a workout")
    void create_successful() throws Exception {
        when(workoutService.create(any(WorkoutDto.class))).thenReturn(WORKOUT_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(WORKOUT_RECORD)));

        verify(workoutService, times(1)).create(any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void create_invalidBody() throws Exception {
        WorkoutDto workoutDto = new WorkoutDto();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workoutDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));

        verify(workoutService, never()).create(any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UniqueConstraintViolationError if user already exists in db")
    void register_userAlreadyExists() throws Exception {
        when(workoutService.create(any(WorkoutDto.class)))
                .thenThrow(UniqueConstraintViolationError.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UniqueConstraintViolationError));

        verify(workoutService, times(1)).create(any(WorkoutDto.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void create_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).create(any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of workouts")
    void findAll_success() throws Exception {
        List<Workout> workouts = Collections.singletonList(WORKOUT_RECORD);
        when(workoutService.findAll()).thenReturn(workouts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(workouts.size())))
                .andExpect(jsonPath("$[0].id", is(WORKOUT_RECORD.getId().toString())));

        verify(workoutService, times(1)).findAll();
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

        verify(workoutService, never()).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("should return a workout")
    void findById_success() throws Exception {
        when(workoutService.findById(any(UUID.class))).thenReturn(WORKOUT_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(WORKOUT_RECORD)));

        verify(workoutService, times(1)).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void findById_noExerciseFound() throws Exception {
        when(workoutService.findById(any(UUID.class))).thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1)).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void findById_invalidCheckOwnership() throws Exception {
        when(workoutService.findById(any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void findById_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).findById(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a workout")
    void findByDateAndMuscle_success() throws Exception {
        when(workoutService.findByDateAndMuscle(any(LocalDate.class), any(Muscles.class)))
                .thenReturn(WORKOUT_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/date/" + WORKOUT_RECORD.getDate()
                        + "/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(WORKOUT_RECORD)));

        verify(workoutService, times(1))
                .findByDateAndMuscle(any(LocalDate.class), any(Muscles.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void findByDateAndMuscle_noExerciseFound() throws Exception {
        when(workoutService.findByDateAndMuscle(any(LocalDate.class), any(Muscles.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/date/" + WORKOUT_RECORD.getDate()
                        + "/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .findByDateAndMuscle(any(LocalDate.class), any(Muscles.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void findByDateAndMuscle_invalidCheckOwnership() throws Exception {
        when(workoutService.findByDateAndMuscle(any(LocalDate.class), any(Muscles.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/date/" + WORKOUT_RECORD.getDate()
                        + "/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .findByDateAndMuscle(any(LocalDate.class), any(Muscles.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw InvalidMuscleEnumException " +
            "if muscle is not a valid enum from Muscles enum")
    void findByDateAndMuscle_invalidMuscleEnum() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/date/" + WORKOUT_RECORD.getDate()
                        + "/" + "invalidMuscle")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof InvalidMuscleEnumException));

        verify(workoutService, never())
                .findByDateAndMuscle(any(LocalDate.class), any(Muscles.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void findByDateAndMuscle_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/date/" + WORKOUT_RECORD.getDate()
                        + "/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never())
                .findByDateAndMuscle(any(LocalDate.class), any(Muscles.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of workouts")
    void findWorkoutsByMuscle_success() throws Exception {
        List<Workout> workouts = Collections.singletonList(WORKOUT_RECORD);
        when(workoutService.findWorkoutsByMuscle(any(Muscles.class)))
                .thenReturn(workouts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/muscle/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(workouts.size())))
                .andExpect(jsonPath("$[0].id", is(WORKOUT_RECORD.getId().toString())));

        verify(workoutService, times(1))
                .findWorkoutsByMuscle(any(Muscles.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void findWorkoutsByMuscle_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/muscle/" + WORKOUT_RECORD.getMuscle())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).findWorkoutsByMuscle(any(Muscles.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of exercises")
    void getExercisesOutsideRepsRange_success() throws Exception {
        List<Exercise> exercises = Collections.singletonList(EXERCISE_RECORD);
        when(workoutService.getExercisesOutsideRepsRange(any(UUID.class)))
                .thenReturn(exercises);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/exercisesOutsideRepRange/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(exercises.size())))
                .andExpect(jsonPath("$[0].id", is(EXERCISE_RECORD.getId().toString())));

        verify(workoutService, times(1))
                .getExercisesOutsideRepsRange(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getExercisesOutsideRepsRange_noExerciseFound() throws Exception {
        when(workoutService.getExercisesOutsideRepsRange(any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/exercisesOutsideRepRange/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1)).getExercisesOutsideRepsRange(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getExercisesOutsideRepsRange_invalidCheckOwnership() throws Exception {
        when(workoutService.getExercisesOutsideRepsRange(any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/exercisesOutsideRepRange/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .getExercisesOutsideRepsRange(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void getExercisesOutsideRepsRange_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/exercisesOutsideRepRange/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).getExercisesOutsideRepsRange(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of exercises")
    void getVolumeLoad_success() throws Exception {
        HashMap<String, BigDecimal> expectedResult = new HashMap<>();
        when(workoutService.calculateVolumeLoad(any(UUID.class)))
                .thenReturn(expectedResult);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/volumeLoad/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(workoutService, times(1))
                .calculateVolumeLoad(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getVolumeLoad_noExerciseFound() throws Exception {
        when(workoutService.calculateVolumeLoad(any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/volumeLoad/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1)).calculateVolumeLoad(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getVolumeLoad_invalidCheckOwnership() throws Exception {
        when(workoutService.calculateVolumeLoad(any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/volumeLoad/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .calculateVolumeLoad(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void getVolumeLoad_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/volumeLoad/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).calculateVolumeLoad(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of ExerciseComparator")
    void getWorkoutsComparison_success() throws Exception {
        List<ExerciseComparator> expectedResult = new ArrayList<>();
        when(workoutService.compareWorkouts(any(UUID.class), any(UUID.class)))
                .thenReturn(expectedResult);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/compareWorkouts/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(workoutService, times(1))
                .compareWorkouts(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getWorkoutsComparison_noExerciseFound() throws Exception {
        when(workoutService.compareWorkouts(any(UUID.class), any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/compareWorkouts/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .compareWorkouts(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getWorkoutsComparison_invalidCheckOwnership() throws Exception {
        when(workoutService.compareWorkouts(any(UUID.class), any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/compareWorkouts/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .compareWorkouts(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void getWorkoutsComparison_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/compareWorkouts/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never()).compareWorkouts(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of exercises")
    void getUniqueOldWorkoutExercises_success() throws Exception {
        List<Exercise> expectedResult = new ArrayList<>();
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenReturn(expectedResult);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueOldExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getUniqueOldWorkoutExercises_noExerciseFound() throws Exception {
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueOldExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getUniqueOldWorkoutExercises_invalidCheckOwnership() throws Exception {
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueOldExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void getUniqueOldWorkoutExercises_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueOldExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never())
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should return a list of exercises")
    void getUniqueCurrentWorkoutExercises_success() throws Exception {
        List<Exercise> expectedResult = new ArrayList<>();
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenReturn(expectedResult);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueCurrentExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getUniqueCurrentWorkoutExercises_noExerciseFound() throws Exception {
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueCurrentExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getUniqueCurrentWorkoutExercises_invalidCheckOwnership() throws Exception {
        when(workoutService.getUniqueWorkoutExercises
                (any(UUID.class), any(UUID.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueCurrentExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void getUniqueCurrentWorkoutExercises_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/uniqueCurrentExercises/" + UUID.randomUUID()
                        + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never())
                .getUniqueWorkoutExercises(any(UUID.class), any(UUID.class));
    }


    @Test
    @WithMockUser
    @DisplayName("should update a workout")
    void update_success() throws Exception {
        when(workoutService.update(any(UUID.class), any(WorkoutDto.class)))
                .thenReturn(WORKOUT_RECORD);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(WORKOUT_RECORD)));

        verify(workoutService, times(1))
                .update(any(UUID.class), any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw MethodArgumentNotValidException for invalid request body")
    void update_invalidBody() throws Exception {
        WorkoutDto workoutDto = new WorkoutDto();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workoutDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof MethodArgumentNotValidException));

        verify(workoutService, never()).update(any(UUID.class), any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void update_noExerciseFound() throws Exception {
        when(workoutService.update(any(UUID.class), any(WorkoutDto.class)))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .update(any(UUID.class), any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void update_invalidCheckOwnership() throws Exception {
        when(workoutService.update(any(UUID.class), any(WorkoutDto.class)))
                .thenThrow(UnauthorizedAccessException.class);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(WORKOUT_DTO_RECORD));

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .update(any(UUID.class), any(WorkoutDto.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void update_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .patch(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never())
                .update(any(UUID.class), any(WorkoutDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should delete an workout")
    void delete_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(workoutService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void delete_noExerciseFound() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(workoutService).delete(any(UUID.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof ResourceNotFoundException));

        verify(workoutService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void delete_invalidCheckOwnership() throws Exception {
        doThrow(UnauthorizedAccessException.class)
                .when(workoutService).delete(any(UUID.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResolvedException()
                                instanceof UnauthorizedAccessException));

        verify(workoutService, times(1))
                .delete(any(UUID.class));
    }

    @Test
    @DisplayName("should return 403 - Forbidden if user is not authenticated")
    void delete_invalidUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(PATH + "/" + WORKOUT_RECORD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals
                        ("Access Denied", result.getResponse().getErrorMessage()));

        verify(workoutService, never())
                .delete(any(UUID.class));
    }
}
