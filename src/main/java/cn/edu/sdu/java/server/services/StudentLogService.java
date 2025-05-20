package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentLog;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentLogRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class StudentLogService {
    private static final Logger log = LoggerFactory.getLogger(StudentLogService.class);
    private final StudentLogRepository studentLogRepository;

    @Autowired
    StudentRepository studentRepository;

    public StudentLogService(StudentLogRepository studentLogRepository) {
        this.studentLogRepository = studentLogRepository;
    }

    private Map<String, String> getMapFromLog(StudentLog log) {
        Map<String, String> m = new HashMap<>();
        if (log == null) return m;

        m.put("logId", log.getLogId().toString());
        m.put("logType", log.getLogType());
        m.put("logContent", log.getLogContent());
        m.put("logTime", log.getLogTime() != null ? CommonMethod.formatDate(log.getLogTime()) : "");
        m.put("amount", log.getAmount() != null ? log.getAmount().toString() : "0.0");
        return m;
    }

    public DataResponse getLogList(@Valid @RequestBody DataRequest dataRequest) {

        List<StudentLog> logList = studentLogRepository.findAll();
        List<Map<String, String>> dataList = new ArrayList<>();

        logList.forEach(log -> dataList.add(getMapFromLog(log)));

        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getLogInfo(DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");
        Optional<StudentLog> logOptional = studentLogRepository.findById(logId);

        return logOptional.map(log -> CommonMethod.getReturnData(getMapFromLog(log)))
                .orElse(CommonMethod.getReturnMessageError("日志不存在"));
    }

    public DataResponse logSave(@Valid @RequestBody  DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");
        StudentLog log;

        if (logId != null && logId > 0) {
            Optional<StudentLog> logOptional = studentLogRepository.findById(logId);
            log = logOptional.orElse(new StudentLog());
        } else {
            log = new StudentLog();
            log.setLogTime(new Date()); // 默认当前时间
        }

        Map<String, Object> form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form, "num");
        Optional<Student> optionalStudent = studentRepository.findStudentByNum(num);
        if (optionalStudent.isEmpty()){
            return new DataResponse(1, null, "failed");
        } else {
            log.setStudent(optionalStudent.get());
            log.setLogType(CommonMethod.getString(form, "logType"));
            log.setLogContent(CommonMethod.getString(form, "logContent"));
            log.setAmount(CommonMethod.getDouble(form, "amount"));
        }

        studentLogRepository.saveAndFlush(log);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse logDelete(DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");
        studentLogRepository.deleteById(logId);
        return CommonMethod.getReturnMessageOK();
    }
}