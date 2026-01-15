package com.temusik.filetools.processing;

import com.temusik.filetools.JobStatus.JobType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobProcessorRegistry {
    private final Map<JobType, JobProcessor> processors;
    public JobProcessorRegistry(List<JobProcessor> processorsList) {
        this.processors = processorsList.stream()
                .collect(Collectors.toMap(
                        JobProcessor::type,
                        p -> p
                ));
    }

    public JobProcessor get(JobType type) {
        JobProcessor processor = processors.get(type);
        if (processor == null) {
            throw new IllegalStateException("No processor for job type: " + type);
        }
        return processor;
    }
}

