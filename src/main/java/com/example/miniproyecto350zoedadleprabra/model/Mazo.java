package com.example.miniproyecto350zoedadleprabra.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import java.util.*;
import java.util.stream.Collectors;

public class Mazo {
    private final Stack<Carta> cartas;
    private static final int CARTAS_INICIALES_POR_JUGADOR = 4;

    public Mazo() {
        this.cartas = new Stack<>();
        inicializarBarajaCompleta();
    }

    /**
     * Crea una baraja francesa completa de 52 cartas:
     * 4 palos × 13 valores (A, 2-10, J, Q, K)
     */
    private void inicializarBarajaCompleta() {
        List<Carta> baraja = new ArrayList<>();

        for (Carta.Palo palo : Carta.Palo.values()) {
            for (Carta.ValorCarta valor : Carta.ValorCarta.values()) {
                baraja.add(new Carta(valor, palo));
            }
        }

        // Barajar
        Collections.shuffle(baraja, new Random());
        cartas.addAll(baraja);
    }

    /**
     * Toma una carta del tope del mazo
     * @throws MazoVacioException si el mazo está vacío
     */
    public Carta tomarCarta() {
        if (cartas.isEmpty()) {
            throw new MazoVacioException("El mazo se ha quedado sin cartas");
        }
        return cartas.pop();
    }

    /**
     * Toma múltiples cartas del mazo
     */
    public List<Carta> tomarCartas(int cantidad) {
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

    /**
     * Agrega cartas al mazo y lo vuelve a barajar
     */
    public void agregarCartas(Collection<Carta> cartasAEgregar) {
        cartas.addAll(cartasAEgregar);
        Collections.shuffle(cartas, new Random());
    }

    /**
     * Agrega una sola carta al mazo
     */
    public void agregarCarta(Carta carta) {
        cartas.push(carta);
    }

    /**
     * Verifica si el mazo está vacío
     */
    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    /**
     * Obtiene la cantidad de cartas restantes
     */
    public int cartasRestantes() {
        return cartas.size();
    }

    /**
     * Reinicia el mazo con una baraja nueva
     */
    public void reiniciar() {
        cartas.clear();
        inicializarBarajaCompleta();
    }

    /**
     * Prepara las manos iniciales para todos los jugadores
     */
    public Map<Integer, List<Carta>> repartirManosIniciales(int cantidadJugadores) {
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