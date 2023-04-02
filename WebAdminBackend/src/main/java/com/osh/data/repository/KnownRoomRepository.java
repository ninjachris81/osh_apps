package com.osh.data.repository;

import com.osh.data.entity.KnownRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KnownRoomRepository extends JpaRepository<KnownRoom, String>, JpaSpecificationExecutor<KnownRoom> {

}
