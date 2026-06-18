package com.example.miniproyecto350zoedadleprabra.model;

import java.util.*;

public class Juego {
    public static final int SUMA_MAXIMA = 50;
    public static final int CARTAS_INICIALES = 4;

    private final List<Jugador> jugadores;
    private final Mazo mazo;
    private Carta cartaEnMesa;
    private int sumaMesa;
    private int indiceJugadorActual;
    private Jugador ganador;
    private boolean juegoTerminado;

    // Historial de jugadas (para análisis)
    private final List<String> historial;

    public Juego(int cantidadMaquinas) {
        this.jugadores = new ArrayList<>();
        this.mazo = new Mazo();
        this.historial = new ArrayList<>();
        this.juegoTerminado = false;

        // Crear jugadores
        jugadores.add(new Jugador("Jugador", Jugador.Tipo.HUMANO));
        for (int i = 1; i <= cantidadMaquinas; i++) {
            jugadores.add(new Jugador("CPU " + i, Jugador.Tipo.MAQUINA));
        }

        this.indiceJugadorActual = 0;
    }

    /**
     * Prepara el juego inicial
     */
    public void preparar() {
        // Repartir 4 cartas a cada jugador
        for (Jugador jugador : jugadores) {
            jugador.recibirCartas(mazo.tomarCartas(CARTAS_INICIALES));
        }

        // Poner primera carta en la mesa
        cartaEnMesa = mazo.tomarCarta();
        sumaMesa = cartaEnMesa.getValorJuego();

        // Si es un As, decidir su valor inicial
        if (cartaEnMesa.esAs()) {
            cartaEnMesa.getMejorValorParaJugar(0);
            sumaMesa = cartaEnMesa.getValorJuego();
        }

        historial.add("INICIO: Carta en mesa: " + cartaEnMesa + " (Valor: " + sumaMesa + ")");
    }

    /**
     * Juega una carta del jugador actual
     * @return true si la jugada fue válida
     */
    public boolean jugarCarta(Carta carta) {
        Jugador jugadorActual = getJugadorActual();

        if (jugadorActual.estaEliminado()) {
            return false;
        }

        // Verificar si la carta está en la mano del jugador
        if (!jugadorActual.getMano().contains(carta)) {
            return false;
        }

        // Verificar si la carta se puede jugar (no excede 50)
        if (!carta.puedeJugarseConMesa(sumaMesa)) {
            return false;
        }

        // Para As, elegir el mejor valor
        if (carta.esAs()) {
            carta.getMejorValorParaJugar(sumaMesa);
        }

        int valorCarta = carta.getValorJuego();
        int sumaAnterior = sumaMesa;

        // Jugar la carta
        jugadorActual.jugarCarta(carta);
        cartaEnMesa = carta;
        sumaMesa += valorCarta;

        // Registrar en historial
        historial.add(String.format("%s juega %s (valor: %d) | Mesa: %d → %d",
                jugadorActual.getNombre(), carta, valorCarta, sumaAnterior, sumaMesa));

        // Verificar si la suma llega exactamente a 50 (jugada especial)
        if (sumaMesa == SUMA_MAXIMA) {
            historial.add("¡" + jugadorActual.getNombre() + " ha llevado la suma exactamente a 50!");
        }

        return true;
    }

    /**
     * El jugador actual toma una carta del mazo
     */
    public Carta tomarCartaDelMazo() {
        if (mazo.estaVacio()) {
            return null;
        }

        Jugador jugadorActual = getJugadorActual();
        Carta nuevaCarta = mazo.tomarCarta();
        jugadorActual.recibirCarta(nuevaCarta);

        historial.add(jugadorActual.getNombre() + " toma carta del mazo: " + nuevaCarta);

        return nuevaCarta;
    }

    /**
     * Verifica y procesa eliminación del jugador actual
     */
    public void verificarEliminacion() {
        Jugador jugadorActual = getJugadorActual();

        if (jugadorActual.debeSerEliminado(sumaMesa)) {
            List<Carta> cartasEliminadas = jugadorActual.eliminar();
            mazo.agregarCartas(cartasEliminadas);

            historial.add(jugadorActual.getNombre() + " ha sido ELIMINADO. " +
                    cartasEliminadas.size() + " cartas devueltas al mazo.");

            // Verificar si hay ganador
            long jugadoresVivos = jugadores.stream()
                    .filter(j -> !j.estaEliminado())
                    .count();

            if (jugadoresVivos == 1) {
                ganador = jugadores.stream()
                        .filter(j -> !j.estaEliminado())
                        .findFirst()
                        .orElse(null);
                juegoTerminado = true;

                if (ganador != null) {
                    ganador.incrementarPartidasGanadas();
                    historial.add("¡" + ganador.getNombre() + " es el GANADOR!");
                }
            }
        }
    }

    /**
     * Avanza al siguiente jugador no eliminado
     */
    public void siguienteTurno() {
        if (juegoTerminado) return;

        int intentos = 0;
        do {
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
            intentos++;

            // Evitar bucle infinito si todos están eliminados
            if (intentos > jugadores.size()) {
                juegoTerminado = true;
                return;
            }
        } while (getJugadorActual().estaEliminado());

        historial.add("TURNO: " + getJugadorActual().getNombre());
    }

    /**
     * Calcula el valor efectivo de la suma de la mesa
     * teniendo en cuenta cartas especiales
     */
    public int calcularValorEfectivo() {
        return sumaMesa;
    }

    /**
     * Obtiene estadísticas del juego
     */
    public String getEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL JUEGO ===\n");
        sb.append("Suma actual de la mesa: ").append(sumaMesa).append("\n");
        sb.append("Cartas en mazo: ").append(mazo.cartasRestantes()).append("\n\n");

        for (Jugador j : jugadores) {
            sb.append(j.getNombre()).append(": ");
            if (j.estaEliminado()) {
                sb.append("ELIMINADO");
            } else {
                sb.append("En juego - ").append(j.getMano().size()).append(" cartas");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getters
    public List<Jugador> getJugadores() { return Collections.unmodifiableList(jugadores); }
    public Jugador getJugadorActual() { return jugadores.get(indiceJugadorActual); }
    public int getIndiceJugadorActual() { return indiceJugadorActual; }
    public Carta getCartaEnMesa() { return cartaEnMesa; }
    public int getSumaMesa() { return sumaMesa; }
    public Mazo getMazo() { return mazo; }
    public boolean hayGanador() { return juegoTerminado && ganador != null; }
    public Jugador getGanador() { return ganador; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public List<String> getHistorial() { return Collections.unmodifiableList(historial); }

    /**
     * Verifica si es turno de un jugador humano
     */
    public boolean esTurnoHumano() {
        return getJugadorActual().getTipo() == Jugador.Tipo.HUMANO;
    }

    /**
     * Obtiene el jugador humano
     */
    public Jugador getJugadorHumano() {
        return jugadores.stream()
                .filter(j -> j.getTipo() == Jugador.Tipo.HUMANO)
                .findFirst()
                .orElse(null);
    }
}