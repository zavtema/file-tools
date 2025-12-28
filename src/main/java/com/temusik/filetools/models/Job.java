package com.temusik.filetools.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
public class Job {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY) // Одна связь описанная с двух сторон, это нужно для Hibernate
    private List<JobFile> files = new ArrayList<>(); // mappedBy показывает, где уже описана эта связь, он идет в JobFile(т.к. в поле этот класс) в после job

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "progress", nullable = false)
    private Integer progress = 0;

    @Column(name = "options_json")
    private String optionsJson;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID(); // По умолчанию всегда идет null, поэтому всегда присваиваю новые id
        if (createdAt == null) createdAt = Instant.now();
        if (progress == null) progress = 0;
    }
}