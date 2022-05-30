package net.shyshkin.study.batch.gettingstarted.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public void startJob(Job job) {
        try {
            JobExecution jobExecution = jobLauncher.run(job, new JobParameters(Map.of("currentTime", new JobParameter(new Date()))));
            log.debug("Job Execution Id: {}", jobExecution.getJobId());
        } catch (Exception e) {
            log.error("Error in Batch Job", e);
        }
    }

}
