package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentHonor;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentHonorRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentHonorService {
    private final StudentHonorRepository studentHonorRepository;

    public StudentHonorService(StudentHonorRepository studentHonorRepository) {
        this.studentHonorRepository = studentHonorRepository;
    }

    public DataResponse getHonorList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String honorType = (String) dataRequest.get("honorType");
        List<StudentHonor> honorList = studentHonorRepository.findHonorListByPersonIdAndHonorType(personId, honorType);
        List<Map<String, Object>> dataList = honorList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getHonorPage(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String honorType = (String) dataRequest.get("honorType");
        Integer pageNum = dataRequest.getCurrentPage();
        Integer pageSize = dataRequest.getPageSize();
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<StudentHonor> page = studentHonorRepository.findHonorPageByPersonIdAndHonorType(personId, honorType, pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotalElements());
        result.put("pageSize", pageSize);
        result.put("currentPage", pageNum);
        result.put("dataList", page.getContent().stream().map(this::convertToMap).collect(Collectors.toList()));
        return CommonMethod.getReturnData(result);
    }

    public DataResponse addHonor(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        StudentHonor honor = new StudentHonor();
        honor.setHonorName((String) form.get("honorName"));
        honor.setHonorDate((String) form.get("honorDate"));
        honor.setHonorType((String) form.get("honorLevel"));
        Integer personId = (Integer) form.get("personId");
        if (personId != null) {
            Student student = new Student();
            student.setPersonId(personId);
            honor.setStudent(student);
        }
        try {
            StudentHonor savedHonor = studentHonorRepository.save(honor);
            return CommonMethod.getReturnData(savedHonor.getHonorId());
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("保存失败: " + e.getMessage());
        }
    }

    public DataResponse updateHonor(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<StudentHonor> optionalHonor = studentHonorRepository.findByHonorId(honorId);
        if (optionalHonor.isPresent()) {
            StudentHonor honor = optionalHonor.get();
            Map<String, Object> form = dataRequest.getMap("form");
            honor.setHonorName((String) form.get("honorName"));
            honor.setHonorDate((String) form.get("honorDate"));
            honor.setHonorType((String) form.get("honorLevel"));
            Integer personId = (Integer) form.get("personId");
            if (personId != null) {
                Student student = new Student();
                student.setPersonId(personId);
                honor.setStudent(student);
            }
            try {
                studentHonorRepository.save(honor);
                return CommonMethod.getReturnMessageOK();
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("保存失败: " + e.getMessage());
            }
        }
        return CommonMethod.getReturnMessageError("荣誉记录不存在");
    }

    public DataResponse deleteHonor(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<StudentHonor> optionalHonor = studentHonorRepository.findByHonorId(honorId);
        if (optionalHonor.isPresent()) {
            studentHonorRepository.deleteById(honorId);
            return CommonMethod.getReturnMessageOK();
        }
        return CommonMethod.getReturnMessageError("荣誉记录不存在");
    }

    public DataResponse getHonorDetails(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<StudentHonor> optionalHonor = studentHonorRepository.findByHonorId(honorId);
        if (optionalHonor.isPresent()) {
            StudentHonor honor = optionalHonor.get();
            return CommonMethod.getReturnData(convertToMap(honor));
        }
        return CommonMethod.getReturnMessageError("荣誉记录不存在");
    }

    private Map<String, Object> convertToMap(StudentHonor honor) {
        Map<String, Object> map = new HashMap<>();
        map.put("honorId", honor.getHonorId());
        map.put("studentId", honor.getStudent().getPersonId());
        map.put("honorName", honor.getHonorName());
        map.put("honorDate", honor.getHonorDate());
        map.put("honorType", honor.getHonorType());
        map.put("studentNum", honor.getStudent().getPerson().getNum());
        map.put("studentName", honor.getStudent().getPerson().getName());
        map.put("honorLevelName", honor.getHonorType());
        return map;
    }
}