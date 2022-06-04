package net.shyshkin.study.batch.itemwriters;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.batch.itemwriters.model.Student;
import net.shyshkin.study.batch.itemwriters.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("jdbc-item-writer")
@SqlGroup({
        @Sql(scripts = {"classpath:jdbc/students-schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(statements = {"truncate table students"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class JdbcWriterJobTest extends AbstractJobTest {

    @Autowired
    JdbcBatchItemWriter<Student> itemWriter;

    @Autowired
    StudentService studentService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        return paramsBuilder.toJobParameters();
    }

    @Test
    void jdbcWriterJobTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualJobInstance.getJobName()).isEqualTo("Chunk Job");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        assertStudentContent();
    }

    @Test
    void jdbcWriterStepTest() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Just Writing Step", defaultJobParameters());
        var actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        //then
        assertThat(actualStepExecutions)
                .hasSize(1)
                .allSatisfy(execution -> assertThat(execution.getWriteCount()).isEqualTo(10));
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        assertStudentContent();
    }

    @Test
    void jdbcWriterStepScopeTest() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory
                .createStepExecution(defaultJobParameters());

        //when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
//            itemWriter.open(stepExecution.getExecutionContext());
            itemWriter.write(mockStudents());
//            itemWriter.close();
            return null;
        });

        //then
        assertStudentContent();
    }

    private List<Student> mockStudents() {
        return studentService.mockStudents();
    }

    private void assertStudentContent() throws IOException {
        Integer count = jdbcTemplate.queryForObject("select count(*) from students", Integer.class);
        assertThat(count).isEqualTo(10L);
    }

}