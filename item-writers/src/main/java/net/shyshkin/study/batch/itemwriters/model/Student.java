package net.shyshkin.study.batch.itemwriters.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "student")
public class Student {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @XmlElement(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @XmlElement(name = "last_name")
    public String getLastName() {
        return lastName;
    }
}
