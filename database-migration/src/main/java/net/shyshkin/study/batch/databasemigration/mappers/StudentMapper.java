package net.shyshkin.study.batch.databasemigration.mappers;

import net.shyshkin.study.batch.databasemigration.mysql.entity.StudentMysql;
import net.shyshkin.study.batch.databasemigration.posgresql.entity.Student;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentMysql toMysql(Student student);

}
