package net.shyshkin.study.batch.itemreaders.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentXml;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Profile("xml-item-reader")
public class XmlItemReaderConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    @Bean
    Job chunkJob() {
        return jobs.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .build();
    }

    private Step chunkStep() {
        return steps.get("Just Reading Step")
                .<StudentXml, StudentXml>chunk(3)
                .reader(xmlItemReader(null, -1, -1))
                .writer(list -> list.forEach(item -> log.debug("{}", item)))
                .build();
    }

    @Bean
    @StepScope
    StaxEventItemReader<StudentXml> xmlItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource resource,
            @Value("#{jobParameters['maxItemCount'] ?: 0x7fffffff}") int maxItemCount,
            @Value("#{jobParameters['skipItems'] ?: 0}") int skipItems
    ) {

        var unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(StudentXml.class);

        return new StaxEventItemReaderBuilder<StudentXml>()
                .name("xmlItemReader")
                .resource(resource)
                .unmarshaller(unmarshaller)
                .addFragmentRootElements("student")
                .maxItemCount(maxItemCount)
                .currentItemCount(skipItems)
                .build();
    }

}
