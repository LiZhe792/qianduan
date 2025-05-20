package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Lecture 数据操作接口
 */
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    // 根据讲座标题或主讲人模糊查询
    @Query(value = "from Lecture where ?1='' or title like %?1% or speaker like %?1% ")
    List<Lecture> findLectureListByTitleOrSpeaker(String keyword);

    // 分页查询
    @Query(value = "from Lecture where ?1='' or title like %?1% or speaker like %?1% ",
            countQuery = "SELECT count(lectureId) from Lecture where ?1='' or title like %?1% or speaker like %?1% ")
    Page<Lecture> findLecturePageByTitleOrSpeaker(String keyword, Pageable pageable);

    // 根据状态查询
    List<Lecture> findByStatus(String status);
}