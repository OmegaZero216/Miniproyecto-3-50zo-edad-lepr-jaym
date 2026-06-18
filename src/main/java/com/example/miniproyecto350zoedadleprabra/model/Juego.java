package com.example.miniproyecto350zoedadleprabra.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Juego {
    public static final int MIN_JUGADORES = 2;
    public static final int MAX_JUGADORES = 4;
    public static final int SUMA_MAXIMA = 50;
    public static final int CARTAS_INICIALES = 4;

    private final List<Jugador> jugadores;
    private final Mazo mazo;
    private final List<String> historial;
    private Carta cartaEnMesa;
    private int sumaMesa;
    private int indiceJugadorActual;
    private Jugador ganador;
    private boolean juegoTerminado;

    public Juego(int cantidadJugadores) {
        if (cantidadJugadores < MIN_JUGADORES || cantidadJugadores > MAX_JUGADORES) {
            throw new IllegalArgumentException("La cantidad de jugadores debe estar entre 2 y 4");
        }

        this.jugadores = new ArrayList<>();
        this.mazo = new Mazo();
        this.historial = new ArrayList<>();

        jugadores.add(new Jugador("Jugador", Jugador.Tipo.HUMANO));
        for (int i = 1; i < cantidadJugadores; i++) {
            jugadores.add(new Jugador("CPU " + i, Jugador.Tipo.MAQUINA));
        }
    }

    public void preparar() {
        if (cartaEnMesa != null) {
            throw new IllegalStateException("El juego ya fue preparado");
        }

        for (Jugador jugador : jugadores) {
            jugador.recibirCartas(mazo.tomarCartas(CARTAS_INICIALES));
        }

        cartaEnMesa = mazo.tomarCarta();
        sumaMesa = cartaEnMesa.getMejorValorParaJugar(0);

        historial.add("INICIO: Carta en mesa: " + cartaEnMesa + " (Valor: " + sumaMesa + ")");
    }

    public boolean jugarCarta(Carta carta) {
        if (carta == null || juegoTerminado) {
            return false;
        }

        Jugador jugadorActual = getJugadorActual();
        if (jugadorActual.estaEliminado() || !jugadorActual.getMano().contains(carta)) {
            return false;
        }

        if (!carta.puedeJugarseConMesa(sumaMesa)) {
            return false;
        }

        int valorCarta = carta.getMejorValorParaJugar(sumaMesa);
        int sumaAnterior = sumaMesa;

        jugadorActual.jugarCarta(carta);
        cartaEnMesa = carta;
        sumaMesa += valorCarta;

        historial.add(String.format("%s juega %s (valor: %d) | Mesa: %d -> %d",
                jugadorActual.getNombre(), carta, valorCarta, sumaAnterior, sumaMesa));

        if (sumaMesa == SUMA_MAXIMA) {
            historial.add(jugadorActual.getNombre() + " ha llevado la suma exactamente a 50");
        }

        return true;
    }

    public Carta tomarCartaDelMazo() {
        if (juegoTerminado || mazo.estaVacio()) {
            return null;
        }

        Jugador jugadorActual = getJugadorActual();
        if (jugadorActual.estaEliminado()) {
            return null;
        }

        Carta nuevaCarta = mazo.tomarCarta();
        jugadorActual.recibirCarta(nuevaCarta);
        historial.add(jugadorActual.getNombre() + " toma carta del mazo");
        return nuevaCarta;
    }

    public void verificarEliminacion() {
        Jugador jugadorActual = getJugadorActual();
        if (jugadorActual.debeSerEliminado(sumaMesa)) {
            eliminarJugador(jugadorActual, "sin cartas validas");
        }
    }

    public void eliminarJugador(Jugador jugador) {
        eliminarJugador(jugador, "rendicion");
    }

    public void siguienteTurno() {
        if (juegoTerminado) {
            return;
        }

        actualizarGanadorSiCorresponde();
        if (juegoTerminado) {
            return;
        }

        int intentos = 0;
        do {
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
            intentos++;
            if (intentos > jugadores.size()) {
                juegoTerminado = true;
                return;
            }
        } while (getJugadorActual().estaEliminado());

        historial.add("TURNO: " + getJugadorActual().getNombre());
    }

    public int calcularValorEfectivo() {
        return sumaMesa;
    }

    public String getEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADISTICAS DEL JUEGO ===\n");
        sb.append("Suma actual de la mesa: ").append(sumaMesa).append("\n");
        sb.append("Cartas en mazo: ").append(mazo.cartasRestantes()).append("\n\n");

        for (Jugador jugador : jugadores) {
            sb.append(jugador.getNombre()).append(": ");
            sb.append(jugador.estaEliminado()
                    ? "ELIMINADO"
                    : "En juego - " + jugador.getMano().size() + " cartas");
            sb.append("\n");
        }

        return sb.toString();
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    public Jugador getJugadorActual() {
        return jugadores.get(indiceJugadorActual);
    }

    public int getIndiceJugadorActual() {
        return indiceJugadorActual;
    }

    public Carta getCartaEnMesa() {
        return cartaEnMesa;
    }

    public int getSumaMesa() {
        return sumaMesa;
    }

    public Mazo getMazo() {
        return mazo;
    }

    public boolean hayGanador() {
        return juegoTerminado && ganador != null;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public List<String> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    public boolean esTurnoHumano() {
        return getJugadorActual().getTipo() == Jugador.Tipo.HUMANO;
    }

    public Jugador getJugadorHumano() {
        return jugadores.stream()
                .filter(j -> j.getTipo() == Jugador.Tipo.HUMANO)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No existe jugador humano"));
    }

    private void eliminarJugador(Jugador jugador, String motivo) {
        if (jugador == null || jugador.estaEliminado() || juegoTerminado) {
            return;
        }

        List<Carta> cartasEliminadas = jugador.eliminar();
        mazo.agregarCartas(cartasEliminadas);
        historial.add(jugador.getNombre() + " ha sido eliminado por " + motivo + ". " +
                cartasEliminadas.size() + " cartas devueltas al mazo.");

        actualizarGanadorSiCorresponde();
    }

    private void actualizarGanadorSiCorresponde() {
        if (juegoTerminado) {
            return;
        }

        List<Jugador> vivos = jugadores.stream()
                .filter(j -> !j.estaEliminado())
                .toList();

        if (vivos.size() == 1) {
            ganador = vivos.get(0);
            ganador.incrementarPartidasGanadas();
            juegoTerminado = true;
            historial.add(ganador.getNombre() + " es el ganador");
        } else if (vivos.isEmpty()) {
            juegoTerminado = true;
        }
    }
}
