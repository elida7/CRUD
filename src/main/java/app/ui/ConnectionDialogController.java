package app.ui;

import app.config.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectionDialogController {

    @FXML private TextField txtUrl;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtTable;
    @FXML private TextField txtPk;

    private DatabaseConfig config;

    @FXML
    private void initialize() {
        // Valores por defecto para PostgreSQL
        txtUrl.setText("jdbc:postgresql://localhost:5432/notas");
        txtUser.setText("postgres");
        txtPassword.setText("27418291");
        txtTable.setText("materias");
        txtPk.setText("id_materia");
    }

    @FXML
    private void onConnect() {
        this.config = new DatabaseConfig(
                txtUrl.getText(),
                txtUser.getText(),
                txtPassword.getText(),
                txtTable.getText(),
                txtPk.getText()
        );
        Stage stage = (Stage) txtUrl.getScene().getWindow();
        stage.close();
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}

