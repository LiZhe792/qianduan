package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Project学生创新项目表实体类
 * Integer projectId 项目ID，主键
 * Student student 关联的学生信息
 * String projectName 项目名称
 * String projectType 项目类型
 * String description 项目描述
 * String status 项目状态
 * String startDate 开始日期
 * String endDate 结束日期
 * Double budget 项目预算
 */
@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;

    @ManyToOne
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Student student;

    @Size(max = 100)
    private String projectName;

    @Size(max = 50)
    private String projectType;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private String status;

    @Size(max = 10)
    private String startDate;

    @Size(max = 10)
    private String endDate;

    private Double budget;
}