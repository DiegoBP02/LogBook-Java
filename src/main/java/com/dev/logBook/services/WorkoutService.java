package com.dev.logBook.services;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.repositories.WorkoutRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static com.dev.logBook.services.utils.CheckOwnership.checkOwnership;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserService userService;

    public Workout create(WorkoutDto workoutDTO) {
        User user = getCurrentUser();
        Workout workout = Workout.builder()
                .date(workoutDTO.getDate())
                .muscle(workoutDTO.getMuscle())
                .user(user)
                .build();
        return workoutRepository.save(workout);
    }

    public List<Workout> findAll() {
        User user = getCurrentUser();
        return workoutRepository.findByUserId(user.getId());
    }

    public Workout findById(UUID id) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        checkOwnership(user, id);
        return workout;
    }

    public Workout update(UUID id, WorkoutDto workoutDto) {
        try {
            User user = getCurrentUser();
            Workout entity = workoutRepository.getReferenceById(id);
            checkOwnership(user, id);
            updateData(entity, workoutDto);
            return workoutRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public void delete(UUID id) {
        try {
            User user = getCurrentUser();
            workoutRepository.getReferenceById(id);
            checkOwnership(user, id);
            workoutRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    private void updateData(Workout entity, WorkoutDto obj) {
        entity.setDate(obj.getDate());
        entity.setMuscle(obj.getMuscle());
    }

    private User getCurrentUser() {
        String rawAuth0Id = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return userService.findByRawAuth0Id(rawAuth0Id);
    }
}
