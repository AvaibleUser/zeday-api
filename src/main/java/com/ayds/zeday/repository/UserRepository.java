package com.ayds.zeday.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    <U> Optional<U> findById(Long id, Class<U> type);

    <U> Optional<U> findByEmail(String email, Class<U> type);

    boolean existsByEmail(String email);
}
