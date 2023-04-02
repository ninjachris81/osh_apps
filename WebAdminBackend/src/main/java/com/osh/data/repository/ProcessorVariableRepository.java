package com.osh.data.repository;

import com.osh.data.entity.ProcessorVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProcessorVariableRepository
        extends
            JpaRepository<ProcessorVariable, String>,
            JpaSpecificationExecutor<ProcessorVariable> {

}
