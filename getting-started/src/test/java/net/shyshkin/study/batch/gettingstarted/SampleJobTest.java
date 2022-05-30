package net.shyshkin.study.batch.gettingstarted;

import net.shyshkin.study.batch.gettingstarted.model.DummyModel;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@ContextConfiguration(classes = {SampleJob.class})
@ActiveProfiles("section04")
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
        System.out.println(jobExecution.getExecutionContext());
        assertThat(jobExecution.getExecutionContext())
                .satisfies(executionContext -> assertThat(executionContext.get("jec")).isEqualTo("jec value"))
                .satisfies(executionContext -> assertThat(executionContext.get("art"))
                        .isInstanceOf(DummyModel.class)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", 123)
                        .hasFieldOrPropertyWithValue("name", "Art")
                        .hasFieldOrPropertyWithValue("price", new BigDecimal("99.99"))
                );
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

        //given
        ExecutionContext jobExecutionContext = new ExecutionContext(
                Map.of(
                        "jec", "jec value",
                        "art", DummyModel.builder()
                                .id(123)
                                .name("Art")
                                .price(new BigDecimal("123.99"))
                                .build()
                )
        );

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Second Step", jobExecutionContext);
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        Optional<StepExecution> stepExecutionOptional = jobExecution.getStepExecutions()
                .stream()
                .filter(stepExecution -> "Second Step".equals(stepExecution.getStepName()))
                .findAny();
        assertThat(stepExecutionOptional)
                .hasValueSatisfying(stepExecution -> assertThat(stepExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"));

    }
}
