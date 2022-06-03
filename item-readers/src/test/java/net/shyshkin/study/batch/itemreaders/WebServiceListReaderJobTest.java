package net.shyshkin.study.batch.itemreaders;

import net.shyshkin.study.batch.itemreaders.model.StudentResponse;
import net.shyshkin.study.batch.itemreaders.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("external-api-list")
class WebServiceListReaderJobTest extends AbstractJobTest {

    @Autowired
    ListItemReader<StudentResponse> itemReader;

    @MockBean
    StudentService studentService;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        given(studentService.restCallToGetStudents())
                .willReturn(mockStudents());
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        return paramsBuilder.toJobParameters();
    }

    @Test
    void webServiceReaderJobText() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    }

    @Test
    void webServiceReaderStepTest() {

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
    void webServiceReaderStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(defaultJobParameters());

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            StudentResponse studentResponseRead;
//            itemReader.open(stepExecution.getExecutionContext());
            while ((studentResponseRead = itemReader.read()) != null) {

                //then
                StudentResponse studentResponse = studentResponseRead;
                assertAll(
                        () -> assertThat(studentResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(studentResponse.getId()).isGreaterThan(0),
                        () -> assertThat(studentResponse.getFirstName()).isNotEmpty(),
                        () -> assertThat(studentResponse.getLastName()).isNotEmpty(),
                        () -> assertThat(studentResponse.getEmail()).isNotEmpty()
                );
            }
//            itemReader.close();
            return null;
        });
    }

    private List<StudentResponse> mockStudents() {
        return LongStream
                .rangeClosed(1, 10)
                .mapToObj(i -> StudentResponse.builder()
                        .id(i)
                        .firstName("FirstName" + i)
                        .lastName("LastName" + i)
                        .email("student" + i + "@gmail.com")
                        .build())
                .collect(Collectors.toList());
    }

}
