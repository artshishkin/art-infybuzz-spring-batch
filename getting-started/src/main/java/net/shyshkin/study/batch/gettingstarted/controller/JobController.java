package net.shyshkin.study.batch.gettingstarted.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.batch.gettingstarted.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/start/{jobName}")
    public ResponseEntity<String> startJob(@PathVariable String jobName) {

        return jobService.getJobByName(jobName)
                .map(job -> {
                    jobService.startJob(job);
                    return ResponseEntity.ok("Job `" + jobName + "` Started...");
                })
                .orElse(ResponseEntity.badRequest().body("Job with name `" + jobName + "` absent"));
    }

}
