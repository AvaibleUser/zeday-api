package com.ayds.zeday.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    <R> List<R> findAllByBusinessIdOrBusinessId(Long businessId1, Long businessId2, Class<R> type);

    List<RoleEntity> findAllByIdInAndBusinessId(Iterable<Long> ids, Long businessId);

    <R> Optional<R> findByIdAndBusinessId(Long id, Long businessId, Class<R> type);

    Optional<RoleEntity> findByName(String name);

    boolean existsByNameAndBusinessId(String name, Long businessId);
}
