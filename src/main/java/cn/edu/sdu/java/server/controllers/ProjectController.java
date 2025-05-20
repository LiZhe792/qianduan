package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/getProjectList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getProjectList(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.getProjectList(dataRequest);
    }

    @PostMapping("/projectDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse projectDelete(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.projectDelete(dataRequest);
    }

    @PostMapping("/getProjectInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getProjectInfo(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.getProjectInfo(dataRequest);
    }

    @PostMapping("/projectEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse projectEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.projectEditSave(dataRequest);
    }

    @PostMapping("/getProjectPageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getProjectPageData(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.getProjectPageData(dataRequest);
    }

    @PostMapping("/getProjectListExcel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getProjectListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return projectService.getProjectListExcel(dataRequest);
    }
}