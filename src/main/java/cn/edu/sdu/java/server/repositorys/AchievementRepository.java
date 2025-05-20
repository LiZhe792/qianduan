package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Achievement 数据操作接口
 */
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

    // 根据学生ID查询所有成果
    List<Achievement> findByStudentPersonId(Integer personId);

    // 根据标题或类型模糊查询
    @Query(value = "from Achievement where ?1='' or title like %?1% or type like %?1% ")
    List<Achievement> findAchievementListByTitleOrType(String keyword);

    // 分页查询
    @Query(value = "from Achievement where ?1='' or title like %?1% or type like %?1% ",
            countQuery = "SELECT count(achievementId) from Achievement where ?1='' or title like %?1% or type like %?1% ")
    Page<Achievement> findAchievementPageByTitleOrType(String keyword, Pageable pageable);

    // 根据成果类型查询
    List<Achievement> findByType(String type);

    // 根据成果级别查询
    List<Achievement> findByLevel(String level);
}