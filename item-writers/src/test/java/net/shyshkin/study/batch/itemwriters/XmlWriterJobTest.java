package net.shyshkin.study.batch.itemwriters;

import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("xml-item-writer")
class XmlWriterJobTest extends AbstractJobTest {

    private static final String TEST_OUTPUT = "../OutputFiles/students.xml";

    @Autowired
    StaxEventItemWriter<Student> itemWriter;

    @Autowired
    StudentService studentService;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("outputFile", TEST_OUTPUT);
        return paramsBuilder.toJobParameters();
    }

    @Test
    void xmlWriterJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        assertStudentContent();
    }

    @Test
    void xmlWriterStepTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Just Writing Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(10));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        assertStudentContent();
    }

    @Test
    void xmlWriterStepScopeTest() throws Exception {
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
        assertStudentContent();
    }

    private List<Student> mockStudents() {
        return studentService.mockStudents();
    }

    private void assertStudentContent() throws IOException {
        String content = Files.readString(new FileSystemResource(TEST_OUTPUT).getFile().toPath());
        assertThat(content.split("<student>"))
                .hasSize(11)
                .anySatisfy(st -> assertThat(st)
                        .contains("<id>")
                        .contains("<first_name>")
                        .contains("<last_name>")
                        .contains("<email>")
                );
    }

}