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

@ActiveProfiles("fault-tolerance-skip")
class FaultToleranceSkipJobTest extends AbstractJobTest {

    public static final String SKIP_READER_OUTPUT_FILE = "../OutputFiles/Chunk Job/Fault Tolerance Step/reader/student-skip.txt";

    @Autowired
    FlatFileItemReader<Student> itemReader;

    @BeforeEach
    void setUp() throws IOException {
        Files.delete(Path.of(SKIP_READER_OUTPUT_FILE));
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", "../InputFiles/students-fault.csv");
        paramsBuilder.addString("skipInReaderFile", SKIP_READER_OUTPUT_FILE);
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
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(8));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertLineCount(2, new FileSystemResource(SKIP_READER_OUTPUT_FILE));
    }
}
