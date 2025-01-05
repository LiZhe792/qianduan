package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {
    private final TeacherService teacherService;  //密码服务自动注入
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }



    @PostMapping("/teacherDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return 根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEditSave(dataRequest);
    }

}
