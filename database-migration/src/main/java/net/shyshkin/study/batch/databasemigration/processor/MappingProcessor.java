package net.shyshkin.study.batch.databasemigration.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.databasemigration.mappers.StudentMapper;
import net.shyshkin.study.batch.databasemigration.mysql.entity.StudentMysql;
import net.shyshkin.study.batch.databasemigration.posgresql.entity.Student;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MappingProcessor implements ItemProcessor<Student, StudentMysql> {

    private final StudentMapper studentMapper;

    @Override
    public StudentMysql process(Student student) throws Exception {
        log.debug("Processing {}", student.getId());
        return studentMapper.toMysql(student);
    }
}
