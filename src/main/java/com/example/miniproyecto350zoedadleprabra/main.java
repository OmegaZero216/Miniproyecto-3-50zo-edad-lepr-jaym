package com.example.miniproyecto350zoedadleprabra;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error inesperado: " + throwable.getMessage());
                alert.showAndWait();
            });
        });

    }
}
