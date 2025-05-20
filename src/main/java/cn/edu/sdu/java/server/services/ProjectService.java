package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Project;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ProjectRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;

    public ProjectService(ProjectRepository projectRepository, StudentRepository studentRepository) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String, Object> getMapFromProject(Project p) {
        Map<String, Object> m = new HashMap<>();
        if (p == null)
            return m;
        m.put("projectId", p.getProjectId());
        m.put("projectName", p.getProjectName());
        m.put("projectType", p.getProjectType());
        m.put("description", p.getDescription());
        m.put("status", p.getStatus());
        m.put("startDate", p.getStartDate());
        m.put("endDate", p.getEndDate());
        m.put("budget", p.getBudget());
        if (p.getStudent() != null) {
            m.put("studentNum", p.getStudent().getPerson().getNum());
            m.put("studentName", p.getStudent().getPerson().getName());
        }
        return m;
    }

    public List<Map<String, Object>> getProjectMapList(String searchText) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Project> pList = projectRepository.findProjectListByNameOrType(searchText);
        if (pList == null || pList.isEmpty())
            return dataList;
        for (Project project : pList) {
            dataList.add(getMapFromProject(project));
        }
        return dataList;
    }

    public DataResponse getProjectList(DataRequest dataRequest) {
        String searchText = dataRequest.getString("searchText");
        List<Map<String, Object>> dataList = getProjectMapList(searchText);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse projectDelete(DataRequest dataRequest) {
        Integer projectId = dataRequest.getInteger("projectId");
        if (projectId != null) {
            Optional<Project> op = projectRepository.findById(projectId);
            op.ifPresent(projectRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getProjectInfo(DataRequest dataRequest) {
        Integer projectId = dataRequest.getInteger("projectId");
        Project p = null;
        Optional<Project> op;
        if (projectId != null) {
            op = projectRepository.findById(projectId);
            if (op.isPresent()) {
                p = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromProject(p));
    }

    public DataResponse projectEditSave(DataRequest dataRequest) {
        Integer projectId = dataRequest.getInteger("projectId");
        Map<String, Object> form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form, "studentNum");

        Optional<Student> studentOp = studentRepository.findByPersonNum(studentNum);
        if (studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不存在！");
        }

        Project p = null;
        Optional<Project> op;
        if (projectId != null) {
            op = projectRepository.findById(projectId);
            if (op.isPresent()) {
                p = op.get();
            }
        }
        if (p == null) {
            p = new Project();
        }

        p.setStudent(studentOp.get());
        p.setProjectName(CommonMethod.getString(form, "projectName"));
        p.setProjectType(CommonMethod.getString(form, "projectType"));
        p.setDescription(CommonMethod.getString(form, "description"));
        p.setStatus(CommonMethod.getString(form, "status"));
        p.setStartDate(CommonMethod.getString(form, "startDate"));
        p.setEndDate(CommonMethod.getString(form, "endDate"));
        p.setBudget(CommonMethod.getDouble(form, "budget"));

        projectRepository.save(p);
        return CommonMethod.getReturnData(p.getProjectId());
    }

    public DataResponse getProjectPageData(DataRequest dataRequest) {
        String searchText = dataRequest.getString("searchText");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String, Object>> dataList = new ArrayList<>();
        Page<Project> page;
        Pageable pageable = PageRequest.of(cPage, size);
        page = projectRepository.findProjectPageByNameOrType(searchText, pageable);

        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Project> list = page.getContent();
            if (!list.isEmpty()) {
                for (Project project : list) {
                    dataList.add(getMapFromProject(project));
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }

    public ResponseEntity<StreamingResponseBody> getProjectListExcel(DataRequest dataRequest) {
        String searchText = dataRequest.getString("searchText");
        List<Map<String, Object>> list = getProjectMapList(searchText);
        Integer[] widths = {8, 20, 100, 50, 200, 20, 15, 15, 15, 20};
        String[] titles = {"序号", "学号", "项目名称", "项目类型", "项目描述", "状态", "开始日期", "结束日期", "预算", "学生姓名"};

        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
            XSSFSheet sheet = wb.createSheet("projects.xlsx");

            for (int j = 0; j < widths.length; j++) {
                sheet.setColumnWidth(j, widths[j] * 256);
            }

            XSSFRow row = sheet.createRow(0);
            XSSFCell[] cell = new XSSFCell[widths.length];
            for (int j = 0; j < widths.length; j++) {
                cell[j] = row.createCell(j);
                cell[j].setCellStyle(style);
                cell[j].setCellValue(titles[j]);
            }

            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    row = sheet.createRow(i + 1);
                    for (int j = 0; j < widths.length; j++) {
                        cell[j] = row.createCell(j);
                        cell[j].setCellStyle(style);
                    }
                    Map<String, Object> m = list.get(i);
                    cell[0].setCellValue((i + 1) + "");
                    cell[1].setCellValue(CommonMethod.getString(m, "studentNum"));
                    cell[2].setCellValue(CommonMethod.getString(m, "projectName"));
                    cell[3].setCellValue(CommonMethod.getString(m, "projectType"));
                    cell[4].setCellValue(CommonMethod.getString(m, "description"));
                    cell[5].setCellValue(CommonMethod.getString(m, "status"));
                    cell[6].setCellValue(CommonMethod.getString(m, "startDate"));
                    cell[7].setCellValue(CommonMethod.getString(m, "endDate"));
                    cell[8].setCellValue(CommonMethod.getString(m, "budget"));
                    cell[9].setCellValue(CommonMethod.getString(m, "studentName"));
                }
            }

            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}