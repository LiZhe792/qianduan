package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Fee 消费流水实体类  保存学生消费流水的基本信息信息，
 * Integer feeId 消费表 fee 主键 fee_id
 * Integer personId   对应student 表里面的 person_id
 * String day 日期
 * Double money 金额
 */
@Entity
@Table(	name = "fee"
)
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feeId;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Student student;
    @Size(max = 20)
    private String day;
    private Double money;

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }


    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
