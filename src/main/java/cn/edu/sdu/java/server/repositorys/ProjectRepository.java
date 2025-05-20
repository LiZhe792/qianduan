package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project 数据操作接口
 * List<Project> findByStudentPersonId(Integer personId) 根据学生ID查询项目列表
 * List<Project> findByProjectNameContaining(String projectName) 根据项目名称模糊查询
 * List<Project> findProjectListByNameOrType(String searchText) 根据项目名称或类型查询
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByStudentPersonId(Integer personId);

    List<Project> findByProjectNameContaining(String projectName);

    @Query(value = "from Project where ?1='' or projectName like %?1% or projectType like %?1% ")
    List<Project> findProjectListByNameOrType(String searchText);

    @Query(value = "from Project where ?1='' or projectName like %?1% or projectType like %?1% ",
            countQuery = "SELECT count(projectId) from Project where ?1='' or projectName like %?1% or projectType like %?1% ")
    Page<Project> findProjectPageByNameOrType(String searchText, Pageable pageable);
}