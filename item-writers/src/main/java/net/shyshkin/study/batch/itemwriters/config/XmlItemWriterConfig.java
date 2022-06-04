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
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Profile("xml-item-writer")
public class XmlItemWriterConfig {

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
                .writer(xmlItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    StaxEventItemWriter<Student> xmlItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource resource) {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Student.class);

        return new StaxEventItemWriterBuilder<Student>()
                .name("xmlItemWriter")
                .resource(resource)
                .rootTagName("students")
                .marshaller(marshaller)
                .build();
    }
}
