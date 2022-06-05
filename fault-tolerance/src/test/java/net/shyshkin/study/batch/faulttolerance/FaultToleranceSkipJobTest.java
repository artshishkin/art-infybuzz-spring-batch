package net.shyshkin.study.batch.faulttolerance;

import net.shyshkin.study.batch.faulttolerance.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.AssertFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("fault-tolerance-skip")
class FaultToleranceSkipJobTest extends AbstractJobTest {

    public static final String SKIP_READER_OUTPUT_FILE = "../OutputFiles/Chunk Job/Fault Tolerance Step/reader/student-skip.txt";
    public static final String SKIP_PROCESSOR_OUTPUT_FILE = "../OutputFiles/Chunk Job/Fault Tolerance Step/processor/student-skip.txt";
    public static final String SKIP_WRITER_OUTPUT_FILE = "../OutputFiles/Chunk Job/Fault Tolerance Step/writer/student-skip.txt";

    @Autowired
    FlatFileItemReader<Student> itemReader;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of(SKIP_READER_OUTPUT_FILE));
        Files.deleteIfExists(Path.of(SKIP_PROCESSOR_OUTPUT_FILE));
        Files.deleteIfExists(Path.of(SKIP_WRITER_OUTPUT_FILE));
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", "../InputFiles/students-fault.csv");
        paramsBuilder.addString("skipInReaderFile", SKIP_READER_OUTPUT_FILE);
        paramsBuilder.addString("skipInProcessorFile", SKIP_PROCESSOR_OUTPUT_FILE);
        paramsBuilder.addString("skipInWriterFile", SKIP_WRITER_OUTPUT_FILE);
        return paramsBuilder.toJobParameters();
    }

    @Test
    void skipJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertLineCount(2, new FileSystemResource(SKIP_READER_OUTPUT_FILE));
        AssertFile.assertLineCount(1, new FileSystemResource(SKIP_PROCESSOR_OUTPUT_FILE));
        AssertFile.assertLineCount(1, new FileSystemResource(SKIP_WRITER_OUTPUT_FILE));
    }

    @Test
    void skipStepTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Fault Tolerance Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertAll(
                        () -> assertThat(execution.getWriteCount()).isEqualTo(6),
                        () -> assertThat(execution.getReadCount()).isEqualTo(8),
                        () -> assertThat(execution.getReadSkipCount()).isEqualTo(2),
                        () -> assertThat(execution.getProcessSkipCount()).isEqualTo(1),
                        () -> assertThat(execution.getWriteSkipCount()).isEqualTo(1)
                ));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertLineCount(2, new FileSystemResource(SKIP_READER_OUTPUT_FILE));
        AssertFile.assertLineCount(1, new FileSystemResource(SKIP_PROCESSOR_OUTPUT_FILE));
        AssertFile.assertLineCount(1, new FileSystemResource(SKIP_WRITER_OUTPUT_FILE));
    }
}
