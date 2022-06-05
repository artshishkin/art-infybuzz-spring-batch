package net.shyshkin.study.batch.databasemigration.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subjects_learning")
public class SubjectsLearning {

    @Id
    private Long id;
    @Column(name = "sub_name")
    private String subName;
    @Column(name = "marks_obtained")
    private Long marksObtained;

}
