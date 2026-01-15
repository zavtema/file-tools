package com.temusik.filetools.repository;

import com.temusik.filetools.models.JobFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface JobFileRepository extends JpaRepository<JobFile, UUID> {
    List<JobFile> findAllByJob_Id(UUID id);

}