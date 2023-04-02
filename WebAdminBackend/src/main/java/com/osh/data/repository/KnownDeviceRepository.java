package com.osh.data.repository;

import com.osh.data.entity.KnownDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KnownDeviceRepository extends JpaRepository<KnownDevice, String>, JpaSpecificationExecutor<KnownDevice> {

}
