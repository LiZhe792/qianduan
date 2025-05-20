package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentLog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 学生日志数据操作接口
 */
public interface StudentLogRepository extends JpaRepository<StudentLog, Integer> {
    @Query("select studentLog from StudentLog studentLog where studentLog.student.person.personId =: personId")
    List<StudentLog> findByPersonId(@Param("personId") Integer personId);

    @Query("select studentLog from StudentLog studentLog where studentLog.student.person.personId =: personId and studentLog.logType =: logType")
    List<StudentLog> findByPersonIdAndLogType(@Param("personId") Integer personId, @Param("logType") String logType);

    @Query("select studentLog from StudentLog studentLog where studentLog.student.person.personId =: personId and studentLog.logTime between :startDate and :endDate")
    List<StudentLog> findByPersonIdAndTimeRange(@Param("personId") Integer personId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}