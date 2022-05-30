package net.shyshkin.study.batch.gettingstarted.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.model.DummyModel;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SecondTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.debug("Second task is executing...");
        Map<String, Object> jobExecutionContext = chunkContext.getStepContext().getJobExecutionContext();
        log.debug("Job execution context inside Step: {}", jobExecutionContext);
        var art = (DummyModel) jobExecutionContext.get("art");
        log.debug("Art price inside Step: {}", art.getPrice());
        return RepeatStatus.FINISHED;
    }
}
