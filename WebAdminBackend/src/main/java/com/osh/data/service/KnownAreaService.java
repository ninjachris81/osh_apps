package com.osh.data.service;

import com.osh.data.entity.KnownArea;
import java.util.Optional;

import com.osh.data.repository.KnownAreaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class KnownAreaService {

    private final KnownAreaRepository repository;

    public KnownAreaService(KnownAreaRepository repository) {
        this.repository = repository;
    }

    public Optional<KnownArea> get(String id) {
        return repository.findById(id);
    }

    public KnownArea update(KnownArea entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<KnownArea> list(Pageable pageable) {
        return repository.findByOrderByDisplayOrderAsc(pageable);
    }

    public Page<KnownArea> list(Pageable pageable, Specification<KnownArea> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
