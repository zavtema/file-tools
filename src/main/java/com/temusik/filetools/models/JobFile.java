package com.temusik.filetools.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "job_files")
@RequiredArgsConstructor
public class JobFile {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Это поле используется только для объяснения связи Hibernate между таблицами
    // Полное описание работы в файле на компе
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;

    @Column(name = "role",nullable = false, length = 16)
    private String role;

    @Column(name = "original_name", nullable = false)
    private String original_name;

    @Column(name = "content_type", length = 128)
    private String content_type;

    @Column(name = "size_bytes", nullable = false)
    private Long size_bytes;

    @Column(name = "storage_key", nullable = false)
    private String storage_key;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (created_at == null) created_at = Instant.now();
    }
}
