package net.shyshkin.study.batch.itemreaders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "student")
public class StudentXml {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
