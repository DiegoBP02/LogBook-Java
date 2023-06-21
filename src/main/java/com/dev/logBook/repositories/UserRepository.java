package com.dev.logBook.repositories;

import com.dev.logBook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByAuth0Id(String auth0Id);
}
