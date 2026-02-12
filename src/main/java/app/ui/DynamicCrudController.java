package app.ui;

import app.config.DatabaseConfig;
import app.db.DatabaseConnectionFactory;
import app.db.GenericRow;
import app.db.GenericTableRepository;
import app.service.GenericTableService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicCrudController {

    @FXML private TableView<GenericRow> tableView;
    @FXML private VBox formContainer;

    private GenericTableService service;
    private List<String> columnNames;
    private final Map<String, TextField> fieldMap = new HashMap<>();
    private String primaryKey;

    private final ObservableList<GenericRow> data = FXCollections.observableArrayList();

    public void init(DatabaseConfig config) throws SQLException {
        this.primaryKey = config.getPrimaryKeyColumn();

        DatabaseConnectionFactory factory = new DatabaseConnectionFactory(config);
        GenericTableRepository repo = new GenericTableRepository(factory);
        this.service = new GenericTableService(repo);

        this.columnNames = service.getColumnNames();

        createTableColumns();
        createForm();
        loadData();
        setupSelectionBinding();
    }

    private void createTableColumns() {
        tableView.getColumns().clear();
        for (String col : columnNames) {
            TableColumn<GenericRow, Object> column = new TableColumn<>(col);
            column.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().get(col)));
            tableView.getColumns().add(column);
        }
        tableView.setItems(data);
    }

    private void createForm() {
        formContainer.getChildren().clear();
        fieldMap.clear();
        for (String col : columnNames) {
            Label label = new Label(col);
            TextField field = new TextField();
            field.setPromptText(col);
            fieldMap.put(col, field);
            formContainer.getChildren().addAll(label, field);
        }
    }

    private void loadData() throws SQLException {
        data.setAll(service.getAll());
    }

    private void setupSelectionBinding() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                for (String col : columnNames) {
                    Object value = selected.get(col);
                    TextField field = fieldMap.get(col);
                    if (field != null) {
                        field.setText(value == null ? "" : value.toString());
                    }
                }
            }
        });
    }

    @FXML
    private void onNuevo() {
        tableView.getSelectionModel().clearSelection();
        fieldMap.values().forEach(TextField::clear);
    }

    @FXML
    private void onGuardar() {
        try {
            GenericRow row = new GenericRow();
            for (String col : columnNames) {
                String text = fieldMap.get(col).getText();
                if (text.isEmpty()) {
                    row.put(col, null);
                } else {
                    // Intentamos inferir tipos básicos (Integer / Double); si falla, se mantiene como String
                    Object value;
                    try {
                        value = Integer.valueOf(text);
                    } catch (NumberFormatException exInt) {
                        try {
                            value = Double.valueOf(text);
                        } catch (NumberFormatException exDouble) {
                            value = text;
                        }
                    }
                    row.put(col, value);
                }
            }

            GenericRow selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                row.put(primaryKey, selected.get(primaryKey));
                service.update(row);
            } else {
                service.create(row);
            }

            loadData();
            onNuevo();
        } catch (SQLException e) {
            showError(e);
        }
    }

    @FXML
    private void onEliminar() {
        GenericRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        try {
            Object pkValue = selected.get(primaryKey);
            service.delete(pkValue);
            loadData();
            onNuevo();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
        alert.showAndWait();
        e.printStackTrace();
    }
}