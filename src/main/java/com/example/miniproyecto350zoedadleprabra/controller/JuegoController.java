package com.example.miniproyecto350zoedadleprabra.controller;

import com.example.miniproyecto350zoedadleprabra.model.Carta;
import com.example.miniproyecto350zoedadleprabra.model.Juego;
import com.example.miniproyecto350zoedadleprabra.model.Jugador;
import com.example.miniproyecto350zoedadleprabra.util.PerfilesManager;
import com.example.miniproyecto350zoedadleprabra.util.RecursosImagen;
import com.example.miniproyecto350zoedadleprabra.view.CardView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JuegoController {
    @FXML private Label lblSumaMesa;
    @FXML private Label lblTurnoActual;
    @FXML private Label lblTiempo;
    @FXML private StackPane panelTemporizador;
    @FXML private ImageView imgContadorVacio;
    @FXML private ImageView imgCartaMesa;
    @FXML private HBox panelManoHumano;
    @FXML private Button btnRendirse;

    @FXML private VBox panelCPU1;
    @FXML private HBox manoCPU1;
    @FXML private Label lblNombreCPU1;
    @FXML private Label lblEstadoCPU1;
    @FXML private ImageView imgFotoCPU1;

    @FXML private VBox panelCPU2;
    @FXML private HBox manoCPU2;
    @FXML private Label lblNombreCPU2;
    @FXML private Label lblEstadoCPU2;
    @FXML private ImageView imgFotoCPU2;

    @FXML private VBox panelCPU3;
    @FXML private HBox manoCPU3;
    @FXML private Label lblNombreCPU3;
    @FXML private Label lblEstadoCPU3;
    @FXML private ImageView imgFotoCPU3;

    private final Map<Jugador, Image> perfilesCPU = new HashMap<>();
    private Juego juego;
    private TurnoManager turnoManager;
    private Timer temporizador;

    @FXML
    private void initialize() {
        ocultarPanelCPU(panelCPU1, manoCPU1);
        ocultarPanelCPU(panelCPU2, manoCPU2);
        ocultarPanelCPU(panelCPU3, manoCPU3);
    }

    public void inicializarJuego(int cantidadJugadores) {
        if (turnoManager != null) {
            turnoManager.detener();
        }

        juego = new Juego(cantidadJugadores);
        juego.preparar();
        turnoManager = new TurnoManager(juego, this::mostrarDialogoGanador);
        temporizador = new Timer(panelTemporizador, lblTiempo, imgContadorVacio);

        configurarCPUIniciales();
        actualizarUI();
        turnoManager.iniciarTurno(this::actualizarUI);
    }

    @FXML
    private void tomarCartaMazo() {
        if (juego == null || juego.isJuegoTerminado()) {
            return;
        }

        if (!juego.esTurnoHumano()) {
            mostrarEstado("Espera el turno de la CPU");
            return;
        }

        mostrarEstado("Juega una carta valida; el robo del mazo es automatico");
    }

    @FXML
    private void rendirse() {
        if (juego == null || juego.isJuegoTerminado()) {
            return;
        }

        Jugador humano = juego.getJugadorHumano();
        juego.eliminarJugador(humano);
        actualizarUI();

        if (juego.hayGanador()) {
            mostrarDialogoGanador(juego.getGanador());
        } else {
            juego.siguienteTurno();
            actualizarUI();
            turnoManager.iniciarTurno(this::actualizarUI);
        }
    }

    private CardView crearVistaCarta(Carta carta, boolean esHumano) {
        CardView cardView = new CardView(esHumano);

        if (esHumano && carta != null) {
            cardView.setCarta(carta);
            cardView.setOnMouseClicked(e -> manejarClickCarta(cardView));
            cardView.setCursor(Cursor.HAND);
            cardView.setOnMouseEntered(e -> {
                if (!cardView.isSeleccionada() && !cardView.isDisabled()) {
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

    private void manejarClickCarta(CardView cardView) {
        if (juego == null || turnoManager == null || !juego.esTurnoHumano()) {
            return;
        }

        Carta carta = cardView.getCarta();
        if (carta == null || !carta.puedeJugarseConMesa(juego.getSumaMesa())) {
            mostrarEstado("Esa carta supera 50 y no puede jugarse");
            return;
        }

        cardView.animarJugada(() -> {
            boolean jugadaValida = turnoManager.jugarHumano(carta, this::actualizarUI);
            if (!jugadaValida) {
                mostrarEstado("No se pudo jugar esa carta");
                actualizarUI();
            }
        });
    }

    private void actualizarUI() {
        if (juego == null) {
            return;
        }

        lblSumaMesa.setText(String.valueOf(juego.getSumaMesa()));
        Jugador actual = juego.getJugadorActual();
        lblTurnoActual.setText(juego.isJuegoTerminado()
                ? "Partida terminada"
                : "Turno: " + actual.getNombre());

        Carta cartaMesa = juego.getCartaEnMesa();
        if (cartaMesa != null) {
            imgCartaMesa.setImage(RecursosImagen.cargarImagenCarta(cartaMesa));
        }

        actualizarManosEnUI();
        actualizarPanelesCPU();
    }

    private void actualizarManosEnUI() {
        panelManoHumano.getChildren().clear();
        Jugador humano = juego.getJugadorHumano();

        for (Carta carta : humano.getMano()) {
            CardView cardView = crearVistaCarta(carta, true);
            boolean jugable = !humano.estaEliminado()
                    && juego.esTurnoHumano()
                    && carta.puedeJugarseConMesa(juego.getSumaMesa());
            cardView.setJugable(jugable);
            cardView.setDisable(!jugable || juego.isJuegoTerminado());
            panelManoHumano.getChildren().add(cardView);
        }

        btnRendirse.setDisable(humano.estaEliminado() || juego.isJuegoTerminado());
    }

    private void configurarCPUIniciales() {
        List<Jugador> jugadores = juego.getJugadores();
        configurarCPU(panelCPU1, manoCPU1, lblNombreCPU1, lblEstadoCPU1, imgFotoCPU1,
                jugadores.size() > 1 ? jugadores.get(1) : null);
        configurarCPU(panelCPU2, manoCPU2, lblNombreCPU2, lblEstadoCPU2, imgFotoCPU2,
                jugadores.size() > 2 ? jugadores.get(2) : null);
        configurarCPU(panelCPU3, manoCPU3, lblNombreCPU3, lblEstadoCPU3, imgFotoCPU3,
                jugadores.size() > 3 ? jugadores.get(3) : null);
    }

    private void configurarCPU(VBox panel, HBox manoBox, Label nombre, Label estado,
                               ImageView foto, Jugador cpu) {
        if (cpu == null) {
            ocultarPanelCPU(panel, manoBox);
            return;
        }

        panel.setVisible(true);
        panel.setManaged(true);
        nombre.setText(cpu.getNombre());
        estado.setText("");
        Image perfil = perfilesCPU.computeIfAbsent(cpu, jugador -> PerfilesManager.obtenerPerfilAleatorioCPU());
        foto.setImage(perfil);
        actualizarManoCPU(panel, manoBox, estado, cpu);
    }

    private void actualizarPanelesCPU() {
        List<Jugador> jugadores = juego.getJugadores();
        actualizarCPU(panelCPU1, manoCPU1, lblEstadoCPU1, jugadores.size() > 1 ? jugadores.get(1) : null);
        actualizarCPU(panelCPU2, manoCPU2, lblEstadoCPU2, jugadores.size() > 2 ? jugadores.get(2) : null);
        actualizarCPU(panelCPU3, manoCPU3, lblEstadoCPU3, jugadores.size() > 3 ? jugadores.get(3) : null);
    }

    private void actualizarCPU(VBox panel, HBox manoBox, Label estado, Jugador cpu) {
        if (cpu == null) {
            ocultarPanelCPU(panel, manoBox);
            return;
        }

        panel.setVisible(true);
        panel.setManaged(true);
        actualizarManoCPU(panel, manoBox, estado, cpu);
    }

    private void actualizarManoCPU(VBox panel, HBox manoBox, Label estado, Jugador cpu) {
        manoBox.getChildren().clear();

        if (cpu.estaEliminado()) {
            panel.setOpacity(0.45);
            estado.setText("ELIMINADO");
            return;
        }

        panel.setOpacity(1.0);
        estado.setText(juego.getJugadorActual() == cpu ? "TURNO" : cpu.getMano().size() + " cartas");
        for (int i = 0; i < cpu.getMano().size(); i++) {
            CardView cardView = crearVistaCarta(null, false);
            cardView.setTamano(71, 95);
            manoBox.getChildren().add(cardView);
        }
    }

    private void ocultarPanelCPU(VBox panel, HBox manoBox) {
        if (panel != null) {
            panel.setVisible(false);
            panel.setManaged(false);
        }
        if (manoBox != null) {
            manoBox.getChildren().clear();
        }
    }

    private void mostrarEstado(String mensaje) {
        lblTurnoActual.setText(mensaje);
    }

    private void mostrarDialogoGanador(Jugador ganador) {
        if (turnoManager != null) {
            turnoManager.detener();
        }
        if (temporizador != null) {
            temporizador.detener();
        }

        Platform.runLater(() -> {
            actualizarUI();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Partida terminada");
            alert.setHeaderText("Ganador: " + (ganador == null ? "Sin ganador" : ganador.getNombre()));
            alert.setContentText("La partida ha finalizado.");
            alert.showAndWait();
        });
    }
}
