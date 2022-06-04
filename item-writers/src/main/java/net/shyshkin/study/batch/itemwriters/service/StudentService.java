package net.shyshkin.study.batch.itemwriters.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.batch.itemwriters.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final List<Student> mockStudents = LongStream
            .rangeClosed(1, 10)
            .mapToObj(i -> Student.builder()
                    .id(i)
                    .firstName("FirstName" + i)
                    .lastName("LastName" + i)
                    .email("student" + i + "@gmail.com")
                    .build())
            .collect(Collectors.toList());

    public List<Student> mockStudents() {
        return mockStudents;
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.external.students-api.url}")
    private URI serverUrl;

    public Student restCallToPostStudent(Student student) throws IOException, InterruptedException {

        String json = objectMapper.writeValueAsString(student);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverUrl.resolve("students"))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), Student.class);
    }

}
