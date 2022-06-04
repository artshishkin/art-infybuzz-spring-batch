package net.shyshkin.study.batch.itemwriters.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemwriters.model.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("jdbc-item-writer")
public class JdbcItemWriterConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final ItemReader<Student> itemReader;
    private final DataSource dataSource;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Writing Step")
                .<Student, Student>chunk(3)
                .reader(itemReader)
                .writer(jdbcItemWriter())
                .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Student> jdbcItemWriter() {

        return new JdbcBatchItemWriterBuilder<Student>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("insert into students (id,first_name,last_name,email) values (:id,:firstName,:lastName,:email)")
                .build();
    }
}
