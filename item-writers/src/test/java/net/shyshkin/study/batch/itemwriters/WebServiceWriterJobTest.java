package net.shyshkin.study.batch.itemwriters;

import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@TestPropertySource(properties = {
        "spring.batch.job.enabled=false",
        "app.external.students-api.url=http://${SERVICES_HOST}:${SERVICES_PORT}/api/v1/"
})
@ContextConfiguration(initializers = WebServiceWriterJobTest.Initializer.class)
@ActiveProfiles("external-api")
@Testcontainers
class WebServiceWriterJobTest extends AbstractJobTest {

    @Autowired
    ItemWriterAdapter<Student> itemWriter;

    @SpyBean
    StudentService studentService;

    @Container
    protected static GenericContainer<?> studentsApiContainer = new GenericContainer<>("artarkatesoft/art-infybuzz-students-api:native")
            .withExposedPorts(8080)
            .waitingFor(Wait.forLogMessage(".*io.micronaut.runtime.Micronaut - Startup completed in.*\\n", 1));


    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        return paramsBuilder.toJobParameters();
    }

    @Test
    void webServiceWriterJobTest() throws Exception {

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
    void webServiceWriterStepTest() throws Exception {

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

    private void assertStudentContent() throws IOException, InterruptedException {
        then(studentService).should(times(10)).restCallToPostStudent(any(Student.class));
    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String host = studentsApiContainer.getHost();
            Integer port = studentsApiContainer.getMappedPort(8080);

            System.setProperty("SERVICES_HOST", host);
            System.setProperty("SERVICES_PORT", String.valueOf(port));
        }
    }
}