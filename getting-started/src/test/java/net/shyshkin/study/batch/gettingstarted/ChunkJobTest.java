package net.shyshkin.study.batch.gettingstarted;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

//@ContextConfiguration(classes = {SampleJob.class})
@ActiveProfiles("section05")
class ChunkJobTest extends AbstractJobTest {

    @Test
    @DirtiesContext
    void sampleJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Second Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    @DirtiesContext
    void firstChunkStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("First Chunk Step");
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(stepExecution -> assertAll(
                                () -> assertThat(stepExecution.getReadCount()).isEqualTo(10),
                                () -> assertThat(stepExecution.getWriteCount()).isEqualTo(10)
                        )
                );
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void secondTaskletStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Second Tasklet Step");
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

}
