package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentStatisticsRepository extends JpaRepository<StudentStatistics,Integer> {
}
