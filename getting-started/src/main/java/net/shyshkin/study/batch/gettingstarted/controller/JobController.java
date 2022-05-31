package net.shyshkin.study.batch.gettingstarted.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.batch.gettingstarted.model.JobParamsRequest;
import net.shyshkin.study.batch.gettingstarted.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

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

}
