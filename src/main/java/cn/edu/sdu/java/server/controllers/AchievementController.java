package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.AchievementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AchievementController 主要是为学生科技成果管理提供的Web请求服务
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/achievement")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping("/getAchievementList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAchievementList(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementList(dataRequest);
    }

    @PostMapping("/achievementDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementDelete(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementDelete(dataRequest);
    }

    @PostMapping("/getAchievementInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAchievementInfo(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementInfo(dataRequest);
    }

    @PostMapping("/achievementEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementEditSave(dataRequest);
    }

    @PostMapping("/getAchievementPageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAchievementPageData(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementPageData(dataRequest);
    }
}