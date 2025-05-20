package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.InternshipService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * InternshipController 主要是为学生校外实习管理提供的Web请求服务
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/internship")
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @PostMapping("/getInternshipList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getInternshipList(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.getInternshipList(dataRequest);
    }

    @PostMapping("/internshipDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse internshipDelete(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.internshipDelete(dataRequest);
    }

    @PostMapping("/getInternshipInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getInternshipInfo(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.getInternshipInfo(dataRequest);
    }

    @PostMapping("/internshipEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse internshipEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.internshipEditSave(dataRequest);
    }

    @PostMapping("/getInternshipPageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getInternshipPageData(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.getInternshipPageData(dataRequest);
    }
}