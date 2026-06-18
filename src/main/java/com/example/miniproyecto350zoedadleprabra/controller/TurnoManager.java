package com.example.miniproyecto350zoedadleprabra.controller;

public class TurnoManager {
    private final Juego juego;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private boolean turnoEnProgreso = false;

    public TurnoManager(Juego juego) {
        this.juego = juego;
    }

    public void iniciarTurno(Runnable actualizarUI) {
        if (turnoEnProgreso || juego.hayGanador()) return;
        turnoEnProgreso = true;

        Jugador actual = juego.getJugadorActual();
        if (actual.getTipo() == Jugador.Tipo.HUMANO) {
            // Esperar input del usuario → se llamará a jugarHumano() desde controlador
            turnoEnProgreso = false; // se libera hasta que el humano juegue
        } else {
            // Máquina: simula tiempo de "pensar"
            int esperaJugar = ThreadLocalRandom.current().nextInt(2000, 4001);
            executor.schedule(() -> {
                Carta carta = actual.seleccionarCartaValida(juego.getSumaMesa());
                Platform.runLater(() -> {
                    juego.jugarCarta(carta);
                    actualizarUI.run(); // refrescar mesa y mano

                    // Después de jugar, toma carta (1-2 seg)
                    int esperaTomar = ThreadLocalRandom.current().nextInt(1000, 2001);
                    executor.schedule(() -> {
                        Platform.runLater(() -> {
                            juego.tomarCarta();
                            juego.verificarEliminacion();
                            actualizarUI.run();
                            if (juego.hayGanador()) {
                                Platform.runLater(() -> mostrarDialogoGanador(juego.getGanador()));
                            } else {
                                juego.finalizarTurno();
                                actualizarUI.run();
                                turnoEnProgreso = false;
                                iniciarTurno(actualizarUI); // siguiente turno
                            }
                        });
                    }, esperaTomar, TimeUnit.MILLISECONDS);
                });
            }, esperaJugar, TimeUnit.MILLISECONDS);
        }
    }

    public void jugarHumano(Carta carta, Runnable actualizarUI) {
        if (juego.jugarCarta(carta)) {
            actualizarUI.run();
            // Igual que la máquina: tomar carta, eliminar si necesario, pasar turno
            juego.tomarCarta();
            juego.verificarEliminacion();
            actualizarUI.run();
            if (juego.hayGanador()) {
                mostrarDialogoGanador(juego.getGanador());
            } else {
                juego.finalizarTurno();
                actualizarUI.run();
                turnoEnProgreso = false;
                iniciarTurno(actualizarUI);
            }
        } else {
            // Carta inválida, UI muestra mensaje (no pasa turno)
        }
    }

    public void detener() { executor.shutdownNow(); }
}