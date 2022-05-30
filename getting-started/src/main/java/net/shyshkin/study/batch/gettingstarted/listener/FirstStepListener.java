package net.shyshkin.study.batch.gettingstarted.listener;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.model.DummyModel;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class FirstStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.debug("Before Step {}", stepExecution.getStepName());
        log.debug("Inside Step -> Job Execution Context {}", stepExecution.getJobExecution().getExecutionContext());
        log.debug("Step Execution Context {}", stepExecution.getExecutionContext());
        stepExecution.getExecutionContext().put("sec", "sec value");
        stepExecution.getExecutionContext().put(
                "kate",
                DummyModel.builder()
                        .id(321)
                        .name("Kate")
                        .price(new BigDecimal("432.99"))
                        .build()
        );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.debug("After Step {}", stepExecution.getStepName());
        log.debug("Step Execution Context {}", stepExecution.getExecutionContext());
        return ExitStatus.COMPLETED;
    }
}
