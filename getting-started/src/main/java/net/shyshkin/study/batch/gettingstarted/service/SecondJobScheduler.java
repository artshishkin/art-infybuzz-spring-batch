package net.shyshkin.study.batch.gettingstarted.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecondJobScheduler {

    private final JobService jobService;
    private final Job secondJob;

    //https://www.freeformatter.com/cron-expression-generator-quartz.html
    @Scheduled(cron = "0/10 * * ? * *") //every 10 sec
    public void startSecondJob() {
        log.debug("Start Second Job");
        jobService.startJob(secondJob, List.of());
    }

}
