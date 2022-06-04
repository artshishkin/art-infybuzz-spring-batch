package net.shyshkin.study.batch.itemwriters.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

@Configuration
public class AppConfiguration {

    @Bean
    ItemReader<Student> itemReader(StudentService studentService) {
        return new ItemReader<>() {

            private Queue<Student> students;

            @Override
            public Student read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (students == null) students = new LinkedList<>(studentService.mockStudents());
                Student student = students.poll();
                if (student == null) students = null;
                return student;
            }
        };
    }


    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

}
