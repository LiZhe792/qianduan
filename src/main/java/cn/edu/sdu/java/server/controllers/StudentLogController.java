package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生日志管理控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentLog")
public class StudentLogController {
    private final StudentLogService studentLogService;

    public StudentLogController(StudentLogService studentLogService) {
        this.studentLogService = studentLogService;
    }

    /**
     * 获取学生日志列表
     * @param dataRequest 包含 personId 和 logType（可选）的请求参数
     * @return 日志列表 DataResponse
     */
    @PostMapping("/getLogList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<DataResponse> getLogList(@Valid @RequestBody DataRequest dataRequest) {
        DataResponse response = studentLogService.getLogList(dataRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取日志详情
     * @param dataRequest 包含 logId 的请求参数
     * @return 日志详情 DataResponse
     */
    @PostMapping("/getLogInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<DataResponse> getLogInfo(@Valid @RequestBody DataRequest dataRequest) {
        DataResponse response = studentLogService.getLogInfo(dataRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 保存或更新日志
     * @param dataRequest 包含日志信息的请求参数（logId 存在则更新，否则新增）
     * @return 操作结果 DataResponse
     */
    @PostMapping("/logSave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<DataResponse> logSave(@Valid @RequestBody DataRequest dataRequest) {
        DataResponse response = studentLogService.logSave(dataRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除日志
     * @param dataRequest 包含 logId 的请求参数
     * @return 操作结果 DataResponse
     */
    @PostMapping("/logDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<DataResponse> logDelete(@Valid @RequestBody DataRequest dataRequest) {
        DataResponse response = studentLogService.logDelete(dataRequest);
        return ResponseEntity.ok(response);
    }
}