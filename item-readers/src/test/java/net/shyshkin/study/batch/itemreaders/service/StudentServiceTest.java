package net.shyshkin.study.batch.itemreaders.service;

import net.shyshkin.study.batch.itemreaders.model.StudentResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false"
})
@ActiveProfiles("external-api")
@Disabled("Only For Manual Testing - Start `/external-api/docker-compose.yml` first")
class StudentServiceTest {

    @Autowired
    StudentService studentService;

    @Test
    void restCallToGetStudents() throws IOException, InterruptedException {

        //when
        List<StudentResponse> studentResponses = studentService.restCallToGetStudents();

        //then
        assertThat(studentResponses)
                .hasSize(10)
                .allSatisfy(st -> assertThat(st).hasNoNullFieldsOrProperties());
    }
}