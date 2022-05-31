package net.shyshkin.study.batch.gettingstarted.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.model.JobParamsRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobLauncher jobLauncher;
    private final List<Job> jobs;

    private Map<String, Job> jobMap;

    @PostConstruct
    void init() {
        jobMap = jobs.stream().collect(Collectors.toMap(Job::getName, Function.identity()));
    }

    public Optional<Job> getJobByName(String jobName) {
        return Optional.ofNullable(jobMap.get(jobName));
    }

    @Async
    public void startJob(Job job, List<JobParamsRequest> requestParameters) {
        try {
            Map<String, JobParameter> params = new HashMap<>();
            requestParameters.forEach(requestParameter -> params.put(requestParameter.getParamKey(), new JobParameter(requestParameter.getParamValue())));
            params.put("currentTime", new JobParameter(new Date()));
            JobParameters jobParameters = new JobParameters(params);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.debug("Job Execution Id: {}", jobExecution.getJobId());
        } catch (Exception e) {
            log.error("Error in Batch Job", e);
        }
    }

}
