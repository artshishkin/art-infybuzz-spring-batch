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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("csv-item-writer")
public class CsvItemWriterConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final ItemReader<Student> itemReader;

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
                .writer(csvItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemWriter<Student> csvItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource resource) {
        return new FlatFileItemWriterBuilder<Student>()
                .name("csvItemWriter")
                .resource(resource)
                .delimited()
                .delimiter(",")
                .names("id", "firstName", "lastName", "email")
                .append(false)
                .headerCallback(writer -> writer.write("ID,First Name,Last Name,Email"))
                .footerCallback(writer -> writer.write("This file was created " + LocalDateTime.now()))
                .build();
    }
}
