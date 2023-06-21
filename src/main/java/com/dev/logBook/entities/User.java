package com.dev.logBook.entities;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private String auth0_id;
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Workout> workouts = new ArrayList<>();

    public User (DecodedJWT jwt){
        this.email = getUserEmailFromJwt(jwt);
        this.auth0_id = getAuth0IdFromJwt(jwt);
    }

    private String getUserEmailFromJwt(DecodedJWT jwt){
        return jwt.getClaims().get("email").asString();
    }

    private String getAuth0IdFromJwt(DecodedJWT jwt) {
        String subject = jwt.getSubject();
        return subject.substring(subject.lastIndexOf("|") + 1);
    }
}