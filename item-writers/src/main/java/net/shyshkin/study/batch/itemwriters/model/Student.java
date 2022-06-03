package net.shyshkin.study.batch.itemwriters.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
