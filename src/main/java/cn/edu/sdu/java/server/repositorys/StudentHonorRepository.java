package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentHonor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentHonorRepository extends JpaRepository<StudentHonor, Integer> {
    Optional<StudentHonor> findByHonorId(Integer honorId);

    List<StudentHonor> findByStudentPersonId(Integer personId);

    List<StudentHonor> findByHonorType(String honorType);

    @Query(value = "from StudentHonor where (?1 is null or student.personId = ?1) and (?2 is null or honorType = ?2)")
    List<StudentHonor> findHonorListByPersonIdAndHonorType(Integer personId, String honorType);

    @Query(value = "from StudentHonor where (?1 is null or student.personId = ?1) and (?2 is null or honorType = ?2)",
            countQuery = "SELECT count(honorId) from StudentHonor where (?1 is null or student.personId = ?1) and (?2 is null or honorType = ?2)")
    Page<StudentHonor> findHonorPageByPersonIdAndHonorType(Integer personId, String honorType, Pageable pageable);
}