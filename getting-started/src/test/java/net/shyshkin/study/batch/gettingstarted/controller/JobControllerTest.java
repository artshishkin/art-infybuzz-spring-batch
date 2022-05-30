package net.shyshkin.study.batch.gettingstarted.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"section04", "section05"})
class JobControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @ParameterizedTest
    @ValueSource(strings = {"First Job", "Second Job"})
    void startExistingJob(String jobName) {

        //when
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/job/start/{jobName}", String.class, jobName);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Job `" + jobName + "` Started...");
    }

    @Test
    void startAbsentJob() {
        //given
        String jobName = "Absent Job";

        //when
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/api/job/start/{jobName}", String.class, jobName);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Job with name `" + jobName + "` absent");
    }

}