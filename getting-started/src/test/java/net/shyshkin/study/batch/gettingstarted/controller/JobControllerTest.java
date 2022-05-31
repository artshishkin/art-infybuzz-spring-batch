package net.shyshkin.study.batch.gettingstarted.controller;

import net.shyshkin.study.batch.gettingstarted.model.JobParamsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

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
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/job/start/{jobName}", List.of(), String.class, jobName);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Job `" + jobName + "` Started...");
    }

    @ParameterizedTest
    @ValueSource(strings = {"First Job", "Second Job"})
    void startExistingJob_withRequestParameters(String jobName) {

        //given
        List<JobParamsRequest> jobParamsRequestList = dummyJobParamsRequest();
        RequestEntity<List<JobParamsRequest>> requestEntity = RequestEntity.method(HttpMethod.POST, "/api/job/start/{jobName}", jobName).body(jobParamsRequestList);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Job `" + jobName + "` Started...");
    }

    private List<JobParamsRequest> dummyJobParamsRequest() {
        return List.of(
                new JobParamsRequest("param1", UUID.randomUUID().toString()),
                new JobParamsRequest("param2", UUID.randomUUID().toString())
        );
    }

    @Test
    void startAbsentJob() {
        //given
        String jobName = "Absent Job";

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/job/start/{jobName}", List.of(), String.class, jobName);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Job with name `" + jobName + "` absent");
    }

}