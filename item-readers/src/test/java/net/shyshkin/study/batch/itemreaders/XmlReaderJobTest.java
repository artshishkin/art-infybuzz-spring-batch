package net.shyshkin.study.batch.itemreaders;

import net.shyshkin.study.batch.itemreaders.model.StudentXml;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("xml-item-reader")
class XmlReaderJobTest extends AbstractJobTest {

    @Autowired
    StaxEventItemReader<StudentXml> itemReader;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", "../InputFiles/students.xml");
        return paramsBuilder.toJobParameters();
    }

    @Test
    void xmlReaderJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Nested
    class ReadAllItemsTests {
        @Test
        void xmlReaderStepTest() {

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
        void xmlReaderStepScopeTest() throws Exception {
            //given
            StepExecution stepExecution = MetaDataInstanceFactory
                    .createStepExecution(defaultJobParameters());

            //when
            StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                StudentXml studentXmlRead;
                itemReader.open(stepExecution.getExecutionContext());
                while ((studentXmlRead = itemReader.read()) != null) {

                    //then
                    StudentXml studentXml = studentXmlRead;
                    assertAll(
                            () -> assertThat(studentXml).hasNoNullFieldsOrProperties(),
                            () -> assertThat(studentXml.getId()).isGreaterThan(0),
                            () -> assertThat(studentXml.getFirstName()).isNotEmpty(),
                            () -> assertThat(studentXml.getLastName()).isNotEmpty(),
                            () -> assertThat(studentXml.getEmail()).isNotEmpty()
                    );
                }
                itemReader.close();
                return null;
            });
        }

    }

    @Nested
    class ReadPartItemsTests {

        JobParameters jobParameters = new JobParametersBuilder(defaultJobParameters())
                .addString("maxItemCount", "7")
                .addLong("skipItems", 2L)
                .toJobParameters();

        @Test
        void xmlReaderStepTest() {

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
        void xmlReaderStepScopeTest() throws Exception {
            //given
            StepExecution stepExecution = MetaDataInstanceFactory
                    .createStepExecution(jobParameters);

            //when
            StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                StudentXml studentXmlRead;
                itemReader.open(stepExecution.getExecutionContext());
                while ((studentXmlRead = itemReader.read()) != null) {

                    //then
                    StudentXml studentXml = studentXmlRead;
                    assertAll(
                            () -> assertThat(studentXml).hasNoNullFieldsOrProperties(),
                            () -> assertThat(studentXml.getId()).isGreaterThan(2),
                            () -> assertThat(studentXml.getId()).isLessThanOrEqualTo(7),
                            () -> assertThat(studentXml.getFirstName()).isNotEmpty(),
//                        () -> assertThat(studentXml.getLastName()).isNotEmpty(),
                            () -> assertThat(studentXml.getEmail()).isNotEmpty()
                    );
                }
                itemReader.close();
                return null;
            });
        }

    }

}
