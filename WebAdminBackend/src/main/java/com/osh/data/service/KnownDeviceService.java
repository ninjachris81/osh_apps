package com.osh.data.service;

import com.osh.data.entity.KnownDevice;
import java.util.Optional;

import com.osh.data.repository.KnownDeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class KnownDeviceService {

    private final KnownDeviceRepository repository;

    public KnownDeviceService(KnownDeviceRepository repository) {
        this.repository = repository;
    }

    public Optional<KnownDevice> get(String id) {
        return repository.findById(id);
    }

    public KnownDevice update(KnownDevice entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Page<KnownDevice> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<KnownDevice> list(Pageable pageable, Specification<KnownDevice> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
