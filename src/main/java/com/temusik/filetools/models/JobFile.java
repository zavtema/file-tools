package com.temusik.filetools.models;

import com.temusik.filetools.JobStatus.JobFileRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "job_files")
@NoArgsConstructor
public class JobFile {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Это поле используется только для объяснения связи Hibernate между таблицами
    // Полное описание работы в файле на компе
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private JobFileRole role;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "content_type", length = 128)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }
}
