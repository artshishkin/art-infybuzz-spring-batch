package net.shyshkin.study.batch.itemreaders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCsv {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
