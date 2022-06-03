package net.shyshkin.study.batch.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Introspected
public class Student {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
