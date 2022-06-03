package net.shyshkin.study.batch.itemreaders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemreaders.model.StudentResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("external-api")
public class StudentServiceAdapter {

    private final StudentService studentService;

    private Queue<StudentResponse> studentQueue;

    public StudentResponse getStudent(long id, String name) throws IOException, InterruptedException {

        log.debug("Student's fake id: {}, name: {}", id, name);

        if (studentQueue == null) {
            studentQueue = new LinkedList<>(studentService.restCallToGetStudents());
        }
        StudentResponse studentResponse = studentQueue.poll();
        if (studentResponse == null) studentQueue = null;
        return studentResponse;
    }

}
