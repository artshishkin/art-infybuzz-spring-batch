package net.shyshkin.study.batch.databasemigration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.databasemigration.mysql.entity.StudentMysql;
import net.shyshkin.study.batch.databasemigration.posgresql.entity.Student;
import net.shyshkin.study.batch.databasemigration.processor.MappingProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EntityManagerFactory posgresqlEntityManagerFactory;
    private final EntityManagerFactory mysqlEntityManagerFactory;
    private final MappingProcessor processor;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<Student, StudentMysql>chunk(3)
                .reader(jpaItemReader(-1, -1))
                .processor(processor)
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    JpaCursorItemReader<Student> jpaItemReader(
            @Value("#{jobParameters['maxItemCount'] ?: -1}") int maxItemCount,
            @Value("#{jobParameters['skipItems'] ?: 0}") int skipItems
    ) {

        return new JpaCursorItemReaderBuilder<Student>()
                .name("jpaItemReader")
                .entityManagerFactory(posgresqlEntityManagerFactory)
//                .queryString("select s from Student s")
                .queryString("from Student")
                .currentItemCount(skipItems)
                .maxItemCount(maxItemCount)
                .build();
    }

    @Bean
    JpaItemWriter<StudentMysql> jpaItemWriter() {
        return new JpaItemWriterBuilder<StudentMysql>()
                .entityManagerFactory(mysqlEntityManagerFactory)
                .build();
    }

}
