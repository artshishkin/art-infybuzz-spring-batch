package net.shyshkin.study.batch.faulttolerance.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.faulttolerance.exception.RetryableException;
import net.shyshkin.study.batch.faulttolerance.exception.SomeExceptionInProcessor;
import net.shyshkin.study.batch.faulttolerance.exception.SomeExceptionInWriter;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

import java.util.concurrent.atomic.AtomicInteger;

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
                .processor(processor())
                .writer(itemWriter())
                .faultTolerant()
                .skip(Throwable.class)
//                .skipLimit(Integer.MAX_VALUE)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .retry(RetryableException.class)
                .retryLimit(4)
                .listener(studentSkipListener)
                .build();
    }

    private ItemProcessor<Student, Student> processor() {
        return new ItemProcessor<Student, Student>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Student process(Student student) throws Exception {

                log.debug("Trying to process {}", student);

                if (student.getId() == 2L) {
                    throw new SomeExceptionInProcessor("Because id=2");
                }

                if (student.getId() == 5L && counter.incrementAndGet() % 3 != 0) {
                    throw new RetryableException("Can be retried, count: " + counter.get() % 3);
                }

                return student;
            }
        };
    }

    private ItemWriter<Student> itemWriter() {
        return list -> {
            list.forEach(item -> {
                if (item.getId() == 9)
                    throw new SomeExceptionInWriter("Because id=9");
                log.debug("Writing: {}", item);
            });
        };
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
