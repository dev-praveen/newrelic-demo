package com.praveen.learn.repository;

import com.praveen.learn.entity.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneModelRepository extends JpaRepository<PhoneEntity, String> {}
