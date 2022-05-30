package net.shyshkin.study.batch.gettingstarted.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobLauncher jobLauncher;
    private final List<Job> jobs;

    private Map<String, Job> jobMap;

    @PostConstruct
    void init() {
        jobMap = jobs.stream().collect(Collectors.toMap(Job::getName, Function.identity()));
    }

    @GetMapping("/start/{jobName}")
    public ResponseEntity<String> startJob(@PathVariable String jobName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        Job job = jobMap.get(jobName);
        if (job == null)
            return ResponseEntity.badRequest().body("Job with name `" + jobName + "` absent");
        jobLauncher.run(job, new JobParameters(Map.of("currentTime", new JobParameter(new Date()))));
        return ResponseEntity.ok("Job `" + jobName + "` Started...");
    }

}
