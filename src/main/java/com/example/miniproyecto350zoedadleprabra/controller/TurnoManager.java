package com.example.miniproyecto350zoedadleprabra.controller;

import com.example.miniproyecto350zoedadleprabra.model.Carta;
import com.example.miniproyecto350zoedadleprabra.model.Juego;
import com.example.miniproyecto350zoedadleprabra.model.Jugador;
import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TurnoManager {
    private final Juego juego;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Consumer<Jugador> alGanar;
    private volatile boolean turnoEnProgreso;

    public TurnoManager(Juego juego, Consumer<Jugador> alGanar) {
        this.juego = Objects.requireNonNull(juego, "juego");
        this.alGanar = alGanar;
    }

    public void iniciarTurno(Runnable actualizarUI) {
        if (turnoEnProgreso || juego.isJuegoTerminado()) {
            return;
        }

        Jugador actual = juego.getJugadorActual();
        if (actual.getTipo() == Jugador.Tipo.HUMANO) {
            manejarJugadorSinCartasValidas(actualizarUI);
            return;
        }

        turnoEnProgreso = true;
        int esperaJugar = ThreadLocalRandom.current().nextInt(800, 1601);
        executor.schedule(() -> Platform.runLater(() -> jugarTurnoMaquina(actualizarUI)),
                esperaJugar, TimeUnit.MILLISECONDS);
    }

    public boolean jugarHumano(Carta carta, Runnable actualizarUI) {
        if (juego.isJuegoTerminado() || !juego.esTurnoHumano() || !juego.jugarCarta(carta)) {
            return false;
        }

        finalizarJugada(actualizarUI);
        if (!juego.isJuegoTerminado()) {
            iniciarTurno(actualizarUI);
        }
        return true;
    }

    private void jugarTurnoMaquina(Runnable actualizarUI) {
        try {
            Jugador actual = juego.getJugadorActual();
            if (actual.getTipo() != Jugador.Tipo.MAQUINA || juego.isJuegoTerminado()) {
                return;
            }

            Carta carta = actual.seleccionarMejorCarta(juego.getSumaMesa());
            if (carta == null) {
                juego.verificarEliminacion();
                avanzarDespuesDeTurno(actualizarUI);
                return;
            }

            juego.jugarCarta(carta);
            finalizarJugada(actualizarUI);
        } finally {
            turnoEnProgreso = false;
            if (!juego.isJuegoTerminado()) {
                iniciarTurno(actualizarUI);
            }
        }
    }

    private void finalizarJugada(Runnable actualizarUI) {
        juego.tomarCartaDelMazo();
        juego.verificarEliminacion();
        avanzarDespuesDeTurno(actualizarUI);
    }

    private void avanzarDespuesDeTurno(Runnable actualizarUI) {
        actualizarUI.run();
        if (juego.hayGanador()) {
            notificarGanador();
            return;
        }

        juego.siguienteTurno();
        actualizarUI.run();
        if (juego.hayGanador()) {
            notificarGanador();
        }
    }

    private void manejarJugadorSinCartasValidas(Runnable actualizarUI) {
        Jugador actual = juego.getJugadorActual();
        if (!actual.puedeJugar(juego.getSumaMesa())) {
            juego.verificarEliminacion();
            avanzarDespuesDeTurno(actualizarUI);
            if (!juego.isJuegoTerminado()) {
                iniciarTurno(actualizarUI);
            }
        }
    }

    private void notificarGanador() {
        if (alGanar != null) {
            alGanar.accept(juego.getGanador());
        }
    }

    public void detener() {
        executor.shutdownNow();
    }
}
