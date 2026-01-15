package com.temusik.filetools.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temusik.filetools.JobStatus.JobFileRole;
import com.temusik.filetools.JobStatus.JobType;
import com.temusik.filetools.dto.StoredObject;
import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.services.JobService;
import com.temusik.filetools.storage.StorageService;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class PdfDeletePagesProcessor implements JobProcessor {

    private final JobService jobService;
    private final StorageService storageService;

    public PdfDeletePagesProcessor(JobService jobService, StorageService storageService) {
        this.jobService = jobService;
        this.storageService = storageService;
    }

    @Override
    public JobType type() {
        return JobType.PDF_DELETE_PAGES;
    }

    @Override
    public void process(UUID jobId) {

        List<JobFile> inputs = jobService.getFiles(jobId).stream()
                .filter(f -> f.getRole() == JobFileRole.INPUT)
                .sorted(Comparator.comparing(JobFile::getCreatedAt))
                .toList();
        if (inputs.size() != 1) {
            throw new IllegalArgumentException("Expected exactly 1 INPUT PDF, got: " + inputs.size());
        }
        JobFile input = inputs.get(0);

        Resource r = storageService.loadAsResource(input.getStorageKey()); // Получил доступ к конкретному файлу в storage

        try (InputStream in = r.getInputStream();
             PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(in))) {
            PDPageTree pages = document.getPages();
            int total = pages.getCount();

            String optionsJson = jobService.getJobOrThrow(jobId).getOptionsJson();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(optionsJson);
            JsonNode pagesNode = root.get("pages");

            if (pagesNode == null || !pagesNode.isArray()) {
                throw new IllegalArgumentException("pages must be an array");
            }

            List<Integer> toDelete = new ArrayList<>();

            for (JsonNode n : pagesNode) {
                int page1Based = n.asInt();
                if (page1Based < 1 || page1Based > total) {
                    throw new IllegalArgumentException("Page out of range: " + page1Based);
                }
                toDelete.add(page1Based - 1);
            }
            toDelete.sort(Comparator.reverseOrder());

            for (int idx : toDelete) {
                pages.remove(idx);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            byte[] pdfBytes = out.toByteArray();

            String storageKey = "jobs/" + jobId + "/output/" + UUID.randomUUID() + "-deleted-pages.pdf";
            StoredObject stored = storageService.saveBytes(
                    jobId,
                    storageKey,
                    pdfBytes,
                    "application/pdf"
            );

            jobService.addFile(
                    jobId,
                    JobFileRole.OUTPUT,
                    stored.originalName(),
                    stored.contentType(),
                    stored.size(),
                    stored.storageKey()
            );
        } catch (Exception e) {
            throw new RuntimeException("PDF delete pages failed", e);
        }
    }
}
