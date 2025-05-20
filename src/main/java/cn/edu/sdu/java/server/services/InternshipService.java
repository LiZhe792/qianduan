package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Internship;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.InternshipRepository;
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
public class InternshipService {
    private static final Logger log = LoggerFactory.getLogger(InternshipService.class);
    private final InternshipRepository internshipRepository;
    private final StudentRepository studentRepository;

    public InternshipService(InternshipRepository internshipRepository, StudentRepository studentRepository) {
        this.internshipRepository = internshipRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String,Object> getMapFromInternship(Internship internship) {
        Map<String,Object> m = new HashMap<>();
        if(internship == null)
            return m;

        Student s = internship.getStudent();
        if(s == null)
            return m;

        m.put("internshipId", internship.getInternshipId());
        m.put("personId", s.getPersonId());
        m.put("studentNum", s.getPerson().getNum());
        m.put("studentName", s.getPerson().getName());
        m.put("company", internship.getCompany());
        m.put("position", internship.getPosition());
        m.put("startDate", internship.getStartDate());
        m.put("endDate", internship.getEndDate());
        m.put("supervisor", internship.getSupervisor());
        m.put("contact", internship.getContact());
        m.put("status", internship.getStatus());
        m.put("evaluation", internship.getEvaluation());
        return m;
    }

    public List<Map<String,Object>> getInternshipMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Internship> iList = internshipRepository.findInternshipListByNumName(numName);
        if (iList == null || iList.isEmpty())
            return dataList;
        for (Internship internship : iList) {
            dataList.add(getMapFromInternship(internship));
        }
        return dataList;
    }

    public DataResponse getInternshipList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getInternshipMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse internshipDelete(DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");
        if (internshipId != null) {
            Optional<Internship> op = internshipRepository.findById(internshipId);
            op.ifPresent(internshipRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getInternshipInfo(DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");
        Internship internship = null;
        Optional<Internship> op;
        if (internshipId != null) {
            op = internshipRepository.findById(internshipId);
            if (op.isPresent()) {
                internship = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromInternship(internship));
    }

    public DataResponse internshipEditSave(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        Integer internshipId = dataRequest.getInteger("internshipId");
        Integer personId = CommonMethod.getInteger(form,"personId");

        if (personId == null) {
            return CommonMethod.getReturnMessageError("学生信息不能为空！");
        }

        Optional<Student> sOp = studentRepository.findById(personId);
        if (sOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到对应的学生！");
        }

        Internship i;
        Optional<Internship> op;
        if (internshipId != null) {
            op = internshipRepository.findById(internshipId);
            i = op.orElseGet(Internship::new);
        } else {
            i = new Internship();
        }

        i.setStudent(sOp.get());
        i.setCompany(CommonMethod.getString(form,"company"));
        i.setPosition(CommonMethod.getString(form,"position"));
        i.setStartDate(CommonMethod.getString(form,"startDate"));
        i.setEndDate(CommonMethod.getString(form,"endDate"));
        i.setSupervisor(CommonMethod.getString(form,"supervisor"));
        i.setContact(CommonMethod.getString(form,"contact"));
        i.setStatus(CommonMethod.getString(form,"status"));
        i.setEvaluation(CommonMethod.getString(form,"evaluation"));

        internshipRepository.save(i);
        return CommonMethod.getReturnData(i.getInternshipId());
    }

    public DataResponse getInternshipPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Internship> page;
        Pageable pageable = PageRequest.of(cPage, size);
        page = internshipRepository.findInternshipPageByNumName(numName, pageable);

        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Internship> list = page.getContent();
            if (!list.isEmpty()) {
                for (Internship internship : list) {
                    dataList.add(getMapFromInternship(internship));
                }
            }
        }

        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }
}