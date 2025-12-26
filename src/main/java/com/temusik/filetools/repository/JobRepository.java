package com.temusik.filetools.repository;

import com.temusik.filetools.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    Optional<Job> findById(UUID id); // Не надо писать, есть автоматически, но написал, чтобы ... потренироваться
}