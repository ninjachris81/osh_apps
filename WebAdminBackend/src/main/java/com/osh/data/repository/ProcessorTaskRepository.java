package com.osh.data.repository;

import com.osh.data.entity.ProcessorTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProcessorTaskRepository
        extends
            JpaRepository<ProcessorTask, String>,
            JpaSpecificationExecutor<ProcessorTask> {

}
