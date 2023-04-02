package com.osh.data.service;

import com.osh.data.entity.ProcessorVariable;
import java.util.Optional;

import com.osh.data.repository.ProcessorVariableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProcessorVariableService {

    private final ProcessorVariableRepository repository;

    public ProcessorVariableService(ProcessorVariableRepository repository) {
        this.repository = repository;
    }

    public Optional<ProcessorVariable> get(String id) {
        return repository.findById(id);
    }

    public ProcessorVariable update(ProcessorVariable entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<ProcessorVariable> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ProcessorVariable> list(Pageable pageable, Specification<ProcessorVariable> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
