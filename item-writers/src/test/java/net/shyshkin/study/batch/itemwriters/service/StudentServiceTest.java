package net.shyshkin.study.batch.itemwriters.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemwriters.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false",
        "app.external.students-api.url=http://${SERVICES_HOST}:${SERVICES_PORT}/api/v1/"
})
@ContextConfiguration(initializers = StudentServiceTest.Initializer.class)
@ActiveProfiles("external-api")
@Testcontainers
class StudentServiceTest {

    @Autowired
    StudentService studentService;

    @Container
    protected static GenericContainer<?> studentsApiContainer = new GenericContainer<>("artarkatesoft/art-infybuzz-students-api:native")
            .withExposedPorts(8080)
            .waitingFor(Wait.forLogMessage(".*io.micronaut.runtime.Micronaut - Startup completed in.*\\n", 1));

    @Test
    void restCallToPostStudent() throws IOException, InterruptedException {
        //given
        Student student = Student.builder()
                .id(12L)
                .firstName("Art")
                .lastName("Shyshkin")
                .email("d.art.shyshkin@gmail.com")
                .build();

        //when
        Student studentResponse = studentService.restCallToPostStudent(student);

        //then
        assertThat(studentResponse).satisfies(st -> assertAll(
                () -> assertThat(st).hasNoNullFieldsOrProperties(),
                () -> log.debug("{}", st)
        ));
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