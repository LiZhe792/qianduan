package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.StudentHonor;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentHonorService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/studentHonor")
public class StudentHonorController {
    private final StudentHonorService studentHonorService;

    public StudentHonorController(StudentHonorService studentHonorService) {
        this.studentHonorService = studentHonorService;
    }

    @PostMapping("/getHonorList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getHonorList(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getHonorList(dataRequest);
    }

    @PostMapping("/getHonorPage")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getHonorPage(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getHonorPage(dataRequest);
    }

    @PostMapping("/addHonor")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addHonor(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.addHonor(dataRequest);
    }

    @PostMapping("/updateHonor")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateHonor(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.updateHonor(dataRequest);
    }

    @PostMapping("/deleteHonor")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteHonor(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.deleteHonor(dataRequest);
    }

    @PostMapping("/getHonorDetails")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getHonorDetails(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getHonorDetails(dataRequest);
    }
}