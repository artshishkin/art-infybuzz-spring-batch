package net.shyshkin.study.batch.itemreaders.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentJson;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("json-item-reader")
public class JsonItemReaderConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<StudentJson, StudentJson>chunk(3)
                .reader(jsonItemReader(null))
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    JsonItemReader<StudentJson> jsonItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource resource) {
        return new JsonItemReaderBuilder<StudentJson>()
                .name("jsonItemReader")
                .resource(resource)
                .jsonObjectReader(new JacksonJsonObjectReader<>(StudentJson.class))
                .build();
    }

}
