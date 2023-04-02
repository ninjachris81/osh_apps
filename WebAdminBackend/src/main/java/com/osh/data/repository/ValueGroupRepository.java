package com.osh.data.repository;

import com.osh.data.entity.ValueGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ValueGroupRepository extends JpaRepository<ValueGroup, String>, JpaSpecificationExecutor<ValueGroup> {

}
