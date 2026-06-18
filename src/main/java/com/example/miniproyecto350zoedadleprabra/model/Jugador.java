package com.example.miniproyecto350zoedadleprabra.model;


import java.util.*;
import java.util.stream.Collectors;

public class Jugador {
    public enum Tipo { HUMANO, MAQUINA }

    private final String nombre;
    private final Tipo tipo;
    private final List<Carta> mano;
    private boolean eliminado;
    private int partidasGanadas;

    // Para la IA: preferencias de estrategia
    private boolean estrategiaAgresiva; // true = prefiere sumar, false = prefiere restar

    public Jugador(String nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.mano = new ArrayList<>();
        this.eliminado = false;
        this.partidasGanadas = 0;
        this.estrategiaAgresiva = new Random().nextBoolean();
    }

    /**
     * Recibe cartas iniciales o del mazo
     */
    public void recibirCartas(Collection<Carta> cartas) {
        mano.addAll(cartas);
    }

    public void recibirCarta(Carta carta) {
        mano.add(carta);
    }

    /**
     * Verifica si el jugador tiene al menos una carta que pueda jugar
     * sin exceder 50
     */
    public boolean puedeJugar(int sumaMesa) {
        return mano.stream().anyMatch(carta -> carta.puedeJugarseConMesa(sumaMesa));
    }

    /**
     * Encuentra todas las cartas válidas para jugar
     */
    public List<Carta> getCartasValidas(int sumaMesa) {
        return mano.stream()
                .filter(carta -> carta.puedeJugarseConMesa(sumaMesa))
                .collect(Collectors.toList());
    }

    /**
     * IA mejorada: selecciona la mejor carta según estrategia
     */
    public Carta seleccionarMejorCarta(int sumaMesa) {
        List<Carta> cartasValidas = getCartasValidas(sumaMesa);

        if (cartasValidas.isEmpty()) {
            return null;
        }

        // Ordenar por "conveniencia" según estrategia y estado del juego
        cartasValidas.sort((c1, c2) -> {
            int valor1 = c1.getMejorValorParaJugar(sumaMesa);
            int valor2 = c2.getMejorValorParaJugar(sumaMesa);

            if (estrategiaAgresiva) {
                // Agresiva: prefiere cartas que sumen más
                return Integer.compare(valor2, valor1);
            } else {
                // Conservadora: prefiere cartas que resten o sumen poco
                return Integer.compare(valor1, valor2);
            }
        });

        // Si la suma está muy alta (>40), cambiar a estrategia conservadora
        if (sumaMesa > 40) {
            cartasValidas.sort((c1, c2) -> {
                int valor1 = c1.getMejorValorParaJugar(sumaMesa);
                int valor2 = c2.getMejorValorParaJugar(sumaMesa);
                return Integer.compare(valor1, valor2); // Prefiere valores bajos/negativos
            });
        }

        Carta cartaSeleccionada = cartasValidas.get(0);

        // Si es un As, configurar el mejor valor
        if (cartaSeleccionada.esAs()) {
            cartaSeleccionada.getMejorValorParaJugar(sumaMesa);
        }

        return cartaSeleccionada;
    }

    /**
     * Juega una carta (la remueve de la mano)
     */
    public void jugarCarta(Carta carta) {
        mano.remove(carta);
    }

    /**
     * Elimina al jugador y retorna sus cartas para devolver al mazo
     */
    public List<Carta> eliminar() {
        eliminado = true;
        List<Carta> cartasAEliminar = new ArrayList<>(mano);
        mano.clear();
        return cartasAEliminar;
    }

    /**
     * Verifica si el jugador debe ser eliminado
     */
    public boolean debeSerEliminado(int sumaMesa) {
        return mano.isEmpty() || !puedeJugar(sumaMesa);
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public Tipo getTipo() { return tipo; }
    public List<Carta> getMano() { return Collections.unmodifiableList(mano); }
    public boolean estaEliminado() { return eliminado; }
    public int getPartidasGanadas() { return partidasGanadas; }
    public void incrementarPartidasGanadas() { partidasGanadas++; }
    public boolean isEstrategiaAgresiva() { return estrategiaAgresiva; }
    public void setEstrategiaAgresiva(boolean agresiva) { this.estrategiaAgresiva = agresiva; }

    /**
     * Obtiene la puntuación actual de la mano (para debugging)
     */
    public String getEstadoMano() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(": ");
        for (Carta c : mano) {
            sb.append(c.getSimbolo()).append("(").append(c.getValorJuego()).append(") ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")" + (eliminado ? " [ELIMINADO]" : "");
    }
}