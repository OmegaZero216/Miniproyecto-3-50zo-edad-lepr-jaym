package com.example.miniproyecto350zoedadleprabra.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Jugador {
    public enum Tipo { HUMANO, MAQUINA }

    private static final Random RANDOM = new Random();

    private final String nombre;
    private final Tipo tipo;
    private final List<Carta> mano;
    private boolean eliminado;
    private int partidasGanadas;
    private boolean estrategiaAgresiva;

    public Jugador(String nombre, Tipo tipo) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.mano = new ArrayList<>();
        this.estrategiaAgresiva = RANDOM.nextBoolean();
    }

    public void recibirCartas(Collection<Carta> cartas) {
        Objects.requireNonNull(cartas, "cartas");
        cartas.forEach(this::recibirCarta);
    }

    public void recibirCarta(Carta carta) {
        mano.add(Objects.requireNonNull(carta, "carta"));
    }

    public boolean puedeJugar(int sumaMesa) {
        return mano.stream().anyMatch(carta -> carta.puedeJugarseConMesa(sumaMesa));
    }

    public List<Carta> getCartasValidas(int sumaMesa) {
        return mano.stream()
                .filter(carta -> carta.puedeJugarseConMesa(sumaMesa))
                .collect(Collectors.toList());
    }

    public Carta seleccionarMejorCarta(int sumaMesa) {
        List<Carta> cartasValidas = getCartasValidas(sumaMesa);
        if (cartasValidas.isEmpty()) {
            return null;
        }

        cartasValidas.sort((c1, c2) -> {
            int valor1 = c1.getMejorValorParaJugar(sumaMesa);
            int valor2 = c2.getMejorValorParaJugar(sumaMesa);

            if (sumaMesa > 40 || !estrategiaAgresiva) {
                return Integer.compare(valor1, valor2);
            }
            return Integer.compare(valor2, valor1);
        });

        Carta cartaSeleccionada = cartasValidas.get(0);
        cartaSeleccionada.getMejorValorParaJugar(sumaMesa);
        return cartaSeleccionada;
    }

    public void jugarCarta(Carta carta) {
        mano.remove(carta);
    }

    public List<Carta> eliminar() {
        eliminado = true;
        List<Carta> cartasEliminadas = new ArrayList<>(mano);
        mano.clear();
        return cartasEliminadas;
    }

    public boolean debeSerEliminado(int sumaMesa) {
        return !eliminado && (mano.isEmpty() || !puedeJugar(sumaMesa));
    }

    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public List<Carta> getMano() {
        return Collections.unmodifiableList(mano);
    }

    public boolean estaEliminado() {
        return eliminado;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }

    public void incrementarPartidasGanadas() {
        partidasGanadas++;
    }

    public boolean isEstrategiaAgresiva() {
        return estrategiaAgresiva;
    }

    public void setEstrategiaAgresiva(boolean agresiva) {
        this.estrategiaAgresiva = agresiva;
    }

    public String getEstadoMano() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(": ");
        for (Carta carta : mano) {
            sb.append(carta.getSimbolo()).append("(").append(carta.getValorJuego()).append(") ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")" + (eliminado ? " [ELIMINADO]" : "");
    }
}
