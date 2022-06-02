package net.shyshkin.study.batch.itemreaders.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class ChunkJob {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    @Bean
    Job secondJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("First Chunk Step")
                .<Integer, Integer>chunk(3)
                .reader(new ListItemReader<>(IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList())))
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }
}
