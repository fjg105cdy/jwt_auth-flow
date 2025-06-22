package com.yian.jwt_authflow.repositories;

import com.yian.jwt_authflow.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;



@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailId(String emailId);
    Optional<User> findByEmailId(String emailId);
}
