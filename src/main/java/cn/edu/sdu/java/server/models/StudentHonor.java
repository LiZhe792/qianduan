package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "student_honor")
public class StudentHonor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    @ManyToOne
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Student student;

    @Size(max = 50)
    private String honorName;

    @Size(max = 20)
    private String honorDate;

    @Size(max = 20)
    private String honorType;
}