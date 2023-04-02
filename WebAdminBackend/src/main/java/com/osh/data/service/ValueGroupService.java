package com.osh.data.service;

import com.osh.data.entity.ValueGroup;
import java.util.Optional;

import com.osh.data.repository.ValueGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ValueGroupService {
2
    private final ValueGroupRepository repository;

    public ValueGroupService(ValueGroupRepository repository) {
        this.repository = repository;
    }

    public Optional<ValueGroup> get(String id) {
        return repository.findById(id);
    }

    public ValueGroup update(ValueGroup entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<ValueGroup> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ValueGroup> list(Pageable pageable, Specification<ValueGroup> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
