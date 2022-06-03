package net.shyshkin.study.batch.itemreaders.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentResponse;
import net.shyshkin.study.batch.itemreaders.service.StudentServiceAdapter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("external-api")
public class WebServiceItemReaderConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final StudentServiceAdapter studentServiceAdapter;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<StudentResponse, StudentResponse>chunk(3)
                .reader(webServiceItemReader())
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    ItemReaderAdapter<StudentResponse> webServiceItemReader() {

        var readerAdapter = new ItemReaderAdapter<StudentResponse>();
        readerAdapter.setTargetObject(studentServiceAdapter);
        readerAdapter.setTargetMethod("getStudent");
        readerAdapter.setArguments(new Object[]{1L, "Art"});

        return readerAdapter;
    }

}
