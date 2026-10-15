package com.cabshare.repository;

import com.cabshare.model.RideGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideGroupRepository extends JpaRepository<RideGroup, Long> {
}
