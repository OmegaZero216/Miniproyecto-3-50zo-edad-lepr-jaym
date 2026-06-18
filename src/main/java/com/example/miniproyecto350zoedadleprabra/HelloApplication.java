package com.example.miniproyecto350zoedadleprabra;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final String INICIO_FXML =
            "/com/example/miniproyecto350zoedadleprabra/fxml/Inicio-view.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                Platform.runLater(() -> mostrarError("Error inesperado", throwable)));

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(INICIO_FXML));
        Parent root = loader.load();

        primaryStage.setTitle("50zo");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private static void mostrarError(String titulo, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(throwable.getMessage() == null
                ? throwable.getClass().getSimpleName()
                : throwable.getMessage());
        alert.showAndWait();
    }
}
