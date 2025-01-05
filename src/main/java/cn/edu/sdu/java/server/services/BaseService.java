package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.DictionaryInfo;
import cn.edu.sdu.java.server.models.MenuInfo;
import cn.edu.sdu.java.server.models.User;
import cn.edu.sdu.java.server.models.UserType;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.MyTreeNode;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.DictionaryInfoRepository;
import cn.edu.sdu.java.server.repositorys.MenuInfoRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.repositorys.UserTypeRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BaseService {
    @Value("${attach.folder}")    //环境配置变量获取
    private String attachFolder;  //服务器端数据存储
    private final ResourceLoader resourceLoader;
    private final PasswordEncoder encoder;  //密码服务自动注入
    private final UserRepository userRepository;  //用户数据操作自动注入
    private final MenuInfoRepository menuInfoRepository; //菜单数据操作自动注入
    private final DictionaryInfoRepository dictionaryInfoRepository;  //数据字典数据操作自动注入
    private final UserTypeRepository userTypeRepository;   //用户类型数据操作自动注入
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    public BaseService(ResourceLoader resourceLoader,PasswordEncoder encoder, UserRepository userRepository, MenuInfoRepository menuInfoRepository, DictionaryInfoRepository dictionaryInfoRepository, UserTypeRepository userTypeRepository) {
        this.resourceLoader = resourceLoader;
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.menuInfoRepository = menuInfoRepository;
        this.dictionaryInfoRepository = dictionaryInfoRepository;
        this.userTypeRepository = userTypeRepository;
    }

    /**
     *  getDictionaryTreeNode 获取数据字典节点树根节点
     * @return MyTreeNode 数据字典树根节点
     */
    public List<MyTreeNode> getDictionaryTreeNodeList() {
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        List<DictionaryInfo> sList = dictionaryInfoRepository.findRootList();
        if(sList == null)
            return childList;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getDictionaryTreeNode(null,sList.get(i),null));
        }
        return childList;
    }

    /**
     * 获得数据字典的MyTreeNode
     * @param pid  父节点
     * @param d   数据字典数据
     * @return  树节点
     */
    public MyTreeNode getDictionaryTreeNode( Integer pid, DictionaryInfo d,String parentTitle) {
        MyTreeNode  node = new MyTreeNode(d.getId(),d.getValue(),d.getLabel(),null);
        node.setLabel(d.getValue()+"-"+d.getLabel());
        node.setParentTitle(parentTitle);
        node.setPid(pid);
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        node.setChildren(childList);
        List<DictionaryInfo> sList = dictionaryInfoRepository.findByPid(d.getId());
        if(sList == null)
            return node;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getDictionaryTreeNode(node.getId(),sList.get(i),node.getValue()));
        }
        return node;
    }

    /**
     * MyTreeNode getMenuTreeNode(Integer userTypeId) 获得角色的菜单树根节点
     * @param userTypeId 用户类型ID
     * @return MyTreeNode 根节点对象
     */
    public List<MyTreeNode> getMenuTreeNodeList() {
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        List<MenuInfo> sList = menuInfoRepository.findByUserTypeIds("");
        if(sList == null)
            return childList;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getMenuTreeNode(null,sList.get(i),""));
        }
        return childList;
    }
    /**
     * MyTreeNode getMenuTreeNode(Integer userTypeId) 获得角色的某个菜单的菜单树根节点
     * @param parentTitle 用户类型ID Integer pid 父节点ID MenuInfo d 菜单信息
     *
     * @return MyTreeNode 当前菜单的MyTreeNode对象
     */
    public MyTreeNode getMenuTreeNode(Integer pid, MenuInfo d,String parentTitle) {
        MyTreeNode  node = new MyTreeNode(d.getId(),d.getName(),d.getTitle(),null);
        node.setLabel(d.getId()+"-"+d.getTitle());
        node.setUserTypeIds(d.getUserTypeIds());
        node.setParentTitle(parentTitle);
        node.setPid(pid);
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        node.setChildren(childList);
        List<MenuInfo> sList = menuInfoRepository.findByUserTypeIds("",d.getId());
        if(sList == null)
            return node;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getMenuTreeNode(node.getId(),sList.get(i),node.getTitle()));
        }
        return node;
    }

    /**
     * 将HTML的字符串转换成PDF文件，返回前端的PDF文件二进制数据流数据流
     * @param htmlContent  HTML 字符流
     * @return PDF数据流兑现
     */
    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        }
        catch (Exception e) {
            return  ResponseEntity.internalServerError().build();
        }
    }

    public List getMenuList(Integer userTypeId, Integer pid) {
        List sList = new ArrayList();
        HashMap ms;
        List<MenuInfo> msList = menuInfoRepository.findByUserTypeIds(userTypeId + "", pid);
        if (msList != null) {
            for (MenuInfo info : msList) {
                ms = new HashMap();
                ms.put("id", info.getId());
                ms.put("name", info.getName());
                ms.put("title", info.getTitle());
                ms.put("sList", getMenuList(userTypeId, info.getId()));
                sList.add(ms);
            }
        }
        return sList;
    }

    public DataResponse getMenuList(DataRequest dataRequest) {
        List dataList = new ArrayList();
        Integer userTypeId = dataRequest.getInteger("userTypeId");
        if (userTypeId == null) {
            Integer userId = CommonMethod.getUserId();
            if (userId == null)
                return CommonMethod.getReturnData(dataList);
            userTypeId = userRepository.findByUserId(userId).get().getUserType().getId();
        }
        List<MenuInfo> mList = menuInfoRepository.findByUserTypeIds(userTypeId + "");
        Map m;
        List sList;
        for (MenuInfo info : mList) {
            m = new HashMap();
            m.put("id", info.getId());
            m.put("name", info.getName());
            m.put("title", info.getTitle());
            sList = getMenuList(userTypeId, info.getId());
            m.put("sList", sList);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }


    public OptionItemList getRoleOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        List<UserType> uList = userTypeRepository.findAll();
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (UserType ut : uList) {
            itemList.add(new OptionItem(ut.getId(), null, ut.getName().name()));
        }
        return new OptionItemList(0, itemList);
    }


    public DataResponse menuDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        int count  = menuInfoRepository.countMenuInfoByPid(id);
        if(count > 0) {
            return CommonMethod.getReturnMessageError("存在子菜单，不能删除！");
        }
        Optional<MenuInfo> op = menuInfoRepository.findById(id);
        if (op.isPresent())
            menuInfoRepository.delete(op.get());
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse menuSave(DataRequest dataRequest) {
        Integer editType = dataRequest.getInteger("editType");
        Map node = dataRequest.getMap("node");
        Integer pid = CommonMethod.getInteger(node,"pid");
        Integer id = CommonMethod.getInteger(node,"id");
        String name = CommonMethod.getString(node,"value");
        String title = CommonMethod.getString(node,"title");
        String userTypeIds = CommonMethod.getString(node,"userTypeIds");
        Optional<MenuInfo> op;
        MenuInfo m = null;
        if (id != null) {
            op = menuInfoRepository.findById(id);
            if(op.isPresent()) {
                if(editType == 0 || editType == 1)
                    CommonMethod.getReturnMessageError("主键已经存在，不能添加");
                m = op.get();
            }
        }
        if (m == null)
            m = new MenuInfo();
        m.setId(id);
        m.setTitle(title);
        m.setName(name);
        m.setPid(pid);
        m.setUserTypeIds(userTypeIds);
        menuInfoRepository.save(m);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteDictionary(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        int count = dictionaryInfoRepository.countDictionaryInfoByPid(id);
        if(count > 0) {
            return CommonMethod.getReturnMessageError("存在数据项，不能删除！");
        }
        Optional<DictionaryInfo> op = dictionaryInfoRepository.findById(id);
        if (op.isPresent()) {
            dictionaryInfoRepository.delete(op.get());
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse dictionarySave(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Integer pid = dataRequest.getInteger("pid");
        String value = dataRequest.getString("value");
        String title = dataRequest.getString("title");
        DictionaryInfo m = null;
        if(id != null) {
            Optional<DictionaryInfo> op = dictionaryInfoRepository.findById(id);
            if (op.isPresent()) {
                m = op.get();
            }
        }
        if(m == null) {
            m = new DictionaryInfo();
            m.setPid(pid);
        }
        m.setLabel(title);
        m.setValue(value);
        dictionaryInfoRepository.save(m);
        return CommonMethod.getReturnMessageOK();
    }

    public OptionItemList getDictionaryOptionItemList(DataRequest dataRequest) {
        String code = dataRequest.getString("code");
        List<DictionaryInfo> dList = dictionaryInfoRepository.getDictionaryInfoList(code);
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (DictionaryInfo d : dList) {
            itemList.add(new OptionItem(d.getId(), d.getValue(), d.getLabel()));
        }
        return new OptionItemList(0, itemList);
    }

    public ResponseEntity<StreamingResponseBody> getFileByteData(DataRequest dataRequest) {
        String fileName = dataRequest.getString("fileName");
        try {
            File file = new File(attachFolder + fileName);
            int len = (int) file.length();
            byte data[] = new byte[len];
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();
            MediaType mType = new MediaType(MediaType.APPLICATION_OCTET_STREAM);
            StreamingResponseBody stream = outputStream -> {
                outputStream.write(data);
            };
            return ResponseEntity.ok()
                    .contentType(mType)
                    .body(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }


    public DataResponse uploadPhoto(byte[] barr,String remoteFile) {
        try {
            OutputStream os = new FileOutputStream(new File(attachFolder + remoteFile));
            os.write(barr);
            os.close();
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("上传错误");
        }
    }

    public DataResponse updatePassword(DataRequest dataRequest) {
        String oldPassword = dataRequest.getString("oldPassword");  //获取oldPassword
        String newPassword = dataRequest.getString("newPassword");  //获取newPassword
        Optional<User> op = userRepository.findByUserId(CommonMethod.getUserId());
        if (!op.isPresent())
            return CommonMethod.getReturnMessageError("账户不存在！");  //通知前端操作正常
        User u = op.get();
        if (!encoder.matches(oldPassword, u.getPassword())) {
            return CommonMethod.getReturnMessageError("原始密码不正确！");
        }
        u.setPassword(encoder.encode(newPassword));
        userRepository.save(u);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/uploadHtmlString")
    @PreAuthorize(" hasRole('ADMIN') ")
    public DataResponse uploadHtmlString(DataRequest dataRequest) {
        String str = dataRequest.getString("html");
        String html = new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)));
        System.out.println(html);
        int htmlCount = ComDataUtil.getInstance().addHtmlString(html);
        return CommonMethod.getReturnData(htmlCount);
    }

    public ResponseEntity<StreamingResponseBody> htmlGetBaseHtmlPage(HttpServletRequest request) {
        String htmlCountStr = request.getParameter("htmlCount");
        Integer htmlCount = Integer.parseInt(htmlCountStr);
        String html = ComDataUtil.getInstance().getHtmlString(htmlCount);
        MediaType mType = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);
        try {
            byte data[] = html.getBytes();
            StreamingResponseBody stream = outputStream -> {
                outputStream.write(data);
            };
            return ResponseEntity.ok()
                    .contentType(mType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<StreamingResponseBody> getPdfData(DataRequest dataRequest) {
        Integer htmlCount = dataRequest.getInteger("htmlCount");
        String content = ComDataUtil.getInstance().getHtmlString(htmlCount);
        String head = "<!DOCTYPE html><html><head><style>html { font-family: \"SourceHanSansSC\", \"Open Sans\";}</style><meta charset='UTF-8' /><title>Insert title here</title></head><body>";
        content = head + content + "</body></html>";
        content = CommonMethod.removeErrorString(content, "&nbsp;", "style=\"font-family: &quot;&quot;;\"");
        return getPdfDataFromHtml(content);
    }


    //  Web 请求
    public DataResponse getPhotoImageStr(DataRequest dataRequest) {
        String fileName = dataRequest.getString("fileName");
        String str = "";
        try {
            File file = new File(attachFolder + fileName);
            int len = (int) file.length();
            byte data[] = new byte[len];
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();
            String imgStr = "data:image/png;base64,";
            String s = new String(Base64.getEncoder().encode(data));
            imgStr = imgStr + s;
            return CommonMethod.getReturnData(imgStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageError("下载错误！");
    }

    public DataResponse uploadPhotoWeb(Map pars, MultipartFile file) {
        try {
            String remoteFile = CommonMethod.getString(pars, "remoteFile");
            InputStream in = file.getInputStream();
            int size = (int) file.getSize();
            byte[] data = new byte[size];
            in.read(data);
            in.close();
            OutputStream os = new FileOutputStream(new File(attachFolder + remoteFile));
            os.write(data);
            os.close();
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {

        }
        return CommonMethod.getReturnMessageOK();
    }

}
