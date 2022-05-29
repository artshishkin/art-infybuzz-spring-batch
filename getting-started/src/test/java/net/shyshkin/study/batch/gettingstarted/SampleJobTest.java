package net.shyshkin.study.batch.gettingstarted;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

import static org.assertj.core.api.Assertions.assertThat;

//@ContextConfiguration(classes = {SampleJob.class})
class SampleJobTest extends AbstractJobTest {

    @Test
    void sampleJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("First Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void firstStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("First Step");
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void secondStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Second Step");
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }
}
