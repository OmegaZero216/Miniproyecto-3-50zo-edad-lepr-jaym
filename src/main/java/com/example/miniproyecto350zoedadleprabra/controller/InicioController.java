package com.example.miniproyecto350zoedadleprabra.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioController {
    @FXML
    private void iniciarJuego() {
        int cantidadCPU = cbMaquinas.getValue();

        try {
            // Cargar el FXML del juego
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/cincuenta/vista/Juego.fxml"));
            Parent root = loader.load();

            // Pasar la configuración al controlador del juego
            JuegoController juegoController = loader.getController();
            juegoController.inicializarJuego(cantidadCPU);

            // Cambiar escena
            Stage stage = (Stage) btnIniciar.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.setFullScreen(false);

        } catch (IOException e) {
            mostrarError("Error al cargar el juego", e);
        }
    }
}
