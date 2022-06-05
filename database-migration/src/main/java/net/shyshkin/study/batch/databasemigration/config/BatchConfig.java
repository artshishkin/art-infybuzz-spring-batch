package net.shyshkin.study.batch.databasemigration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.databasemigration.posgresql.entity.Department;
import net.shyshkin.study.batch.databasemigration.posgresql.entity.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final DataSource universityDataSource;
    private final EntityManagerFactory posgresqlEntityManagerFactory;
    private final EntityManagerFactory mysqlEntityManagerFactory;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<Student, Student>chunk(3)
                .reader(jpaItemReader(-1, -1))
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    JdbcCursorItemReader<Department> jdbcItemReader(
            @Value("#{jobParameters['maxItemCount'] ?: -1}") int maxItemCount,
            @Value("#{jobParameters['skipItems'] ?: 0}") int skipItems
    ) {

        return new JdbcCursorItemReaderBuilder<Department>()
                .name("jdbcItemReader")
                .dataSource(universityDataSource)
                .sql("select id, dept_name as deptName from department")
                .beanRowMapper(Department.class)
                .currentItemCount(skipItems)
                .maxRows(maxItemCount)
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

}
