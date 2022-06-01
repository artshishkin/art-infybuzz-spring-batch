package net.shyshkin.study.batch.gettingstarted.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.batch.gettingstarted.model.JobParamsRequest;
import net.shyshkin.study.batch.gettingstarted.service.JobService;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final JobOperator jobOperator;

    @PostMapping("/start/{jobName}")
    public ResponseEntity<String> startJob(@PathVariable String jobName,
                                           @RequestBody(required = false) List<JobParamsRequest> requestParameters) {

        return jobService.getJobByName(jobName)
                .map(job -> {
                    jobService.startJob(job, requestParameters);
                    return ResponseEntity.ok("Job `" + jobName + "` Started...");
                })
                .orElse(ResponseEntity.badRequest().body("Job with name `" + jobName + "` absent"));
    }

    @GetMapping("/stop/{jobExecutionId}")
    public String stopJob(@PathVariable long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        boolean stopped = jobOperator.stop(jobExecutionId);
        return stopped ? "Job stopped..." : "Can not stop the Job...";
    }

}
