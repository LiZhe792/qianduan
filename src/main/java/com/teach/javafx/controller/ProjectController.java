package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> projectNameColumn;
    @FXML
    private TableColumn<Map, String> projectTypeColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;
    @FXML
    private TableColumn<Map, String> startDateColumn;
    @FXML
    private TableColumn<Map, String> endDateColumn;
    @FXML
    private TableColumn<Map, String> budgetColumn;

    @FXML
    private TextField studentNumField;
    @FXML
    private TextField projectNameField;
    @FXML
    private TextField projectTypeField;
    @FXML
    private ComboBox<OptionItem> statusComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField budgetField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField searchTextField;

    private Integer projectId = null;
    private ArrayList<Map> projectList = new ArrayList<>();
    private List<OptionItem> statusList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(projectList));
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest req = new DataRequest();
        req.add("searchText", "");
        DataResponse res = HttpRequestUtil.request("/api/project/getProjectList", req);
        if (res != null && res.getCode() == 0) {
            projectList = (ArrayList<Map>) res.getData();
        }

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        projectNameColumn.setCellValueFactory(new MapValueFactory<>("projectName"));
        projectTypeColumn.setCellValueFactory(new MapValueFactory<>("projectType"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        budgetColumn.setCellValueFactory(new MapValueFactory<>("budget"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

        statusList = new ArrayList<>();
        statusList.add(new OptionItem("进行中", "进行中"));
        statusList.add(new OptionItem("已完成", "已完成"));
        statusList.add(new OptionItem("已终止", "已终止"));
        statusComboBox.getItems().addAll(statusList);

        startDatePicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        endDatePicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel() {
        projectId = null;
        studentNumField.setText("");
        projectNameField.setText("");
        projectTypeField.setText("");
        statusComboBox.getSelectionModel().select(-1);
        startDatePicker.getEditor().setText("");
        endDatePicker.getEditor().setText("");
        budgetField.setText("");
        descriptionArea.setText("");
    }

    protected void changeProjectInfo() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        projectId = CommonMethod.getInteger(form, "projectId");
        DataRequest req = new DataRequest();
        req.add("projectId", projectId);
        DataResponse res = HttpRequestUtil.request("/api/project/getProjectInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        studentNumField.setText(CommonMethod.getString(form, "studentNum"));
        projectNameField.setText(CommonMethod.getString(form, "projectName"));
        projectTypeField.setText(CommonMethod.getString(form, "projectType"));
        statusComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(statusList, CommonMethod.getString(form, "status")));
        startDatePicker.getEditor().setText(CommonMethod.getString(form, "startDate"));
        endDatePicker.getEditor().setText(CommonMethod.getString(form, "endDate"));
        budgetField.setText(CommonMethod.getString(form, "budget"));
        descriptionArea.setText(CommonMethod.getString(form, "description"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeProjectInfo();
    }

    @FXML
    protected void onQueryButtonClick() {
        String searchText = searchTextField.getText();
        DataRequest req = new DataRequest();
        req.add("searchText", searchText);
        DataResponse res = HttpRequestUtil.request("/api/project/getProjectList", req);
        if (res != null && res.getCode() == 0) {
            projectList = (ArrayList<Map>) res.getData();
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
        projectId = CommonMethod.getInteger(form, "projectId");
        DataRequest req = new DataRequest();
        req.add("projectId", projectId);
        DataResponse res = HttpRequestUtil.request("/api/project/projectDelete", req);
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
            MessageDialog.showDialog("学号为空，不能保存");
            return;
        }
        if (projectNameField.getText().isEmpty()) {
            MessageDialog.showDialog("项目名称为空，不能保存");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("studentNum", studentNumField.getText());
        form.put("projectName", projectNameField.getText());
        form.put("projectType", projectTypeField.getText());
        if (statusComboBox.getSelectionModel() != null && statusComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("status", statusComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("startDate", startDatePicker.getEditor().getText());
        form.put("endDate", endDatePicker.getEditor().getText());
        form.put("budget", budgetField.getText());
        form.put("description", descriptionArea.getText());

        DataRequest req = new DataRequest();
        req.add("projectId", projectId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/project/projectEditSave", req);
        if (res.getCode() == 0) {
            projectId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onExportButtonClick() {
        String searchText = searchTextField.getText();
        DataRequest req = new DataRequest();
        req.add("searchText", searchText);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/project/getProjectListExcel", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("选择保存的文件");
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
        onExportButtonClick();
    }
}