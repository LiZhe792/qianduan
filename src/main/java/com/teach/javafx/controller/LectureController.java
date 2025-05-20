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

public class LectureController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> titleColumn;
    @FXML
    private TableColumn<Map, String> speakerColumn;
    @FXML
    private TableColumn<Map, String> dateColumn;
    @FXML
    private TableColumn<Map, String> timeColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, String> capacityColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;

    @FXML
    private TextField titleField;
    @FXML
    private TextField speakerField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField capacityField;
    @FXML
    private ComboBox<OptionItem> statusComboBox;
    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField keywordTextField;

    private Integer lectureId = null;
    private ArrayList<Map> lectureList = new ArrayList<>();
    private List<OptionItem> statusList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(lectureList));
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest req = new DataRequest();
        req.add("keyword", "");
        DataResponse res = HttpRequestUtil.request("/api/lecture/getLectureList", req);
        if (res != null && res.getCode() == 0) {
            lectureList = (ArrayList<Map>) res.getData();
        }

        titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        speakerColumn.setCellValueFactory(new MapValueFactory<>("speaker"));
        dateColumn.setCellValueFactory(new MapValueFactory<>("date"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        capacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

        // 初始化状态下拉框
        statusList = new ArrayList<>();
        statusList.add(new OptionItem("未开始", "未开始"));
        statusList.add(new OptionItem("进行中", "进行中"));
        statusList.add(new OptionItem("已结束", "已结束"));
        statusComboBox.getItems().addAll(statusList);
    }

    public void clearPanel() {
        lectureId = null;
        titleField.setText("");
        speakerField.setText("");
        datePicker.setValue(null);
        timeField.setText("");
        locationField.setText("");
        capacityField.setText("");
        statusComboBox.getSelectionModel().select(-1);
        descriptionArea.setText("");
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        lectureId = CommonMethod.getInteger(form, "lectureId");
        DataRequest req = new DataRequest();
        req.add("lectureId", lectureId);
        DataResponse res = HttpRequestUtil.request("/api/lecture/getLectureInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        titleField.setText(CommonMethod.getString(form, "title"));
        speakerField.setText(CommonMethod.getString(form, "speaker"));
        datePicker.getEditor().setText(CommonMethod.getString(form, "date"));
        timeField.setText(CommonMethod.getString(form, "time"));
        locationField.setText(CommonMethod.getString(form, "location"));
        capacityField.setText(CommonMethod.getString(form, "capacity"));
        statusComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(statusList, CommonMethod.getString(form, "status")));
        descriptionArea.setText(CommonMethod.getString(form, "description"));
    }

    @FXML
    protected void onQueryButtonClick() {
        String keyword = keywordTextField.getText();
        DataRequest req = new DataRequest();
        req.add("keyword", keyword);
        DataResponse res = HttpRequestUtil.request("/api/lecture/getLectureList", req);
        if (res != null && res.getCode() == 0) {
            lectureList = (ArrayList<Map>) res.getData();
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
        lectureId = CommonMethod.getInteger(form, "lectureId");
        DataRequest req = new DataRequest();
        req.add("lectureId", lectureId);
        DataResponse res = HttpRequestUtil.request("/api/lecture/lectureDelete", req);
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
        if (titleField.getText().isEmpty()) {
            MessageDialog.showDialog("讲座标题为空，不能保存");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("title", titleField.getText());
        form.put("speaker", speakerField.getText());
        form.put("date", datePicker.getEditor().getText());
        form.put("time", timeField.getText());
        form.put("location", locationField.getText());
        form.put("capacity", capacityField.getText());
        if (statusComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("status", statusComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("description", descriptionArea.getText());

        DataRequest req = new DataRequest();
        req.add("lectureId", lectureId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/lecture/lectureEditSave", req);
        if (res.getCode() == 0) {
            lectureId = CommonMethod.getIntegerFromObject(res.getData());
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