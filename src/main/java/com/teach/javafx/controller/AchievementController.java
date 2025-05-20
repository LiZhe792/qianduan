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

public class AchievementController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> titleColumn;
    @FXML
    private TableColumn<Map, String> typeColumn;
    @FXML
    private TableColumn<Map, String> dateColumn;
    @FXML
    private TableColumn<Map, String> levelColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;

    @FXML
    private TextField studentNumField;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<OptionItem> typeComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<OptionItem> levelComboBox;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<OptionItem> statusComboBox;

    @FXML
    private TextField keywordTextField;

    private Integer achievementId = null;
    private ArrayList<Map> achievementList = new ArrayList<>();
    private List<OptionItem> typeList;
    private List<OptionItem> levelList;
    private List<OptionItem> statusList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(achievementList));
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest req = new DataRequest();
        req.add("keyword", "");
        DataResponse res = HttpRequestUtil.request("/api/achievement/getAchievementList", req);
        if (res != null && res.getCode() == 0) {
            achievementList = (ArrayList<Map>) res.getData();
        }

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        dateColumn.setCellValueFactory(new MapValueFactory<>("date"));
        levelColumn.setCellValueFactory(new MapValueFactory<>("level"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

        // 初始化下拉框选项
        typeList = new ArrayList<>();
        typeList.add(new OptionItem("论文", "论文"));
        typeList.add(new OptionItem("专利", "专利"));
        typeList.add(new OptionItem("竞赛", "竞赛"));
        typeList.add(new OptionItem("项目", "项目"));
        typeComboBox.getItems().addAll(typeList);

        levelList = new ArrayList<>();
        levelList.add(new OptionItem("国家级", "国家级"));
        levelList.add(new OptionItem("省级", "省级"));
        levelList.add(new OptionItem("市级", "市级"));
        levelComboBox.getItems().addAll(levelList);

        statusList = new ArrayList<>();
        statusList.add(new OptionItem("已发表", "已发表"));
        statusList.add(new OptionItem("已授权", "已授权"));
        statusList.add(new OptionItem("进行中", "进行中"));
        statusComboBox.getItems().addAll(statusList);
    }

    public void clearPanel() {
        achievementId = null;
        studentNumField.setText("");
        titleField.setText("");
        typeComboBox.getSelectionModel().select(-1);
        datePicker.setValue(null);
        levelComboBox.getSelectionModel().select(-1);
        descriptionArea.setText("");
        statusComboBox.getSelectionModel().select(-1);
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        achievementId = CommonMethod.getInteger(form, "achievementId");
        DataRequest req = new DataRequest();
        req.add("achievementId", achievementId);
        DataResponse res = HttpRequestUtil.request("/api/achievement/getAchievementInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        studentNumField.setText(CommonMethod.getString(form, "studentNum"));
        titleField.setText(CommonMethod.getString(form, "title"));
        typeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(typeList, CommonMethod.getString(form, "type")));
        datePicker.getEditor().setText(CommonMethod.getString(form, "date"));
        levelComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(levelList, CommonMethod.getString(form, "level")));
        descriptionArea.setText(CommonMethod.getString(form, "description"));
        statusComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(statusList, CommonMethod.getString(form, "status")));
    }

    @FXML
    protected void onQueryButtonClick() {
        String keyword = keywordTextField.getText();
        DataRequest req = new DataRequest();
        req.add("keyword", keyword);
        DataResponse res = HttpRequestUtil.request("/api/achievement/getAchievementList", req);
        if (res != null && res.getCode() == 0) {
            achievementList = (ArrayList<Map>) res.getData();
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
        achievementId = CommonMethod.getInteger(form, "achievementId");
        DataRequest req = new DataRequest();
        req.add("achievementId", achievementId);
        DataResponse res = HttpRequestUtil.request("/api/achievement/achievementDelete", req);
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
        if (studentNumField.getText().isEmpty()) {
            MessageDialog.showDialog("学号为空，不能保存");
            return;
        }
        if (titleField.getText().isEmpty()) {
            MessageDialog.showDialog("成果标题为空，不能保存");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("studentNum", studentNumField.getText());
        form.put("title", titleField.getText());
        if (typeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("type", typeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("date", datePicker.getEditor().getText());
        if (levelComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("level", levelComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("description", descriptionArea.getText());
        if (statusComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("status", statusComboBox.getSelectionModel().getSelectedItem().getValue());

        DataRequest req = new DataRequest();
        req.add("achievementId", achievementId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/achievement/achievementEditSave", req);
        if (res.getCode() == 0) {
            achievementId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
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