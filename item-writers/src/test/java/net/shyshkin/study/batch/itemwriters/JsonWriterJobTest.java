package net.shyshkin.study.batch.itemwriters;

import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("json-item-writer")
class JsonWriterJobTest extends AbstractJobTest {

    private static final String TEST_OUTPUT = "../OutputFiles/students.json";

    @Autowired
    JsonFileItemWriter<Student> itemWriter;

    @Autowired
    StudentService studentService;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("outputFile", TEST_OUTPUT);
        return paramsBuilder.toJobParameters();
    }

    @Test
    void jsonWriterJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertLineCount(12, new FileSystemResource(TEST_OUTPUT));
        assertCorrectPropertyNames();
    }

    @Test
    void jsonWriterStepTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Just Writing Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(10));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertLineCount(12, new FileSystemResource(TEST_OUTPUT));
        assertCorrectPropertyNames();
    }

    @Test
    void jsonWriterStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(defaultJobParameters());

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            itemWriter.open(stepExecution.getExecutionContext());
            itemWriter.write(mockStudents());
            itemWriter.close();
            return null;
        });

        //then
        AssertFile.assertLineCount(12, new FileSystemResource(TEST_OUTPUT));
        assertCorrectPropertyNames();
    }

    private List<Student> mockStudents() {
        return studentService.mockStudents();
    }

    private void assertCorrectPropertyNames() throws IOException {
        assertThat(Files.readAllLines(new FileSystemResource(TEST_OUTPUT).getFile().toPath()))
                .anySatisfy(row -> assertThat(row).contains("first_name"))
                .anySatisfy(row -> assertThat(row).contains("last_name"));
    }

}