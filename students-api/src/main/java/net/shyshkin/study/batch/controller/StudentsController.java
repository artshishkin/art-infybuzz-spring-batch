package net.shyshkin.study.batch.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import net.shyshkin.study.batch.model.Student;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Controller("/api/v1")
public class StudentsController {

    private final List<Student> students = LongStream
            .rangeClosed(1, 10)
            .mapToObj(i -> Student.builder()
                    .id(i)
                    .firstName("FirstName" + i)
                    .lastName("LastName" + i)
                    .email("student" + i + "@gmail.com")
                    .build())
            .collect(Collectors.toList());

    @Get("/students")
    public List<Student> students() {
        return students;
    }
}
