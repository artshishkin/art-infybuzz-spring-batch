package net.shyshkin.study.batch.itemreaders.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile({"external-api","external-api-list"})
public class StudentService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.external.students-api.url}")
    private URI serverUrl;

    public List<StudentResponse> restCallToGetStudents() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverUrl.resolve("students"))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), new TypeReference<List<StudentResponse>>() {
        });
    }

}
