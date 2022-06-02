package net.shyshkin.study.batch.gettingstarted.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.model.JobParamsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"section04", "section05"})
class JobControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JobExplorer jobExplorer;

    @ParameterizedTest
    @ValueSource(strings = {"First Job", "Second Job"})
    void startExistingJob(String jobName) {

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/job/start/{jobName}", List.of(), String.class, jobName);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Job `" + jobName + "` Started...");
        awaitJobComplete(jobName);
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
        awaitJobComplete(jobName);
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

    private void awaitJobComplete(String jobName) {
        await()
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .timeout(5, TimeUnit.SECONDS)
                .until(
                        (Callable<JobExecution>) () -> {
                            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(jobName);
                            JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
                            log.debug("Get Last Job Execution: {}", lastJobExecution);
                            return lastJobExecution;
                        },
                        allOf(
                                notNullValue(),
                                hasProperty("exitStatus", equalTo(ExitStatus.COMPLETED))
                        )
                );
    }

}