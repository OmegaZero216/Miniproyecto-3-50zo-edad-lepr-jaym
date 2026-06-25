package com.example.miniproyecto350zoedadleprabra.model;

import java.util.Objects;

public class Carta {
    public enum Palo {
        CORAZONES, DIAMANTES, TREBOLES, PICAS
    }

    public enum ValorCarta {
        A(1, "A", true),    // El A puede valer 1 o 10
        DOS(2, "2", false),
        TRES(3, "3", false),
        CUATRO(4, "4", false),
        CINCO(5, "5", false),
        SEIS(6, "6", false),
        SIETE(7, "7", false),
        OCHO(8, "8", false),
        NUEVE(9, "9", false), // El 9 es especial: suma 0
        DIEZ(10, "10", false),
        J(11, "J", false),   // Resta 10
        Q(12, "Q", false),   // Resta 10
        K(13, "K", false);   // Resta 10

        private final int valorNumerico;
        private final String simbolo;
        private final boolean esAs;

        ValorCarta(int valorNumerico, String simbolo, boolean esAs) {
            this.valorNumerico = valorNumerico;
            this.simbolo = simbolo;
            this.esAs = esAs;
        }

        public int getValorNumerico() { return valorNumerico; }
        public String getSimbolo() { return simbolo; }
        public boolean esAs() { return esAs; }
        public boolean esFigura() { return this == J || this == Q || this == K; }
        public boolean esNueve() { return this == NUEVE; }
    }

    private final ValorCarta valorCarta;
    private final Palo palo;

    // Para el As, guardamos si está usando 1 o 10
    private boolean asUsandoValorAlto = true; // Por defecto usa 10

    public Carta(ValorCarta valorCarta, Palo palo) {
        this.valorCarta = Objects.requireNonNull(valorCarta);
        this.palo = Objects.requireNonNull(palo);
    }

    /**
     * Calcula el valor real de juego de la carta según las reglas:
     * - Cartas 2-8 y 10: suman su valor
     * - J, Q, K: restan 10
     * - 9: suma 0 (ni suma ni resta)
     * - A: suma 1 o 10 según convenga
     */
    public int getValorJuego() {
        if (valorCarta == ValorCarta.NUEVE) {
            return 0;
        }
        if (valorCarta == ValorCarta.DIEZ) {
            return 10;
        }
        if (valorCarta.esFigura()) {
            return -10;
        }
        if (valorCarta.esAs()) {
            return asUsandoValorAlto ? 10 : 1;
        }
        // Cartas 2-8
        return valorCarta.getValorNumerico();
    }

    /**
     * Para el As, verifica si puede jugarse con ambos valores
     * Retorna los valores posibles que puede tomar esta carta
     */
    public int[] getValoresPosibles() {
        if (valorCarta.esAs()) {
            return new int[]{1, 10};
        }
        return new int[]{getValorJuego()};
    }

    /**
     * Alterna el valor del As entre 1 y 10
     */
    public void alternarValorAs() {
        if (valorCarta.esAs()) {
            asUsandoValorAlto = !asUsandoValorAlto;
        }
    }

    /**
     * Configura el valor del As manualmente
     */
    public void setAsValorAlto(boolean alto) {
        if (valorCarta.esAs()) {
            this.asUsandoValorAlto = alto;
        }
    }

    /**
     * Verifica si esta carta puede jugarse dado el valor actual de la mesa
     * sin exceder 50
     */
    public boolean puedeJugarseConMesa(int sumaMesa) {
        for (int valorPosible : getValoresPosibles()) {
            if (sumaMesa + valorPosible <= 50) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el mejor valor para jugar esta carta dada la suma actual
     * Prioriza no exceder 50
     */
    public int getMejorValorParaJugar(int sumaMesa) {
        if (valorCarta.esAs()) {
            // Si con 10 no excede, usar 10; si no, usar 1
            if (sumaMesa + 10 <= 50) {
                asUsandoValorAlto = true;
                return 10;
            } else {
                asUsandoValorAlto = false;
                return 1;
            }
        }
        return getValorJuego();
    }

    // Getters
    public ValorCarta getValorCarta() { return valorCarta; }
    public Palo getPalo() { return palo; }
    public String getSimbolo() { return valorCarta.getSimbolo(); }
    public boolean esAs() { return valorCarta.esAs(); }
    public boolean estaUsandoValorAlto() { return asUsandoValorAlto; }

    /**
     * Obtiene el nombre del archivo de imagen para esta carta
     */
    public String getNombreImagen() {
        return switch (palo) {
            case CORAZONES -> "C";
            case DIAMANTES -> "D";
            case PICAS -> "P";
            case TREBOLES -> "T";
        } + valorCarta.getSimbolo() + ".png";
    }

    @Override
    public String toString() {
        return valorCarta.getSimbolo() + " de " + palo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carta)) return false;
        Carta carta = (Carta) o;
        return valorCarta == carta.valorCarta && palo == carta.palo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valorCarta, palo);
    }
}
