package net.shyshkin.study.batch.gettingstarted.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.listener.FirstJobListener;
import net.shyshkin.study.batch.gettingstarted.listener.FirstStepListener;
import net.shyshkin.study.batch.gettingstarted.model.DummyModel;
import net.shyshkin.study.batch.gettingstarted.service.SecondTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class SampleJob {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final SecondTasklet secondTasklet;
    private final FirstJobListener firstJobListener;
    private final FirstStepListener firstStepListener;

    @Bean
    Job firstJob() {
        return jobs.get("First Job")
                .incrementer(new RunIdIncrementer())
                .listener(firstJobListener)
                .start(firstStep())
                .next(secondStep())
                .build();
    }

    private Step firstStep() {
        return steps.get("First Step")
                .listener(firstStepListener)
                .tasklet(firstTask())
                .build();
    }

    private Tasklet firstTask() {
        return (stepContribution, chunkContext) -> {
            log.debug("{} is executing", chunkContext.getStepContext().getStepName());
            DummyModel kate = (DummyModel) chunkContext.getStepContext().getStepExecutionContext().get("kate");
            log.debug("Model from Step Execution Context: {}", kate);
            return RepeatStatus.FINISHED;
        };
    }

    private Step secondStep() {
        return steps.get("Second Step")
                .tasklet(secondTasklet)
                .build();
    }

}
