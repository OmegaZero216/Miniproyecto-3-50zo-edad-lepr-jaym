package com.example.miniproyecto350zoedadleprabra.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Timer {
    private final StackPane panel;
    private final Label lblTiempo;
    private final ImageView imgContador;
    private Thread hiloTemporizador;
    private volatile boolean corriendo = false;
    private int segundosRestantes;
    private Runnable alTerminar;

    public Timer(StackPane panel, Label lblTiempo, ImageView imgContador) {
        this.panel = panel;
        this.lblTiempo = lblTiempo;
        this.imgContador = imgContador;
    }

    public void iniciar(int segundos, Runnable callbackAlTerminar) {
        detener();
        this.segundosRestantes = segundos;
        this.alTerminar = callbackAlTerminar;
        corriendo = true;

        Platform.runLater(() -> {
            panel.setVisible(true);
            actualizarDisplay();
        });

        hiloTemporizador = new Thread(() -> {
            while (corriendo && segundosRestantes > 0) {
                try {
                    Thread.sleep(1000);
                    segundosRestantes--;
                    Platform.runLater(this::actualizarDisplay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (corriendo) {
                Platform.runLater(() -> {
                    panel.setVisible(false);
                    if (alTerminar != null) alTerminar.run();
                });
            }
        });
        hiloTemporizador.setDaemon(true);
        hiloTemporizador.setName("Hilo-Temporizador");
        hiloTemporizador.start();
    }

    private void actualizarDisplay() {
        lblTiempo.setText(String.valueOf(segundosRestantes));

        // Cambiar color según tiempo restante
        if (segundosRestantes <= 2) {
            lblTiempo.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 28px; -fx-font-weight: bold;");
        } else if (segundosRestantes <= 5) {
            lblTiempo.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 28px; -fx-font-weight: bold;");
        } else {
            lblTiempo.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 28px; -fx-font-weight: bold;");
        }
    }

    public void detener() {
        corriendo = false;
        if (hiloTemporizador != null && hiloTemporizador.isAlive()) {
            hiloTemporizador.interrupt();
        }
        Platform.runLater(() -> panel.setVisible(false));
    }
}