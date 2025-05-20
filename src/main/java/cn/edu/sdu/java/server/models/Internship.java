package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Internship 学生校外实习表实体类
 * Integer internshipId 实习表主键
 * Student student 关联的学生对象
 * String company 实习公司
 * String position 实习岗位
 * String startDate 开始日期
 * String endDate 结束日期
 * String supervisor 企业导师
 * String contact 联系方式
 * String status 实习状态（进行中/已完成）
 * String evaluation 实习评价
 */
@Getter
@Setter
@Entity
@Table(name = "internship")
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer internshipId;

    @ManyToOne
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Student student;

    @Size(max = 100)
    private String company;

    @Size(max = 50)
    private String position;

    private String startDate;

    private String endDate;

    @Size(max = 50)
    private String supervisor;

    @Size(max = 20)
    private String contact;

    @Size(max = 20)
    private String status;

    @Size(max = 500)
    private String evaluation;
}