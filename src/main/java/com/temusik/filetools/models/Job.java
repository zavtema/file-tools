package com.temusik.filetools.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@RequiredArgsConstructor
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
    private String options_json;

    @Column(name = "error_code", length = 64)
    private String error_code;

    @Column(name = "error_message")
    private String error_message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @Column(name = "started_at")
    private Instant started_at;

    @Column(name = "finished_at")
    private Instant finished_at;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID(); // По умолчанию всегда идет null, поэтому всегда присваиваю новые id
        if (created_at == null) created_at = Instant.now();
        if (progress == null) progress = 0;
    }
}