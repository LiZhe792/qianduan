package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Achievement 学生科技成果表实体类
 * Integer achievementId 科技成果表主键
 * Student student 关联的学生
 * String title 成果标题
 * String type 成果类型（论文/专利/竞赛/项目等）
 * String date 获得日期
 * String level 成果级别（国家级/省级/市级等）
 * String description 成果描述
 * String status 成果状态（已发表/已授权/进行中等）
 */
@Getter
@Setter
@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer achievementId;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Student student;

    @Size(max = 100)
    private String title;

    @Size(max = 20)
    private String type;

    private String date;

    @Size(max = 20)
    private String level;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private String status;
}