package com.temusik.filetools.processing;

import com.temusik.filetools.JobStatus.JobFileRole;
import com.temusik.filetools.JobStatus.JobType;

import com.temusik.filetools.dto.StoredObject;
import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.repository.JobFileRepository;
import com.temusik.filetools.services.JobService;
import com.temusik.filetools.storage.StorageService;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.UUID;
import java.util.List;

@Component
public class PdfMergeProcessor implements JobProcessor {

    private final StorageService storageService;
    private final JobFileRepository jobFileRepository;
    private final JobService jobService;

    public PdfMergeProcessor(StorageService storageService, JobFileRepository jobFileRepository, JobService jobService) {
        this.storageService = storageService;
        this.jobFileRepository = jobFileRepository;
        this.jobService = jobService;
    }

    @Override
    public JobType type() {
        return JobType.PDF_MERGE;
    }

    @Override
    public void process(UUID jobId) {
        List<JobFile> files = jobFileRepository.findAllByJob_Id(jobId).stream()
                .filter(f -> f.getRole() == JobFileRole.INPUT)
                .sorted(Comparator.comparing(JobFile::getCreatedAt))
                .toList();
        if (files.isEmpty()) throw new IllegalStateException("No INPUT files to merge");

        PDFMergerUtility merger = new PDFMergerUtility();

        try (PDDocument destination = new PDDocument()) { // try-with-resources, "создай эти объекты, а после выхода из блока автоматически закрой их"
            for (JobFile f : files) {
                Resource r = storageService.loadAsResource(f.getStorageKey());
                try (InputStream in = r.getInputStream(); // можно создавать объекты через ;
                     PDDocument src = Loader.loadPDF(new RandomAccessReadBuffer(in))) {
                    merger.appendDocument(destination,src);
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            destination.save(out);
            byte[] pdfBytes = out.toByteArray();
            String storageKey = "jobs/" + jobId + "/output/" + UUID.randomUUID() + "-merged.pdf";
            StoredObject storedObject = storageService.saveBytes(jobId, storageKey, pdfBytes, "application/pdf");

            jobService.addFile(
                    jobId,
                    JobFileRole.OUTPUT,
                    storedObject.originalName(),
                    storedObject.contentType(),
                    storedObject.size(),
                    storedObject.storageKey()
            );

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

