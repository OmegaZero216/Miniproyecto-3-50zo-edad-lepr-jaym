package com.example.miniproyecto350zoedadleprabra.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartaTest {
    @Test
    void cartaNormalSumaSuValor() {
        Carta carta = new Carta(Carta.ValorCarta.CINCO, Carta.Palo.CORAZONES);
        assertEquals(5, carta.getValorJuego());
    }

    @Test
    void cartaDiezSumaDiez() {
        Carta carta = new Carta(Carta.ValorCarta.DIEZ, Carta.Palo.TREBOLES);
        assertEquals(10, carta.getValorJuego());
    }

    @Test
    void cartaNueveSumaCero() {
        Carta carta = new Carta(Carta.ValorCarta.NUEVE, Carta.Palo.PICAS);
        assertEquals(0, carta.getValorJuego());
    }

    @Test
    void figurasRestanDiez() {
        assertEquals(-10, new Carta(Carta.ValorCarta.J, Carta.Palo.DIAMANTES).getValorJuego());
        assertEquals(-10, new Carta(Carta.ValorCarta.Q, Carta.Palo.CORAZONES).getValorJuego());
        assertEquals(-10, new Carta(Carta.ValorCarta.K, Carta.Palo.TREBOLES).getValorJuego());
    }

    @Test
    void asPuedeValerUnoODiez() {
        Carta as = new Carta(Carta.ValorCarta.A, Carta.Palo.PICAS);
        assertEquals(10, as.getValorJuego());

        as.setAsValorAlto(false);
        assertEquals(1, as.getValorJuego());

        as.alternarValorAs();
        assertEquals(10, as.getValorJuego());
    }

    @Test
    void asEligeMejorValorSegunMesa() {
        Carta as = new Carta(Carta.ValorCarta.A, Carta.Palo.CORAZONES);

        assertEquals(1, as.getMejorValorParaJugar(45));
        assertEquals(10, as.getMejorValorParaJugar(30));
    }

    @Test
    void cartaPuedeJugarseSiNoSuperaCincuenta() {
        Carta ocho = new Carta(Carta.ValorCarta.OCHO, Carta.Palo.PICAS);

        assertTrue(ocho.puedeJugarseConMesa(40));
        assertFalse(ocho.puedeJugarseConMesa(45));
    }

    @Test
    void nombreImagenUsaConvencionDeRecursos() {
        Carta carta = new Carta(Carta.ValorCarta.K, Carta.Palo.DIAMANTES);
        assertEquals("DK.png", carta.getNombreImagen());
    }
}
