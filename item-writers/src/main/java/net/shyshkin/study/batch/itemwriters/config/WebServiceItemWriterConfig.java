package net.shyshkin.study.batch.itemwriters.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("external-api")
public class WebServiceItemWriterConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final ItemReader<Student> itemReader;
    private final StudentService studentService;

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
                .writer(webServiceItemWriter())
                .build();
    }

    @Bean
    ItemWriterAdapter<Student> webServiceItemWriter() {

        return new ItemWriterAdapter<>() {
            {
                setTargetObject(studentService);
                setTargetMethod("restCallToPostStudent");
            }
        };
    }
}
