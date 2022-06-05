package net.shyshkin.study.batch.faulttolerance.listener;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.faulttolerance.exception.SomeExceptionInProcessor;
import net.shyshkin.study.batch.faulttolerance.exception.SomeExceptionInWriter;
import net.shyshkin.study.batch.faulttolerance.model.Student;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.SkipListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Slf4j
@StepScope
@Component
@Profile("fault-tolerance-skip")
public class StudentSkipListener extends SkipListenerSupport<Student, Student> {

    private final FileSystemResource skipInReaderResource;
    private final FileSystemResource skipInProcessorResource;
    private final FileSystemResource skipInWriterResource;

    public StudentSkipListener(
            @Value("#{jobParameters['skipInReaderFile']}") FileSystemResource skipInReaderResource,
            @Value("#{jobParameters['skipInProcessorFile']}") FileSystemResource skipInProcessorResource,
            @Value("#{jobParameters['skipInWriterFile']}") FileSystemResource skipInWriterResource
    ) {
        this.skipInReaderResource = skipInReaderResource;
        this.skipInProcessorResource = skipInProcessorResource;
        this.skipInWriterResource = skipInWriterResource;
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(skipInReaderResource.getFile().toPath().getParent());
            Files.createDirectories(skipInProcessorResource.getFile().toPath().getParent());
            Files.createDirectories(skipInWriterResource.getFile().toPath().getParent());
        } catch (IOException e) {
            log.debug("Error while creating directories: {}", e.getMessage());
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException) {
            FlatFileParseException ex = (FlatFileParseException) t;
            writeToSkipFile(skipInReaderResource.getFile(), ex.getInput());
        }
    }

    @Override
    public void onSkipInProcess(Student student, Throwable t) {
        if (t instanceof SomeExceptionInProcessor) {
            writeToSkipFile(skipInProcessorResource.getFile(), student + " - " + t.getMessage());
        }
    }

    @Override
    public void onSkipInWrite(Student student, Throwable t) {
        if (t instanceof SomeExceptionInWriter) {
            writeToSkipFile(skipInWriterResource.getFile(), student + " - " + t.getMessage());
        }
    }

    private void writeToSkipFile(File outputFile, String message) {
        try (FileWriter fileWriter = new FileWriter(outputFile, true)) {
            fileWriter.write(message + " - " + LocalDateTime.now() + "\r\n");
        } catch (IOException e) {
            log.debug("Error while writing to skip logs: {}", e.getMessage());
        }
    }

}
