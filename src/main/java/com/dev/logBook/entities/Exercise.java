package com.dev.logBook.entities;

import com.dev.logBook.entities.models.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private String name;
    @ElementCollection()
    private List<Set> sets;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}


