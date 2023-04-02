package com.osh.data.repository;

import com.osh.data.entity.KnownArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KnownAreaRepository extends JpaRepository<KnownArea, String>, JpaSpecificationExecutor<KnownArea> {

    Page<KnownArea> findByOrderByDisplayOrderAsc(Pageable pageable);
}
