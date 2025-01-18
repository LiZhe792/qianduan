package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "student_statistics",
        uniqueConstraints = {
        })
public class StudentStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statisticsId;

    @OneToOne
    @JoinColumn(name="personId")
    private  Student student;

    private  Integer courseCount;
    private Integer creditTotal;
    private Double avgScore;
    private Double gpa;
    private Integer activeCount;

}
