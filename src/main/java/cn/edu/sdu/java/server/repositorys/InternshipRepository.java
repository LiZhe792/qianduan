package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Internship 数据操作接口
 */
public interface InternshipRepository extends JpaRepository<Internship, Integer> {

    // 根据学生personId查询实习记录
    List<Internship> findByStudentPersonId(Integer personId);

    // 根据学生学号或姓名模糊查询实习记录
    @Query(value = "from Internship i where ?1='' or i.student.person.num like %?1% or i.student.person.name like %?1% ")
    List<Internship> findInternshipListByNumName(String numName);

    // 分页查询
    @Query(value = "from Internship i where ?1='' or i.student.person.num like %?1% or i.student.person.name like %?1% ",
            countQuery = "SELECT count(internshipId) from Internship i where ?1='' or i.student.person.num like %?1% or i.student.person.name like %?1% ")
    Page<Internship> findInternshipPageByNumName(String numName, Pageable pageable);
}