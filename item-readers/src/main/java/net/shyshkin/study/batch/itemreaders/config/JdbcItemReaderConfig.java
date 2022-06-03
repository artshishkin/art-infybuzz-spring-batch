package net.shyshkin.study.batch.itemreaders.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentJdbc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("jdbc-item-reader")
public class JdbcItemReaderConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final DataSource datasource;


    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<StudentJdbc, StudentJdbc>chunk(3)
                .reader(jdbcItemReader(-1, -1))
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    JdbcCursorItemReader<StudentJdbc> jdbcItemReader(
            @Value("#{jobParameters['maxItemCount'] ?: -1}") int maxItemCount,
            @Value("#{jobParameters['skipItems'] ?: 0}") int skipItems
    ) {

        return new JdbcCursorItemReaderBuilder<StudentJdbc>()
                .name("webServiceItemReader")
                .dataSource(datasource)
                .sql("select * from students")
                .beanRowMapper(StudentJdbc.class)
                .currentItemCount(skipItems)
                .maxRows(maxItemCount)
                .build();
    }

}
