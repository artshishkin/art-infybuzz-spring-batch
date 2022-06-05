package net.shyshkin.study.batch.itemreaders;

import net.shyshkin.study.batch.itemreaders.model.StudentCsv;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("csv-item-reader")
class CsvReaderJobTest extends AbstractJobTest {

    @Autowired
    FlatFileItemReader<StudentCsv> itemReader;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", "../InputFiles/students.csv");
        return paramsBuilder.toJobParameters();
    }

    @Test
    void csvReaderJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void csvReaderStepTest() {

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
    void csvReaderStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(defaultJobParameters());

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            StudentCsv studentCsvRead;
            itemReader.open(stepExecution.getExecutionContext());
            while ((studentCsvRead = itemReader.read()) != null) {

                //then
                StudentCsv studentCsv = studentCsvRead;
                assertAll(
                        () -> assertThat(studentCsv).hasNoNullFieldsOrProperties(),
                        () -> assertThat(studentCsv.getId()).isGreaterThan(0),
                        () -> assertThat(studentCsv.getFirstName()).isNotEmpty(),
                        () -> assertThat(studentCsv.getLastName()).isNotEmpty(),
                        () -> assertThat(studentCsv.getEmail()).isNotEmpty()
                );
            }
            itemReader.close();
            return null;
        });
    }
}
