package net.shyshkin.study.batch.itemreaders;

import net.shyshkin.study.batch.itemreaders.model.StudentJdbc;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("jdbc-item-reader")
class JdbcReaderJobTest extends AbstractJobTest {

    @Autowired
    JdbcCursorItemReader<StudentJdbc> itemReader;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        return paramsBuilder.toJobParameters();
    }

    @Test
    void jdbcReaderJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void readAll_jdbcReaderStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Just Reading Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(10));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void readAll_jdbcReaderStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(defaultJobParameters());

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            StudentJdbc studentJdbcRead;
            itemReader.open(stepExecution.getExecutionContext());
            while ((studentJdbcRead = itemReader.read()) != null) {

                //then
                StudentJdbc studentJdbc = studentJdbcRead;
                assertAll(
                        () -> assertThat(studentJdbc).hasNoNullFieldsOrProperties(),
                        () -> assertThat(studentJdbc.getId()).isGreaterThan(0),
                        () -> assertThat(studentJdbc.getFirstName()).isNotEmpty(),
                        () -> assertThat(studentJdbc.getLastName()).isNotEmpty(),
                        () -> assertThat(studentJdbc.getEmail()).isNotEmpty()
                );
            }
            itemReader.close();
            return null;
        });
    }

    JobParameters jobParameters = new JobParametersBuilder(defaultJobParameters())
            .addString("maxItemCount", "7")
            .addLong("skipItems", 2L)
            .toJobParameters();

    @Test
    void readPart_jdbcReaderStepTest() {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Just Reading Step", jobParameters);
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(5));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    void readPart_jdbcReaderStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(jobParameters);

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            StudentJdbc studentJdbcRead;
            itemReader.open(stepExecution.getExecutionContext());
            while ((studentJdbcRead = itemReader.read()) != null) {

                //then
                StudentJdbc studentJdbc = studentJdbcRead;
                assertAll(
                        () -> assertThat(studentJdbc).hasNoNullFieldsOrProperties(),
                        () -> assertThat(studentJdbc.getId()).isGreaterThan(2),
                        () -> assertThat(studentJdbc.getId()).isLessThanOrEqualTo(7),
                        () -> assertThat(studentJdbc.getFirstName()).isNotEmpty(),
                        () -> assertThat(studentJdbc.getLastName()).isNotEmpty(),
                        () -> assertThat(studentJdbc.getEmail()).isNotEmpty()
                );
            }
            itemReader.close();
            return null;
        });
    }

}
