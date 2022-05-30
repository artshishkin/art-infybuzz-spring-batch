package net.shyshkin.study.batch.gettingstarted.listener;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.model.DummyModel;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class FirstJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.debug("Before Job: {}", jobExecution.getJobInstance().getJobName());
        log.debug("Job Parameters: {}", jobExecution.getJobParameters());
        log.debug("Job Execution Context: {}", jobExecution.getExecutionContext());

        jobExecution.getExecutionContext().putString("jec", "jec value");
        DummyModel art = DummyModel.builder()
                .id(123)
                .name("Art")
                .price(new BigDecimal("99.99"))
                .build();
        jobExecution.getExecutionContext().put("art", art);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.debug("After Job: {}", jobExecution.getJobInstance().getJobName());
        log.debug("Job Parameters: {}", jobExecution.getJobParameters());
        log.debug("Job Execution Context: {}", jobExecution.getExecutionContext());
        log.debug("Job Exit Status: {}", jobExecution.getExitStatus());
        var art = (DummyModel) jobExecution.getExecutionContext().get("art");
        assert art != null;
        log.debug("Art price: {}", art.getPrice());
    }
}
