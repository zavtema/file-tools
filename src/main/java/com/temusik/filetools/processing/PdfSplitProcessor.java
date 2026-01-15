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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class PdfSplitProcessor implements JobProcessor {

    private final JobService jobService;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    public PdfSplitProcessor(JobService jobService, StorageService storageService, ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType type() {
        return JobType.PDF_SPLIT;
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

        try {
            String optionsJson = jobService.getJobOrThrow(jobId).getOptionsJson();
            if (optionsJson == null || optionsJson.isBlank()) {
                throw new IllegalArgumentException("optionsJson is required for PDF_SPLIT");
            }

            JsonNode root = objectMapper.readTree(optionsJson);
            JsonNode splitAtNode = root.get("splitAt");
            if (splitAtNode == null || !splitAtNode.canConvertToInt()) {
                throw new IllegalArgumentException("splitAt must be an integer");
            }

            int splitAt1Based = splitAtNode.asInt();
            if (splitAt1Based < 1) {
                throw new IllegalArgumentException("splitAt must be >= 1");
            }

            Resource r = storageService.loadAsResource(input.getStorageKey());
            try (InputStream in = r.getInputStream();
                 PDDocument source = Loader.loadPDF(new RandomAccessReadBuffer(in))) {

                int total = source.getNumberOfPages();
                if (splitAt1Based >= total) {
                    throw new IllegalArgumentException("splitAt must be < total pages (" + total + ")");
                }

                try (PDDocument part1 = new PDDocument();
                     PDDocument part2 = new PDDocument()) {

                    int splitAt0 = splitAt1Based - 1;

                    for (int i = 0; i <= splitAt0; i++) {
                        part1.addPage(source.getPage(i));
                    }
                    for (int i = splitAt0 + 1; i < total; i++) {
                        part2.addPage(source.getPage(i));
                    }

                    saveOutput(jobId, part1, "part-1.pdf");
                    saveOutput(jobId, part2, "part-2.pdf");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("PDF split failed", e);
        }
    }

    private void saveOutput(UUID jobId, PDDocument doc, String filename) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            doc.save(out);
            byte[] bytes = out.toByteArray();

            String storageKey = "jobs/" + jobId + "/output/" + UUID.randomUUID() + "-" + filename;
            StoredObject stored = storageService.saveBytes(jobId, storageKey, bytes, "application/pdf");

            jobService.addFile(
                    jobId,
                    JobFileRole.OUTPUT,
                    stored.originalName(),
                    stored.contentType(),
                    stored.size(),
                    stored.storageKey()
            );
        }
    }
}
