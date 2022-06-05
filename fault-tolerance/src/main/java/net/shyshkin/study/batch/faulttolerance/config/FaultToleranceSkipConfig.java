package net.shyshkin.study.batch.faulttolerance.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.faulttolerance.exception.SomeExceptionInProcessor;
import net.shyshkin.study.batch.faulttolerance.listener.StudentSkipListener;
import net.shyshkin.study.batch.faulttolerance.model.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("fault-tolerance-skip")
public class FaultToleranceSkipConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final StudentSkipListener studentSkipListener;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Fault Tolerance Step")
                .<Student, Student>chunk(3)
                .reader(csvItemReader(null))
                .processor((ItemProcessor<Student, Student>) student -> {
                    if (student.getId() == 2L) {
                        throw new SomeExceptionInProcessor("Because id=2");
                    }
                    return student;
                })
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .faultTolerant()
                .skip(Throwable.class)
//                .skipLimit(Integer.MAX_VALUE)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(studentSkipListener)
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<Student> csvItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource resource) {
        return new FlatFileItemReaderBuilder<Student>()
                .name("csvItemReader")
                .linesToSkip(1)
                .resource(resource)
                .delimited()
                .delimiter(",")
                .names("ID", "First Name", "Last Name", "Email")
                .targetType(Student.class)
                .build();
    }

}
