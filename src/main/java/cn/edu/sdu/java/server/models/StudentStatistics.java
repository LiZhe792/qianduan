package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;

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

    public Integer getStatisticsId() {
        return statisticsId;
    }

    public void setStatisticsId(Integer statisticsId) {
        this.statisticsId = statisticsId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(Integer creditTotal) {
        this.creditTotal = creditTotal;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }
}
