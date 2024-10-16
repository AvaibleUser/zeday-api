package com.ayds.zeday.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    <U> Optional<U> findUserById(Long id, Class<U> type);

    <U> Optional<U> findByIdAndBusinessId(Long id, Long businessId, Class<U> type);

    <U> Optional<U> findByEmailAndBusinessId(String email, Long businessId, Class<U> type);

    boolean existsByEmailAndBusinessId(String email, Long bussinessId);
}
