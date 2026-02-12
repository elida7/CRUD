package app;

import app.config.DatabaseConfig;
import app.ui.ConnectionDialogController;
import app.ui.DynamicCrudController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1) Diálogo de conexión
        FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/connection_dialog.fxml"));
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Conexión a Base de Datos");
        dialogStage.setScene(new Scene(dialogLoader.load()));
        dialogStage.showAndWait();

        ConnectionDialogController dialogController = dialogLoader.getController();
        DatabaseConfig config = dialogController.getConfig();
        if (config == null) {
            // usuario canceló
            return;
        }

        // 2) Pantalla principal
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/dynamic_crud_view.fxml"));
        Scene scene = new Scene(mainLoader.load());

        DynamicCrudController controller = mainLoader.getController();
        controller.init(config);

        primaryStage.setTitle("CRUD Genérico - Programación Visual");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

