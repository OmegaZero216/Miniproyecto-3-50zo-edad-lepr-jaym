package com.example.miniproyecto350zoedadleprabra.controller;

public class JuegoController {
    // En JuegoController.java

    private CardView crearVistaCarta(Carta carta, boolean esHumano) {
        CardView cardView = new CardView(esHumano); // Mostrar valor solo para humano

        if (esHumano) {
            cardView.setCarta(carta);
            cardView.setOnMouseClicked(e -> manejarClickCarta(cardView));
            cardView.setCursor(javafx.scene.Cursor.HAND);

            // Efecto hover
            cardView.setOnMouseEntered(e -> {
                if (!cardView.isSeleccionada()) {
                    cardView.setTranslateY(-10);
                }
            });
            cardView.setOnMouseExited(e -> {
                if (!cardView.isSeleccionada()) {
                    cardView.setTranslateY(0);
                }
            });
        } else {
            cardView.mostrarReverso();
        }

        return cardView;
    }

    private void actualizarManosEnUI() {
        // Para el jugador humano
        panelManoHumano.getChildren().clear();
        Jugador humano = juego.getJugadorHumano();

        for (Carta carta : humano.getMano()) {
            CardView cardView = crearVistaCarta(carta, true);

            // Marcar cartas jugables
            cardView.setJugable(carta.puedeJugarseConMesa(juego.getSumaMesa()));

            panelManoHumano.getChildren().add(cardView);
        }

        // Para las CPU (mostrar reverso)
        actualizarManoCPU(panelCPU1, manoCPU1, juego.getJugadores().get(1));
        // ... etc
    }

    private void actualizarManoCPU(VBox panel, HBox manoBox, Jugador cpu) {
        if (cpu == null || cpu.estaEliminado()) {
            panel.setVisible(false);
            return;
        }

        panel.setVisible(true);
        manoBox.getChildren().clear();

        for (int i = 0; i < cpu.getMano().size(); i++) {
            CardView cardView = crearVistaCarta(null, false);
            cardView.setTamano(71, 95); // Más pequeñas para las CPU
            manoBox.getChildren().add(cardView);
        }
    }
}
