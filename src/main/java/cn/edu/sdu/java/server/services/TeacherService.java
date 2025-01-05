package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.EUserType;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.models.User;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.repositorys.UserTypeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TeacherService {
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final TeacherRepository teacherRepository;  //学生数据操作自动注入
    private final UserRepository userRepository;  //学生数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
    public TeacherService(PersonRepository personRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
    }
    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName ="";
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);
        return CommonMethod.getReturnData(sList);
    }



    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");  //获取student_id值
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if (uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }
            Person p = s.getPerson();
            teacherRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return 根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Teacher();
            s.setPerson(new Person());
        }
        return CommonMethod.getReturnData(s);
    }

    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Map pForm =  CommonMethod.getMap(form,"person");
        String num = CommonMethod.getString(pForm, "num");  //Map 获取属性的值
        Teacher s = null;
        Person p;
        User u;
        Optional<Teacher> op;
        Integer personId;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新工号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("2");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            String password = encoder.encode("123456");
            u = new User();
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getUserId());
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Teacher();   // 创建实体对象
            s.setPerson(p);
            teacherRepository.saveAndFlush(s);  //插入新的Student记录
        } else {
            p = s.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(pForm, "name"));
        p.setDept(CommonMethod.getString(pForm, "dept"));
        p.setCard(CommonMethod.getString(pForm, "card"));
        p.setGender(CommonMethod.getString(pForm, "gender"));
        p.setBirthday(CommonMethod.getString(pForm, "birthday"));
        p.setEmail(CommonMethod.getString(pForm, "email"));
        p.setPhone(CommonMethod.getString(pForm, "phone"));
        p.setAddress(CommonMethod.getString(pForm, "address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setTitle(CommonMethod.getString(form, "title"));
        s.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getTeacherId());  // 将studentId返回前端
    }

}
