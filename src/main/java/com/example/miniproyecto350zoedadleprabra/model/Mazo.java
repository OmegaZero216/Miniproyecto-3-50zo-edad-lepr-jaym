package com.example.miniproyecto350zoedadleprabra.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class Mazo {
    private static final int CARTAS_INICIALES_POR_JUGADOR = 4;

    private final Stack<Carta> cartas;

    public Mazo() {
        this.cartas = new Stack<>();
        inicializarBarajaCompleta();
    }

    private void inicializarBarajaCompleta() {
        List<Carta> baraja = new ArrayList<>();

        for (Carta.Palo palo : Carta.Palo.values()) {
            for (Carta.ValorCarta valor : Carta.ValorCarta.values()) {
                baraja.add(new Carta(valor, palo));
            }
        }

        Collections.shuffle(baraja, new Random());
        cartas.addAll(baraja);
    }

    public Carta tomarCarta() {
        if (cartas.isEmpty()) {
            throw new MazoVacioException("El mazo se ha quedado sin cartas");
        }
        return cartas.pop();
    }

    public List<Carta> tomarCartas(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de cartas no puede ser negativa");
        }
        if (cantidad > cartas.size()) {
            throw new MazoVacioException(
                    String.format("No hay suficientes cartas. Solicitadas: %d, Disponibles: %d",
                            cantidad, cartas.size()));
        }

        List<Carta> cartasTomadas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            cartasTomadas.add(cartas.pop());
        }
        return cartasTomadas;
    }

    public void agregarCartas(Collection<Carta> cartasAgregar) {
        Objects.requireNonNull(cartasAgregar, "cartasAgregar");
        cartas.addAll(cartasAgregar);
        Collections.shuffle(cartas, new Random());
    }

    public void agregarCarta(Carta carta) {
        cartas.push(Objects.requireNonNull(carta, "carta"));
    }

    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    public int cartasRestantes() {
        return cartas.size();
    }

    public void reiniciar() {
        cartas.clear();
        inicializarBarajaCompleta();
    }

    public Map<Integer, List<Carta>> repartirManosIniciales(int cantidadJugadores) {
        if (cantidadJugadores < 0) {
            throw new IllegalArgumentException("La cantidad de jugadores no puede ser negativa");
        }

        Map<Integer, List<Carta>> manos = new HashMap<>();
        for (int i = 0; i < cantidadJugadores; i++) {
            manos.put(i, tomarCartas(CARTAS_INICIALES_POR_JUGADOR));
        }
        return manos;
    }

    @Override
    public String toString() {
        return "Mazo{" + cartasRestantes() + " cartas restantes}";
    }
}
