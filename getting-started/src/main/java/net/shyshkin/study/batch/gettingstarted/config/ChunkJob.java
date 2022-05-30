package net.shyshkin.study.batch.gettingstarted.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.gettingstarted.processor.FirstItemProcessor;
import net.shyshkin.study.batch.gettingstarted.reader.FirstItemReader;
import net.shyshkin.study.batch.gettingstarted.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("section05")
public class ChunkJob {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final FirstItemReader reader;
    private final FirstItemProcessor processor;
    private final FirstItemWriter writer;

    @Bean
    Job secondJob() {
        return jobs.get("Second Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .next(secondStep())
                .build();
    }

    private Step firstChunkStep() {
        return steps.get("First Chunk Step")
                .<Integer, Long>chunk(3)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    private Step secondStep() {
        return steps.get("Second Tasklet Step")
                .tasklet((stepContribution, chunkContext) -> {
                    log.debug("Inside {}", chunkContext.getStepContext().getStepName());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
