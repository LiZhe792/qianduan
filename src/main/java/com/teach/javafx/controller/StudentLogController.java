package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/**
 * 学生日志管理控制器 对应 student_log_panel.fxml
 */
public class StudentLogController extends ToolController {

    @FXML
    private TableView<Map> logTableView; // 日志信息表格
    @FXML
    private TableColumn<Map, String> logIdColumn;
    @FXML
    private TableColumn<Map, String> logTypeColumn;   // 日志类型列
    @FXML
    private TableColumn<Map, String> logContentColumn; // 日志内容列
    @FXML
    private TableColumn<Map, String> logTimeColumn;   // 日志时间列
    @FXML
    private TableColumn<Map, String> amountColumn;    // 金额列
    @FXML
    private TextField numField; //
    @FXML
    private ComboBox<String> logTypeComboBox;        // 日志类型选择框
    @FXML
    private TextField logContentField;                // 日志内容输入框
    @FXML
    private DatePicker logTimePicker;               // 日志时间选择器
    @FXML
    private TextField amountField;                 // 金额输入框

    @FXML
    private TextField searchField;                 // 查询输入框

    private Integer currentLogId = null;           // 当前编辑的日志ID
    private ObservableList<Map> observableLogList = FXCollections.observableArrayList(); // 表格数据
    private ArrayList<Map<String, String>> studentLogList = new ArrayList<>();

    private List<String> logTypeList = Arrays.asList(  // 日志类型列表
            "外出请假", "生活消费", "学习记录", "其他"
    );

    private void setTableViewData(){
        observableLogList.clear();
        for (int j = 0; j < studentLogList.size(); j++) {
            observableLogList.addAll(FXCollections.observableArrayList(studentLogList.get(j)));
        }
        logTableView.setItems(observableLogList);
    }

    /**
     * 初始化方法，设置表格和表单初始状态
     */
    @FXML
    public void initialize() {
        // init
        DataRequest data = new DataRequest();
        DataResponse dataResponse = HttpRequestUtil.request("/api/studentLog/getLogList", data);
        if (dataResponse != null) {
            if (dataResponse.getCode() != 0 ){
                System.err.println();
            } else {
                studentLogList = (ArrayList<Map<String, String>>) dataResponse.getData();
            }
        } else {
            System.err.println("error");
        }

        // 初始化表格列
        logIdColumn.setCellValueFactory(new MapValueFactory<>("logId"));
        logTypeColumn.setCellValueFactory(new MapValueFactory<>("logType"));
        logContentColumn.setCellValueFactory(new MapValueFactory<>("logContent"));
        logTimeColumn.setCellValueFactory(new MapValueFactory<>("logTime"));
        amountColumn.setCellValueFactory(new MapValueFactory<>("amount"));

        // 初始化日志类型下拉框
        logTypeComboBox.getItems().addAll(logTypeList);

        // 表格选择监听
        logTableView.getSelectionModel().getSelectedIndices().addListener(this::onLogRowSelect);

        // 加载初始数据
        loadLogList("");
        setTableViewData();
    }

    /**
     * 加载日志列表
     * @param keyword 查询关键字
     */
    private void loadLogList(String keyword) {
        DataRequest req = new DataRequest();
        req.add("logType", logTypeComboBox.getValue());
        DataResponse res = HttpRequestUtil.request("/api/studentLog/getLogList", req);

        if (res != null && res.getCode() == 0) {
            List<Map> logList = (List<Map>) res.getData();
            observableLogList.clear();
            observableLogList.addAll(logList);
            logTableView.setItems(observableLogList);
        }
    }

    /**
     * 表格行选择事件处理
     */
    private void onLogRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map selectedLog = logTableView.getSelectionModel().getSelectedItem();
        if (selectedLog != null) {
            currentLogId = CommonMethod.getInteger(selectedLog, "logId");
            logTypeComboBox.setValue(CommonMethod.getString(selectedLog, "logType"));
            logContentField.setText(CommonMethod.getString(selectedLog, "logContent"));
            logTimePicker.setValue(CommonMethod.localDateFromString(CommonMethod.getString(selectedLog, "logTime")));
            amountField.setText(CommonMethod.getString(selectedLog, "amount"));
        } else {
            clearForm();
        }
    }

    /**
     * 清空表单
     */
    private void clearForm() {
        currentLogId = null;
        logTypeComboBox.setValue(null);
        logContentField.clear();
        logTimePicker.setValue(null);
        amountField.clear();
    }

    // ========== 按钮事件处理 ==========

    /**
     * 查询按钮点击事件
     */
    @FXML
    protected void onQueryButtonClick() {
        String keyword = searchField.getText();
        loadLogList(keyword);
    }

    /**
     * 添加按钮点击事件
     */
    @FXML
    protected void onAddButtonClick() {
        clearForm();
    }

    /**
     * 删除按钮点击事件
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map selectedLog = logTableView.getSelectionModel().getSelectedItem();
        if (selectedLog == null) {
            MessageDialog.showDialog("请选择要删除的日志");
            return;
        }

        int confirm = MessageDialog.choiceDialog("确认删除该日志？");
        if (confirm == MessageDialog.CHOICE_YES) {
            Integer logId = CommonMethod.getInteger(selectedLog, "logId");
            DataRequest req = new DataRequest();
            req.add("logId", logId);
            DataResponse res = HttpRequestUtil.request("/api/studentLog/logDelete", req);

            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功");
                loadLogList("");
                clearForm();
            } else {
                MessageDialog.showDialog("删除失败：" + res.getMsg());
            }
        }
    }

    /**
     * 保存按钮点击事件
     */
    @FXML

    protected void onSaveButtonClick() {
        if (logTypeComboBox.getValue() == null || logTypeComboBox.getValue().isEmpty()) {
            MessageDialog.showDialog("请选择日志类型");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("num", numField.getText());
        form.put("logType", logTypeComboBox.getValue());
        form.put("logContent", logContentField.getText());
        form.put("logTime", CommonMethod.formatDate(logTimePicker.getValue()));

        // 修改部分：构建 Map 并调用 getDouble 方法
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("amount", amountField.getText());
        form.put("amount", CommonMethod.getDouble(amountMap, "amount"));

        DataRequest req = new DataRequest();
        req.add("form", form);
        if (currentLogId != null) {
            req.add("logId", currentLogId);
        }

        DataResponse res = HttpRequestUtil.request("/api/studentLog/logSave", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("保存成功");
            loadLogList("");
            clearForm();
        } else {
            MessageDialog.showDialog("保存失败：" + res.getMsg());
        }
    }

    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择日志数据文件");
        fileDialog.setInitialDirectory(new File("D:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res = HttpRequestUtil.importData("/api/studentLog/importLogData", file.getPath(), paras);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            loadLogList("");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    // ========== 工具方法 ==========

    @Override
    public void doNew() {
        onAddButtonClick();
    }

    @Override
    public void doSave() {
        onSaveButtonClick();
    }

    @Override
    public void doDelete() {
        onDeleteButtonClick();
    }
}