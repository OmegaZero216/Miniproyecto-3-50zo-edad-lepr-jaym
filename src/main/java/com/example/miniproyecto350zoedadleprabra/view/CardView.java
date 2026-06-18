package com.example.miniproyecto350zoedadleprabra.view;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.example.miniproyecto350zoedadleprabra.model.Carta;
import com.example.miniproyecto350zoedadleprabra.util.RecursosImagen;

public class CardView extends StackPane {

    private final ImageView imageView;
    private final Label labelValor;
    private final boolean mostrarValor;
    private Carta carta;
    private boolean seleccionada = false;

    // Efectos visuales
    private static final DropShadow SOMBRA_NORMAL = new DropShadow(10, Color.BLACK);
    private static final DropShadow SOMBRA_SELECCIONADA = new DropShadow(15, Color.GOLD);
    private static final DropShadow SOMBRA_JUGABLE = new DropShadow(12, Color.LIGHTGREEN);

    public CardView(boolean mostrarValor) {
        this.mostrarValor = mostrarValor;

        // ImageView para la carta
        imageView = new ImageView();
        imageView.setFitWidth(RecursosImagen.ANCHO_CARTA);
        imageView.setFitHeight(RecursosImagen.ALTO_CARTA);
        imageView.setPreserveRatio(true);
        imageView.setEffect(SOMBRA_NORMAL);

        // Label para mostrar el valor (opcional)
        labelValor = new Label();
        labelValor.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);" +
                        "-fx-text-fill: #FFD700;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 2 8;" +
                        "-fx-background-radius: 10;"
        );
        labelValor.setVisible(mostrarValor);
        labelValor.setManaged(mostrarValor);
        StackPane.setAlignment(labelValor, Pos.BOTTOM_CENTER);

        getChildren().addAll(imageView, labelValor);
        setAlignment(Pos.CENTER);
    }

    /**
     * Establece la carta a mostrar
     */
    public void setCarta(Carta carta) {
        this.carta = carta;
        if (carta != null) {
            Image imagen = RecursosImagen.cargarImagenCarta(carta);
            imageView.setImage(imagen);
            actualizarLabelValor();
        }
    }

    /**
     * Muestra el reverso de la carta
     */
    public void mostrarReverso() {
        this.carta = null;
        Image imagen = RecursosImagen.cargarReverso();
        imageView.setImage(imagen);
        labelValor.setVisible(false);
    }

    /**
     * Actualiza el label con el valor de juego de la carta
     */
    private void actualizarLabelValor() {
        if (carta != null && mostrarValor) {
            int valor = carta.getValorJuego();
            String texto;

            if (valor > 0) {
                texto = "+" + valor;
                labelValor.setStyle(
                        "-fx-background-color: rgba(0, 100, 0, 0.8);" +
                                "-fx-text-fill: #00FF00;"
                );
            } else if (valor < 0) {
                texto = "" + valor;
                labelValor.setStyle(
                        "-fx-background-color: rgba(139, 0, 0, 0.8);" +
                                "-fx-text-fill: #FF6B6B;"
                );
            } else {
                texto = "0";
                labelValor.setStyle(
                        "-fx-background-color: rgba(0, 0, 0, 0.7);" +
                                "-fx-text-fill: #FFD700;"
                );
            }

            labelValor.setText(texto);
            labelValor.setVisible(true);
        }
    }

    /**
     * Marca la carta como seleccionada
     */
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;

        if (seleccionada) {
            imageView.setEffect(SOMBRA_SELECCIONADA);
            animarSeleccion();
        } else {
            imageView.setEffect(SOMBRA_NORMAL);
            setTranslateY(0);
        }
    }

    /**
     * Marca la carta como jugable (válida para jugar)
     */
    public void setJugable(boolean jugable) {
        if (jugable && !seleccionada) {
            imageView.setEffect(SOMBRA_JUGABLE);
        } else if (!seleccionada) {
            imageView.setEffect(SOMBRA_NORMAL);
        }
    }

    /**
     * Animación de selección
     */
    private void animarSeleccion() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), this);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    /**
     * Animación al jugar la carta
     */
    public void animarJugada(Runnable onFinished) {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), this);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.3);
        st.setToY(1.3);
        st.setOnFinished(e -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        st.play();
    }

    // Getters
    public Carta getCarta() { return carta; }
    public boolean isSeleccionada() { return seleccionada; }

    /**
     * Establece el tamaño de la carta
     */
    public void setTamano(double ancho, double alto) {
        imageView.setFitWidth(ancho);
        imageView.setFitHeight(alto);
    }
}