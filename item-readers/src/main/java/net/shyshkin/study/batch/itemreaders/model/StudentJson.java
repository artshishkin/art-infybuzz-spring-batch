package net.shyshkin.study.batch.itemreaders.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentJson {

    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    private String lastName;
    private String email;

}
