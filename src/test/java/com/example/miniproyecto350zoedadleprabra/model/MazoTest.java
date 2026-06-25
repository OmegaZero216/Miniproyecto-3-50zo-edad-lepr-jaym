package com.example.miniproyecto350zoedadleprabra.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MazoTest {
    @Test
    void mazoInicialTiene52Cartas() {
        Mazo mazo = new Mazo();
        assertEquals(52, mazo.cartasRestantes());
    }

    @Test
    void tomarCartaReduceElMazo() {
        Mazo mazo = new Mazo();
        mazo.tomarCarta();
        assertEquals(51, mazo.cartasRestantes());
    }

    @Test
    void repartirManosInicialesEntregaCuatroCartasPorJugador() {
        Mazo mazo = new Mazo();
        Map<Integer, List<Carta>> manos = mazo.repartirManosIniciales(4);

        assertEquals(4, manos.size());
        manos.values().forEach(mano -> assertEquals(4, mano.size()));
        assertEquals(52 - 16, mazo.cartasRestantes());
    }

    @Test
    void mazoVacioLanzaExcepcion() {
        Mazo mazo = new Mazo();
        for (int i = 0; i < 52; i++) {
            mazo.tomarCarta();
        }

        assertThrows(MazoVacioException.class, mazo::tomarCarta);
    }

    @Test
    void agregarCartasDevuelveCartasAlMazo() {
        Mazo mazo = new Mazo();
        List<Carta> cartas = new ArrayList<>();
        cartas.add(new Carta(Carta.ValorCarta.A, Carta.Palo.CORAZONES));
        cartas.add(new Carta(Carta.ValorCarta.K, Carta.Palo.PICAS));

        mazo.agregarCartas(cartas);

        assertFalse(mazo.estaVacio());
        assertEquals(54, mazo.cartasRestantes());
    }
}
