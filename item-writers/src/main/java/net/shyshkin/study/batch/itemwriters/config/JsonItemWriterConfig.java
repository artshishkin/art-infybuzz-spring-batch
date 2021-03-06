package net.shyshkin.study.batch.itemwriters.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.model.StudentJson;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("json-item-writer")
public class JsonItemWriterConfig {

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
                .<Student, StudentJson>chunk(3)
                .reader(itemReader)
                .processor(processor())
                .writer(jsonItemWriter(null))
                .build();
    }

    private ItemProcessor<Student, StudentJson> processor() {
        return student -> StudentJson.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .build();
    }

    @Bean
    @StepScope
    JsonFileItemWriter<StudentJson> jsonItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource resource) {

        JacksonJsonObjectMarshaller<StudentJson> marshaller = new JacksonJsonObjectMarshaller<>();

        return new JsonFileItemWriterBuilder<StudentJson>()
                .name("jsonItemWriter")
                .resource(resource)
                .jsonObjectMarshaller(marshaller)
                .append(false)
                .build();
    }
}
