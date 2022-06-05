package net.shyshkin.study.batch.databasemigration.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student")
public class StudentMysql {

    @Id
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Column(name = "is_active", columnDefinition = "tinyint")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "dept_id", referencedColumnName = "id")
    private Department department;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private List<SubjectsLearning> subjectsLearningList;

}
