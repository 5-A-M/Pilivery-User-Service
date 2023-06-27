package com.fiveam.userservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fiveam.userservice.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail( String email);
    Optional<User> findByOauthId(String oauthId);
    Optional<User> findByDisplayName( String displayName);
    Optional<User> findByPhone( String phoneNumber);



}
