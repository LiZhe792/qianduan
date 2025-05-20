package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Lecture 学生培训讲座表实体类
 * Integer lectureId 讲座表主键
 * String title 讲座标题
 * String speaker 主讲人
 * String date 讲座日期
 * String time 讲座时间
 * String location 讲座地点
 * String description 讲座描述
 * Integer capacity 容量人数
 * String status 讲座状态（未开始/进行中/已结束）
 */
@Getter
@Setter
@Entity
@Table(name = "lecture")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lectureId;

    @Size(max = 100)
    private String title;

    @Size(max = 50)
    private String speaker;

    private String date;

    private String time;

    @Size(max = 100)
    private String location;

    @Size(max = 500)
    private String description;

    private Integer capacity;

    @Size(max = 20)
    private String status;
}