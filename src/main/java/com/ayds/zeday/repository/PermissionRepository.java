package com.ayds.zeday.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    List<PermissionEntity> findAllByIdInAndScheduleBusinessId(Iterable<Long> ids, Long businessId);

}
