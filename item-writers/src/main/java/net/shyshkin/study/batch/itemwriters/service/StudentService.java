package net.shyshkin.study.batch.itemwriters.service;

import net.shyshkin.study.batch.itemwriters.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
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

}
