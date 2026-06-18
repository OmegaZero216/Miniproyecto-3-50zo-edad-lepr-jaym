package com.example.miniproyecto350zoedadleprabra.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class InicioController {
    private static final String JUEGO_FXML =
            "/com/example/miniproyecto350zoedadleprabra/fxml/Juego-view.fxml";

    @FXML
    private ComboBox<Integer> cbCantidadJugadores;

    @FXML
    private Button btnIniciar;

    @FXML
    private void initialize() {
        cbCantidadJugadores.setItems(FXCollections.observableArrayList(2, 3, 4));
        cbCantidadJugadores.setValue(2);
        cbCantidadJugadores.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "" : value + " jugadores";
            }

            @Override
            public Integer fromString(String value) {
                if (value == null || value.isBlank()) {
                    return 2;
                }
                String digits = value.replaceAll("\\D+", "");
                return digits.isEmpty() ? 2 : Integer.parseInt(digits);
            }
        });
    }

    @FXML
    private void iniciarJuego() {
        int cantidadJugadores = cbCantidadJugadores.getValue() == null
                ? 2
                : cbCantidadJugadores.getValue();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(JUEGO_FXML));
            Parent root = loader.load();

            JuegoController juegoController = loader.getController();
            juegoController.inicializarJuego(cantidadJugadores);

            Stage stage = (Stage) btnIniciar.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
            stage.setFullScreen(false);
        } catch (Exception e) {
            mostrarError("Error al cargar el juego", e);
        }
    }

    private void mostrarError(String titulo, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }
}
