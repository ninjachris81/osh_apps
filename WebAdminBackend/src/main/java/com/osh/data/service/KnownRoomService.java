package com.osh.data.service;

import com.osh.data.entity.KnownRoom;
import java.util.Optional;

import com.osh.data.repository.KnownRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class KnownRoomService {

    private final KnownRoomRepository repository;

    public KnownRoomService(KnownRoomRepository repository) {
        this.repository = repository;
    }

    public Optional<KnownRoom> get(String id) {
        return repository.findById(id);
    }

    public KnownRoom update(KnownRoom entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<KnownRoom> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<KnownRoom> list(Pageable pageable, Specification<KnownRoom> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
