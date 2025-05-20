package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternshipController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> companyColumn;
    @FXML
    private TableColumn<Map, String> positionColumn;
    @FXML
    private TableColumn<Map, String> startDateColumn;
    @FXML
    private TableColumn<Map, String> endDateColumn;
    @FXML
    private TableColumn<Map, String> supervisorColumn;
    @FXML
    private TableColumn<Map, String> contactColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;

    @FXML
    private TextField studentNumField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField companyField;
    @FXML
    private TextField positionField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField supervisorField;
    @FXML
    private TextField contactField;
    @FXML
    private ComboBox<OptionItem> statusComboBox;
    @FXML
    private TextArea evaluationArea;

    @FXML
    private TextField numNameTextField;

    private Integer internshipId = null;
    private Integer personId = null;
    private ArrayList<Map> internshipList = new ArrayList<>();
    private List<OptionItem> statusList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(internshipList));
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest req = new DataRequest();
        req.add("numName", "");
        DataResponse res = HttpRequestUtil.request("/api/internship/getInternshipList", req);
        if (res != null && res.getCode() == 0) {
            internshipList = (ArrayList<Map>) res.getData();
        }

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        companyColumn.setCellValueFactory(new MapValueFactory<>("company"));
        positionColumn.setCellValueFactory(new MapValueFactory<>("position"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        supervisorColumn.setCellValueFactory(new MapValueFactory<>("supervisor"));
        contactColumn.setCellValueFactory(new MapValueFactory<>("contact"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

        // 初始化状态下拉框
        statusList = new ArrayList<>();
        statusList.add(new OptionItem("进行中", "进行中"));
        statusList.add(new OptionItem("已完成", "已完成"));
        statusComboBox.getItems().addAll(statusList);
    }

    public void clearPanel() {
        internshipId = null;
        personId = null;
        studentNumField.setText("");
        studentNameField.setText("");
        companyField.setText("");
        positionField.setText("");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        supervisorField.setText("");
        contactField.setText("");
        statusComboBox.getSelectionModel().select(-1);
        evaluationArea.setText("");
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        internshipId = CommonMethod.getInteger(form, "internshipId");
        DataRequest req = new DataRequest();
        req.add("internshipId", internshipId);
        DataResponse res = HttpRequestUtil.request("/api/internship/getInternshipInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        personId = CommonMethod.getInteger(form, "personId");
        studentNumField.setText(CommonMethod.getString(form, "studentNum"));
        studentNameField.setText(CommonMethod.getString(form, "studentName"));
        companyField.setText(CommonMethod.getString(form, "company"));
        positionField.setText(CommonMethod.getString(form, "position"));
        startDatePicker.getEditor().setText(CommonMethod.getString(form, "startDate"));
        endDatePicker.getEditor().setText(CommonMethod.getString(form, "endDate"));
        supervisorField.setText(CommonMethod.getString(form, "supervisor"));
        contactField.setText(CommonMethod.getString(form, "contact"));
        statusComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(statusList, CommonMethod.getString(form, "status")));
        evaluationArea.setText(CommonMethod.getString(form, "evaluation"));
    }

    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/internship/getInternshipList", req);
        if (res != null && res.getCode() == 0) {
            internshipList = (ArrayList<Map>) res.getData();
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
        internshipId = CommonMethod.getInteger(form, "internshipId");
        DataRequest req = new DataRequest();
        req.add("internshipId", internshipId);
        DataResponse res = HttpRequestUtil.request("/api/internship/internshipDelete", req);
        if(res!= null) {
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
        if (personId == null) {
            MessageDialog.showDialog("请先选择学生！");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("personId", personId);
        form.put("company", companyField.getText());
        form.put("position", positionField.getText());
        form.put("startDate", startDatePicker.getEditor().getText());
        form.put("endDate", endDatePicker.getEditor().getText());
        form.put("supervisor", supervisorField.getText());
        form.put("contact", contactField.getText());
        if (statusComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("status", statusComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("evaluation", evaluationArea.getText());

        DataRequest req = new DataRequest();
        req.add("internshipId", internshipId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/internship/internshipEditSave", req);
        if (res.getCode() == 0) {
            internshipId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSelectStudentButtonClick() {
        String studentNum = studentNumField.getText();
        if (studentNum.isEmpty()) {
            MessageDialog.showDialog("请输入学号！");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("studentNum", studentNum);
        DataResponse res = HttpRequestUtil.request("/api/student/getPersonIdByStudentNum", req);

        if (res.getCode() == 0) {
            Map<String, String> data = (Map<String, String>) res.getData();
            personId = Integer.parseInt(data.get("personId"));

            // 获取学生信息
            req = new DataRequest();
            req.add("personId", personId);
            res = HttpRequestUtil.request("/api/student/getStudentInfo", req);

            if (res.getCode() == 0) {
                Map<String, Object> studentInfo = (Map<String, Object>) res.getData();
                studentNameField.setText(CommonMethod.getString(studentInfo, "name"));
            }
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
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
}