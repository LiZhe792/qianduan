package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 学生日志实体类，包含外出请假和生活学习消费信息
 * Integer logId 日志主键
 * Integer personId 学生ID（关联Student表的personId）
 * String logType 日志类型（外出请假/生活消费/学习记录等）
 * String logContent 日志内容
 * Date logTime 日志时间
 * Double amount 金额（消费相关记录使用）
 */
@Getter
@Setter
@Entity
@Table(name = "student_log")
public class StudentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "logType", length = 20)
    private String logType;

    @Column(name = "logContent", length = 500)
    private String logContent;

    @Column(name = "logTime")
    private Date logTime;

    @Column(name = "amount", precision = 10)
    private Double amount;
}