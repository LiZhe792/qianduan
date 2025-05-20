package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Lecture;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.LectureRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LectureService {
    private static final Logger log = LoggerFactory.getLogger(LectureService.class);
    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public Map<String,Object> getMapFromLecture(Lecture lecture) {
        Map<String,Object> map = new HashMap<>();
        if(lecture == null)
            return map;
        map.put("lectureId", lecture.getLectureId());
        map.put("title", lecture.getTitle());
        map.put("speaker", lecture.getSpeaker());
        map.put("date", lecture.getDate());
        map.put("time", lecture.getTime());
        map.put("location", lecture.getLocation());
        map.put("description", lecture.getDescription());
        map.put("capacity", lecture.getCapacity());
        map.put("status", lecture.getStatus());
        return map;
    }

    public List<Map<String,Object>> getLectureMapList(String keyword) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Lecture> lectureList = lectureRepository.findLectureListByTitleOrSpeaker(keyword);
        if (lectureList == null || lectureList.isEmpty())
            return dataList;
        for (Lecture lecture : lectureList) {
            dataList.add(getMapFromLecture(lecture));
        }
        return dataList;
    }

    public DataResponse getLectureList(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        List<Map<String,Object>> dataList = getLectureMapList(keyword);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse lectureDelete(DataRequest dataRequest) {
        Integer lectureId = dataRequest.getInteger("lectureId");
        if (lectureId != null) {
            Optional<Lecture> op = lectureRepository.findById(lectureId);
            op.ifPresent(lectureRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getLectureInfo(DataRequest dataRequest) {
        Integer lectureId = dataRequest.getInteger("lectureId");
        Lecture lecture = null;
        Optional<Lecture> op;
        if (lectureId != null) {
            op = lectureRepository.findById(lectureId);
            if (op.isPresent()) {
                lecture = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromLecture(lecture));
    }

    public DataResponse lectureEditSave(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        Integer lectureId = dataRequest.getInteger("lectureId");

        Lecture lecture;
        Optional<Lecture> op;
        if (lectureId != null) {
            op = lectureRepository.findById(lectureId);
            lecture = op.orElseGet(Lecture::new);
        } else {
            lecture = new Lecture();
        }

        lecture.setTitle(CommonMethod.getString(form,"title"));
        lecture.setSpeaker(CommonMethod.getString(form,"speaker"));
        lecture.setDate(CommonMethod.getString(form,"date"));
        lecture.setTime(CommonMethod.getString(form,"time"));
        lecture.setLocation(CommonMethod.getString(form,"location"));
        lecture.setDescription(CommonMethod.getString(form,"description"));
        lecture.setCapacity(CommonMethod.getInteger(form,"capacity"));
        lecture.setStatus(CommonMethod.getString(form,"status"));

        lectureRepository.save(lecture);
        return CommonMethod.getReturnData(lecture.getLectureId());
    }

    public DataResponse getLecturePageData(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        Integer currentPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int pageSize = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Lecture> page;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        page = lectureRepository.findLecturePageByTitleOrSpeaker(keyword, pageable);

        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Lecture> list = page.getContent();
            if (!list.isEmpty()) {
                for (Lecture lecture : list) {
                    dataList.add(getMapFromLecture(lecture));
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