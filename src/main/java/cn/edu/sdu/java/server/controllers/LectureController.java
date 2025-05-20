package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.LectureService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * LectureController 主要是为培训讲座管理提供的Web请求服务
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lecture")
public class LectureController {
    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/getLectureList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLectureList(@Valid @RequestBody DataRequest dataRequest) {
        return lectureService.getLectureList(dataRequest);
    }

    @PostMapping("/lectureDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse lectureDelete(@Valid @RequestBody DataRequest dataRequest) {
        return lectureService.lectureDelete(dataRequest);
    }

    @PostMapping("/getLectureInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLectureInfo(@Valid @RequestBody DataRequest dataRequest) {
        return lectureService.getLectureInfo(dataRequest);
    }

    @PostMapping("/lectureEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse lectureEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return lectureService.lectureEditSave(dataRequest);
    }

    @PostMapping("/getLecturePageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLecturePageData(@Valid @RequestBody DataRequest dataRequest) {
        return lectureService.getLecturePageData(dataRequest);
    }
}