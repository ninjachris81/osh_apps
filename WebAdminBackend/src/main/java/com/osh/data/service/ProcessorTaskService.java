package com.osh.data.service;

import com.osh.data.entity.ProcessorTask;
import java.util.Optional;

import com.osh.data.repository.ProcessorTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProcessorTaskService {

    private final ProcessorTaskRepository repository;

    public ProcessorTaskService(ProcessorTaskRepository repository) {
        this.repository = repository;
    }

    public Optional<ProcessorTask> get(String id) {
        return repository.findById(id);
    }

    public ProcessorTask update(ProcessorTask entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<ProcessorTask> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ProcessorTask> list(Pageable pageable, Specification<ProcessorTask> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
