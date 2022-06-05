package net.shyshkin.study.batch.faulttolerance;

import net.shyshkin.study.batch.faulttolerance.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("fault-tolerance-skip")
class FaultToleranceSkipJobTest extends AbstractJobTest {

    @Autowired
    FlatFileItemReader<Student> itemReader;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", "../InputFiles/students-fault.csv");
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

    }

    @Test
    void skipStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Fault Tolerance Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(8));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }
}
