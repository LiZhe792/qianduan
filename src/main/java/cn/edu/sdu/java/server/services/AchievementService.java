package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Achievement;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AchievementRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AchievementService {
    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);
    private final AchievementRepository achievementRepository;
    private final StudentRepository studentRepository;

    public AchievementService(AchievementRepository achievementRepository, StudentRepository studentRepository) {
        this.achievementRepository = achievementRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String,Object> getMapFromAchievement(Achievement achievement) {
        Map<String,Object> map = new HashMap<>();
        if(achievement == null)
            return map;
        map.put("achievementId", achievement.getAchievementId());
        map.put("title", achievement.getTitle());
        map.put("type", achievement.getType());
        map.put("date", achievement.getDate());
        map.put("level", achievement.getLevel());
        map.put("description", achievement.getDescription());
        map.put("status", achievement.getStatus());

        Student student = achievement.getStudent();
        if(student != null) {
            map.put("studentNum", student.getPerson().getNum());
            map.put("studentName", student.getPerson().getName());
            map.put("personId", student.getPersonId());
        }
        return map;
    }

    public List<Map<String,Object>> getAchievementMapList(String keyword) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Achievement> achievementList = achievementRepository.findAchievementListByTitleOrType(keyword);
        if (achievementList == null || achievementList.isEmpty())
            return dataList;
        for (Achievement achievement : achievementList) {
            dataList.add(getMapFromAchievement(achievement));
        }
        return dataList;
    }

    public DataResponse getAchievementList(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        List<Map<String,Object>> dataList = getAchievementMapList(keyword);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse achievementDelete(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        if (achievementId != null) {
            Optional<Achievement> op = achievementRepository.findById(achievementId);
            op.ifPresent(achievementRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getAchievementInfo(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Achievement achievement = null;
        Optional<Achievement> op;
        if (achievementId != null) {
            op = achievementRepository.findById(achievementId);
            if (op.isPresent()) {
                achievement = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAchievement(achievement));
    }

    public DataResponse achievementEditSave(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Map<String,Object> form = dataRequest.getMap("form");

        Achievement achievement = null;
        Optional<Achievement> op;
        if (achievementId != null) {
            op = achievementRepository.findById(achievementId);
            if(op.isPresent()) {
                achievement = op.get();
            }
        }
        if(achievement == null) {
            achievement = new Achievement();
        }

        String studentNum = CommonMethod.getString(form, "studentNum");
        Optional<Student> studentOp = studentRepository.findByPersonNum(studentNum);
        if(studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不存在！");
        }

        achievement.setStudent(studentOp.get());
        achievement.setTitle(CommonMethod.getString(form, "title"));
        achievement.setType(CommonMethod.getString(form, "type"));
        achievement.setDate(CommonMethod.getString(form, "date"));
        achievement.setLevel(CommonMethod.getString(form, "level"));
        achievement.setDescription(CommonMethod.getString(form, "description"));
        achievement.setStatus(CommonMethod.getString(form, "status"));

        achievementRepository.save(achievement);
        return CommonMethod.getReturnData(achievement.getAchievementId());
    }

    public DataResponse getAchievementPageData(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        Integer currentPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int pageSize = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Achievement> page;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        page = achievementRepository.findAchievementPageByTitleOrType(keyword, pageable);

        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Achievement> list = page.getContent();
            if (!list.isEmpty()) {
                for (Achievement achievement : list) {
                    dataList.add(getMapFromAchievement(achievement));
                }
            }
        }

        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", pageSize);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }
}