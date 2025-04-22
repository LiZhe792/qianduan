package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository; //教师数据操作自动注入
    private final PersonRepository personRepository;  //人员数据操作自动注入

    public TeacherService(TeacherRepository teacherRepository, PersonRepository personRepository) {
        this.teacherRepository = teacherRepository;
        this.personRepository = personRepository;
    }

    public Map getMapFromTeacher(Teacher t) {
        Map m = new HashMap();
        Person p;
        if(t == null)
            return m;
        m.put("title",t.getTitle());
        m.put("degree",t.getDegree());
        p = t.getPerson();
        if(p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        return m;
    }

    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0;i <sList.size();i++) {
            dataList.add(getMapFromTeacher(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher t = null;
        Person p;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                t = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(t)); //这里回传包含学生信息的Map对象
    }

    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Teacher t = null;
        Person p;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                t = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (t == null || !t.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("账号已经存在，不能添加或修改！");
            }
        }
        if (t == null) {
            p = new Person();
            p.setNum(num);
            p.setType("2");
            personRepository.save(p);  //插入新的Person记录
            personId = p.getPersonId();
            try {
                t = new Teacher(); //创建实体对象
                t.setPersonId(personId);
                teacherRepository.save(t); //插入新的Teacher记录
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            p = t.getPerson();
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        t.setTitle(CommonMethod.getString(form, "title"));
        t.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(t);  //修改保存教师信息
        return CommonMethod.getReturnData(t.getPersonId());  // 将personId返回前端
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null) {
            return CommonMethod.getReturnMessageError("缺少必要的参数：personId");
        }
        try {
            // 先查询教师信息
            Optional<Teacher> teacherOptional = teacherRepository.findById(personId);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                Person person = teacher.getPerson();
                // 删除教师信息
                teacherRepository.delete(teacher);
                // 删除人员信息
                if (person != null) {
                    personRepository.delete(person);
                }
                return CommonMethod.getReturnMessageOK("删除教师信息成功");
            } else {
                return CommonMethod.getReturnMessageError("未找到该教师信息，无法删除");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("删除教师信息失败：" + e.getMessage());
        }
    }
}