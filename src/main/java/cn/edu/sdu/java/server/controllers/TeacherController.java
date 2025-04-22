package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * TeacherController 主要是为教师管理数据管理提供的Web请求服务
 */
// origins： 允许可访问的域列表
// maxAge: 准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * getTeacherList 教师管理 点击查询按钮请求
     * 前台请求参数可包含教师编号或名称等查询串
     * 返回前端 存储教师信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @param dataRequest 包含查询条件的请求体
     * @return 存储教师信息的响应数据
     */
    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }

    /**
     * teacherDelete 删除教师信息Web服务 Teacher页面的列表里点击删除按钮则可以删除已经存在的教师信息，
     * 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     *
     * @param dataRequest 前端teacherId 要删除的教师的主键 teacher_id
     * @return 正常操作响应
     */
    @PostMapping("/teacherDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    /**
     * getTeacherInfo 前端点击教师列表时前端获取教师详细信息请求服务
     *
     * @param dataRequest 从前端获取 teacherId 查询教师信息的主键 teacher_id
     * @return 根据teacherId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    /**
     * teacherEditSave 前端教师信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Teacher 计算新的id， 复制相关属性，保存，
     * 如果是编辑原来的信息，teacherId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     *
     * @param dataRequest 包含教师信息的请求体
     * @return 新建或修改教师的主键 teacher_id 返回前端
     */
    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEditSave(dataRequest);
    }
}