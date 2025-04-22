package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.request.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentHonorController 学生荣誉交互控制类 对应 student_honor_panel.fxml
 */
public class StudentHonorController extends ToolController {
    private ImageView photoImageView;
    @FXML
    private TableView<Map> dataTableView;  //学生荣誉信息表
    @FXML
    private TableColumn<Map, String> studentNumColumn;   //学生荣誉信息表 学号列
    @FXML
    private TableColumn<Map, String> studentNameColumn; //学生荣誉信息表 姓名列
    @FXML
    private TableColumn<Map, String> honorNameColumn;  //学生荣誉信息表 荣誉名称列
    @FXML
    private TableColumn<Map, String> honorLevelColumn; //学生荣誉信息表 荣誉等级列
    @FXML
    private TableColumn<Map, String> honorDateColumn; //学生荣誉信息表 获得日期列

    @FXML
    private TextField studentNumField; //学生荣誉信息  学号输入域
    @FXML
    private TextField studentNameField;  //学生荣誉信息  姓名输入域
    @FXML
    private TextField honorNameField; //学生荣誉信息  荣誉名称输入域
    @FXML
    private TextField honorLevelField;  //学生荣誉信息  荣誉等级输入域（改为文本框）
    @FXML
    private DatePicker honorDatePick;  //学生荣誉信息  获得日期选择域

    @FXML
    private TextField searchTextField;  //查询 学号姓名输入域

    private Integer honorId = null;  //当前编辑修改的学生荣誉的主键

    private ArrayList<Map> studentHonorList = new ArrayList();  // 学生荣誉信息列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentHonorList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentHonorList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);

        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("search", "");
        res = HttpRequestUtil.request("/api/studentHonor/getHonorList", req);
        if (res != null && res.getCode() == 0) {
            studentHonorList = (ArrayList<Map>) res.getData();
        }
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        honorNameColumn.setCellValueFactory(new MapValueFactory<>("honorName"));
        honorLevelColumn.setCellValueFactory(new MapValueFactory<>("honorLevelName"));
        honorDateColumn.setCellValueFactory(new MapValueFactory<>("honorDate"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

        honorDatePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel() {
        honorId = null;
        studentNumField.setText("");
        studentNameField.setText("");
        honorNameField.setText("");
        honorLevelField.setText("");
        honorDatePick.getEditor().setText("");
    }

    protected void changeStudentHonorInfo() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        honorId = CommonMethod.getInteger(form, "honorId");
        DataRequest req = new DataRequest();
        req.add("honorId", honorId);
        DataResponse res = HttpRequestUtil.request("/api/studentHonor/getHonorDetails", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        studentNumField.setText(CommonMethod.getString(form, "studentNum"));
        studentNameField.setText(CommonMethod.getString(form, "studentName"));
        honorNameField.setText(CommonMethod.getString(form, "honorName"));
        honorLevelField.setText(CommonMethod.getString(form, "honorLevel"));
        honorDatePick.getEditor().setText(CommonMethod.getString(form, "honorDate"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentHonorInfo();
    }

    @FXML
    protected void onQueryButtonClick() {
        String search = searchTextField.getText();
        DataRequest req = new DataRequest();
        req.add("search", search);
        DataResponse res = HttpRequestUtil.request("/api/studentHonor/getHonorList", req);
        if (res != null && res.getCode() == 0) {
            studentHonorList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        honorId = CommonMethod.getInteger(form, "honorId");
        DataRequest req = new DataRequest();
        req.add("honorId", honorId);
        DataResponse res = HttpRequestUtil.request("/api/studentHonor/deleteHonor", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (studentNumField.getText().isEmpty()) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        if (studentNameField.getText().isEmpty()) {
            MessageDialog.showDialog("姓名为空，不能修改");
            return;
        }
        if (honorNameField.getText().isEmpty()) {
            MessageDialog.showDialog("荣誉名称为空，不能修改");
            return;
        }
        if (honorLevelField.getText().isEmpty()) {
            MessageDialog.showDialog("请输入荣誉等级");
            return;
        }
        if (honorDatePick.getEditor().getText().isEmpty()) {
            MessageDialog.showDialog("请选择获得日期");
            return;
        }

        String studentNum = studentNumField.getText();
        Integer personId = getPersonIdByStudentNum(studentNum);
        if (personId == null) {
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("studentNum", studentNumField.getText());
        form.put("studentName", studentNameField.getText());
        form.put("honorName", honorNameField.getText());
        form.put("honorLevel", honorLevelField.getText());
        form.put("honorDate", honorDatePick.getEditor().getText());
        form.put("personId", personId); // 添加 personId 到表单

        DataRequest req = new DataRequest();
        req.add("form", form);

        String apiUrl;
        if (honorId == null) {
            apiUrl = "/api/studentHonor/addHonor";
        } else {
            req.add("honorId", honorId);
            apiUrl = "/api/studentHonor/updateHonor";
        }

        DataResponse res = HttpRequestUtil.request(apiUrl, req);
        if (res != null) {
            if (res.getCode() == 0) {
                if (honorId == null) {
                    honorId = CommonMethod.getIntegerFromObject(res.getData());
                }
                MessageDialog.showDialog("提交成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog("保存失败: " + res.getMsg());
            }
        } else {
            MessageDialog.showDialog("保存失败: 未收到响应");
        }
    }

    private Integer getPersonIdByStudentNum(String studentNum) {
        DataRequest req = new DataRequest();
        req.add("studentNum", studentNum);
        DataResponse res = HttpRequestUtil.request("/api/student/getPersonIdByStudentNum", req);
        if (res != null && res.getCode() == 0) {
            Map map = (Map) res.getData();
            String personId =CommonMethod.getString(map, "personId");
            return Integer.parseInt(personId);
        }
        MessageDialog.showDialog("根据学号未找到对应的学生信息");
        return null;
    }

    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    public void doExport() {
        String search = searchTextField.getText();
        DataRequest req = new DataRequest();
        req.add("search", search);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/studentHonor/getHonorListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("请选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("请选择学生荣誉数据表");
        fileDialog.setInitialDirectory(new File("D:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        if (file != null) {
            String paras = "";
            DataResponse res = HttpRequestUtil.importData("/api/studentHonor/importHonorData", file.getPath(), paras);
            if (res.getCode() == 0) {
                MessageDialog.showDialog("上传成功！");
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }
}